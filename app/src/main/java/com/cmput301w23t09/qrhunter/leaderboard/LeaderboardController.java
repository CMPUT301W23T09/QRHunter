package com.cmput301w23t09.qrhunter.leaderboard;

import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.profile.ProfileFragment;
import com.cmput301w23t09.qrhunter.qrcode.DeleteQRCodeFragment;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeFragment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

public class LeaderboardController {
  private final GameController gameController;

  public LeaderboardController(GameController gameController) {
    this.gameController = gameController;
  }

  /**
   * Retrieve the total points leaderboard in descending order.
   *
   * @param callback callback to call with leaderboard
   */
  public void getTotalPointsLeaderboard(
      BiConsumer<Exception, Leaderboard<PlayerLeaderboardEntry>> callback) {
    PlayerDatabase.getInstance()
        .getAllPlayers(
            task -> {
              // For each player, run the database task to get their QRCodes
              if (!task.isSuccessful()) {
                callback.accept(task.getException(), null);
                return;
              }

              // Map each player to their total qr code hash scores.
              List<PlayerLeaderboardEntry> entries = new ArrayList<>();
              AtomicInteger entriesLeft = new AtomicInteger(task.getData().size());
              AtomicReference<Exception> exception = new AtomicReference<>();

              for (Player player : task.getData()) {
                // Fetch the QRCodes for each player and sum them up.
                QRCodeDatabase.getInstance()
                    .getQRCodeHashes(
                        player.getQRCodeHashes(),
                        qrCodeHashesTask -> {
                          if (qrCodeHashesTask.isSuccessful()) {
                            long score =
                                qrCodeHashesTask.getData().stream()
                                    .mapToLong(QRCode::getScore)
                                    .reduce(0, Long::sum);

                            // Add new player leaderboard entry
                            entries.add(new PlayerLeaderboardEntry(player, score, "points"));
                          } else {
                            exception.set(qrCodeHashesTask.getException());
                          }

                          // On completion of all queries, return the data.
                          if (entriesLeft.decrementAndGet() == 0) {
                            // If there was an exception at any point return null.
                            if (exception.get() != null) {
                              callback.accept(exception.get(), null);
                              return;
                            }

                            // entries now contains all players and their scores.
                            Collections.sort(entries);
                            callback.accept(null, new Leaderboard<>(entries));
                          }
                        });
              }
            });
  }

  /**
   * Retrieve the top scans leaderboard in descending order.
   *
   * @param callback callback to call with leaderboard
   */
  public void getTopScansLeaderboard(
      BiConsumer<Exception, Leaderboard<PlayerLeaderboardEntry>> callback) {
    PlayerDatabase.getInstance()
        .getAllPlayers(
            task -> {
              if (!task.isSuccessful()) {
                callback.accept(task.getException(), null);
                return;
              }

              List<PlayerLeaderboardEntry> entries = new ArrayList<>();
              for (Player player : task.getData()) {
                long scans = player.getQRCodeHashes().size();
                entries.add(new PlayerLeaderboardEntry(player, scans, "codes"));
              }

              Collections.sort(entries);
              callback.accept(null, new Leaderboard<>(entries));
            });
  }

  /**
   * Retrieve the top QRCodes points value leaderboard in descending order.
   *
   * @param callback callback to call with leaderboard
   */
  public void getTopQRCodesLeaderboard(
      BiConsumer<Exception, Leaderboard<QRCodeLeaderboardEntry>> callback) {
    QRCodeDatabase.getInstance()
        .getAllQRCodes(
            task -> {
              if (!task.isSuccessful()) {
                callback.accept(task.getException(), null);
                return;
              }

              List<QRCodeLeaderboardEntry> entries = new ArrayList<>();
              for (QRCode qrCode : task.getData()) {
                entries.add(new QRCodeLeaderboardEntry(qrCode, qrCode.getScore(), "points"));
              }

              Collections.sort(entries);
              callback.accept(null, new Leaderboard<>(entries));
            });
  }

  public void handleEntryClick(PlayerLeaderboardEntry entry) {
    Player player = entry.getPlayer();
    gameController.setBody(new ProfileFragment(gameController, player.getDeviceId()));
  }

  public void handleEntryClick(QRCodeLeaderboardEntry entry) {
    QRCode qrCode = entry.getQRCode();
    QRCodeDatabase.getInstance()
        .playerHasQRCode(
            gameController.getActivePlayer(),
            qrCode,
            task -> {
              if (task.isSuccessful()) {
                boolean playerHasQR = task.getData();

                QRCodeFragment fragment;
                if (playerHasQR) {
                  // Show delete fragment
                  fragment =
                      DeleteQRCodeFragment.newInstance(qrCode, gameController.getActivePlayer());
                } else {
                  // Show QR fragment without any option
                  fragment = QRCodeFragment.newInstance(qrCode, gameController.getActivePlayer());
                }
                gameController.setPopup(fragment);
              }
            });
  }

  public void getTopQRCodesByRegionLeaderboard(
      BiConsumer<Exception, Map<String, Leaderboard<QRCodeLeaderboardEntry>>> callback) {
    // TODO: Implement after location recording is completed and fetch the region of the photo.
  }
}
