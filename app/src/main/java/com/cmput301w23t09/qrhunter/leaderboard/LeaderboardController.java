package com.cmput301w23t09.qrhunter.leaderboard;

import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import com.cmput301w23t09.qrhunter.qrcode.QRLocation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

public class LeaderboardController {
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
                        player.getQRCodeHashes(),
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
                long scans = player.getQRCodeHashes().size();
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

  /**
   * Retrieve the top QRCode points value leaderboard by region in descending order.
   *
   * @param callback callback to call with the leaderboards mapped by their city.
   */
  public void getTopQRCodesByRegionLeaderboard(BiConsumer<Exception, List<Leaderboard>> callback) {
    QRCodeDatabase.getInstance()
        .getAllQRCodes(
            task -> {
              if (!task.isSuccessful()) {
                callback.accept(task.getException(), null);
                return;
              }

              Map<String, List<LeaderboardEntry>> leaderboardEntriesByRegion = new HashMap<>();
              for (QRCode qrCode : task.getData()) {
                QRLocation qrLocation = qrCode.getLoc();
                if (qrLocation != null) {
                  leaderboardEntriesByRegion.putIfAbsent(qrLocation.getCity(), new ArrayList<>());
                  leaderboardEntriesByRegion
                      .get(qrLocation.getCity())
                      .add(new LeaderboardEntry(qrCode.getName(), qrCode.getScore(), "points"));
                }
              }

              List<Leaderboard> leaderboards = new ArrayList<>();
              for (String city : leaderboardEntriesByRegion.keySet()) {
                List<LeaderboardEntry> entries = leaderboardEntriesByRegion.get(city);
                Collections.sort(entries);

                leaderboards.add(new Leaderboard(city, entries));
              }

              callback.accept(null, leaderboards);
            });
    // TODO: Implement after location recording is completed and fetch the region of the photo.
  }
}
