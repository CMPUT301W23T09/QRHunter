package com.cmput301w23t09.qrhunter.leaderboard;

import android.os.Bundle;

import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.profile.ProfileSettingsFragment;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import com.cmput301w23t09.qrhunter.GameController;

public class LeaderboardController {
    /** This is the game controller that controls the content on screen. */
    private final GameController gameController;

    public LeaderboardController(GameController gameController) {
        this.gameController = gameController;
    }

  /**
   * Retrieve the total points leaderboard in descending order.
   *
   * @param callback callback to call with leaderboard
   */
  public void getTotalPointsLeaderboard(BiConsumer<Exception, Leaderboard> callback) {
    PlayerDatabase.getInstance()
        .getAllPlayers(
            task -> {
              // For each player, run the database task to get their QRCodes
              if (!task.isSuccessful()) {
                callback.accept(task.getException(), null);
                return;
              }

              // Map each player to their total qr code hash scores.
              List<LeaderboardEntry> entries = new ArrayList<>();
              AtomicInteger entriesLeft = new AtomicInteger(task.getData().size());
              AtomicReference<Exception> exception = new AtomicReference<>();

              for (Player player : task.getData()) {
                // Fetch the QRCodes for each player and sum them up.
                QRCodeDatabase.getInstance()
                    .getQRCodeHashes(
                        player.getQrCodeHashes(),
                        qrCodeHashesTask -> {
                          if (qrCodeHashesTask.isSuccessful()) {
                            long score =
                                qrCodeHashesTask.getData().stream()
                                    .mapToLong(QRCode::getScore)
                                    .reduce(0, Long::sum);

                            // Add new player leaderboard entry
                            entries.add(
                                new LeaderboardEntry(player.getUsername(), score, "points"));
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
                            callback.accept(null, new Leaderboard(entries));
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
  public void getTopScansLeaderboard(BiConsumer<Exception, Leaderboard> callback) {
    PlayerDatabase.getInstance()
        .getAllPlayers(
            task -> {
              if (!task.isSuccessful()) {
                callback.accept(task.getException(), null);
                return;
              }

              List<LeaderboardEntry> entries = new ArrayList<>();
              for (Player player : task.getData()) {
                long scans = player.getQrCodeHashes().size();
                entries.add(new LeaderboardEntry(player.getUsername(), scans, "codes"));
              }

              Collections.sort(entries);
              callback.accept(null, new Leaderboard(entries));
            });
  }

  /**
   * Retrieve the top QRCodes points value leaderboard in descending order.
   *
   * @param callback callback to call with leaderboard
   */
  public void getTopQRCodesLeaderboard(BiConsumer<Exception, Leaderboard> callback) {
    QRCodeDatabase.getInstance()
        .getAllQRCodes(
            task -> {
              if (!task.isSuccessful()) {
                callback.accept(task.getException(), null);
                return;
              }

              List<LeaderboardEntry> entries = new ArrayList<>();
              for (QRCode qrCode : task.getData()) {
                entries.add(new LeaderboardEntry(qrCode.getName(), qrCode.getScore(), "points"));
              }

              Collections.sort(entries);
              callback.accept(null, new Leaderboard(entries));
            });
  }

  public void getTopQRCodesByRegionLeaderboard(
      BiConsumer<Exception, Map<String, Leaderboard>> callback) {
    // TODO: Implement after location recording is completed and fetch the region of the photo.
  }

  public void handleSearchQuery(String query) {
      PlayerSearchFragment searchFragment = new PlayerSearchFragment(gameController);
      Bundle args = new Bundle();
      args.putString("search_query", query);
      searchFragment.setArguments(args);
      gameController.setBody(searchFragment);
  }

}
