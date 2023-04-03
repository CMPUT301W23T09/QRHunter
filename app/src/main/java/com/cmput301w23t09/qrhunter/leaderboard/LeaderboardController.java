package com.cmput301w23t09.qrhunter.leaderboard;

import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.map.QRLocation;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.profile.MyProfileFragment;
import com.cmput301w23t09.qrhunter.profile.OtherProfileFragment;
import com.cmput301w23t09.qrhunter.qrcode.DeleteQRCodeFragment;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeFragment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

public class LeaderboardController {
  private final GameController gameController;
  private final LeaderboardFragment fragment;
  private boolean filteredByFollowedPlayers;

  public LeaderboardController(LeaderboardFragment fragment, GameController gameController) {
    this.gameController = gameController;
    this.fragment = fragment;
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
                // Do we need to filter by followed players?
                if (isFilteredByFollowedPlayers()
                    && (!gameController
                            .getActivePlayer()
                            .getFollowing()
                            .contains(player.getDeviceId())
                        && !gameController
                            .getActivePlayer()
                            .getDeviceId()
                            .equals(player.getDeviceId()))) {
                  entriesLeft.decrementAndGet();
                  continue;
                }

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
                // Do we need to filter by followed players?
                if (isFilteredByFollowedPlayers()
                    && (!gameController
                            .getActivePlayer()
                            .getFollowing()
                            .contains(player.getDeviceId())
                        && !gameController
                            .getActivePlayer()
                            .getDeviceId()
                            .equals(player.getDeviceId()))) {
                  continue;
                }

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

  /**
   * Retrieve the top QRCode points value leaderboard by region in descending order.
   *
   * @param callback callback to call with the leaderboards mapped by their city.
   */
  public void getTopQRCodesByRegionLeaderboard(
      BiConsumer<Exception, List<Leaderboard<QRCodeLeaderboardEntry>>> callback) {
    QRCodeDatabase.getInstance()
        .getAllQRCodes(
            task -> {
              if (!task.isSuccessful()) {
                callback.accept(task.getException(), null);
                return;
              }

              // Store the leaderboard entries by location and keep track of what hashes were added
              // to what locations
              Map<String, List<QRCodeLeaderboardEntry>> leaderboardEntriesByRegion =
                  new HashMap<>();
              Map<String, Set<String>> leaderboardHashesAddedByRegion = new HashMap<>();

              for (QRCode qrCode : task.getData()) {
                // For each QR, add it to each of the locations the QR was found in
                for (QRLocation qrLocation : qrCode.getLocations()) {
                  if (qrLocation != null) {
                    leaderboardEntriesByRegion.putIfAbsent(
                        qrLocation.getRegion(), new ArrayList<>());
                    leaderboardHashesAddedByRegion.putIfAbsent(
                        qrLocation.getRegion(), new HashSet<>());

                    // Check that we haven't added this QR to this location already (if we haven't,
                    // add it!)
                    boolean tryAddingQRToLocation =
                        leaderboardHashesAddedByRegion
                            .get(qrLocation.getRegion())
                            .add(qrCode.getHash());
                    if (tryAddingQRToLocation) {
                      leaderboardEntriesByRegion
                          .get(qrLocation.getRegion())
                          .add(new QRCodeLeaderboardEntry(qrCode, qrCode.getScore(), "points"));
                    }
                  }
                }
              }

              // Sort all of the leaderboards
              List<Leaderboard<QRCodeLeaderboardEntry>> leaderboards = new ArrayList<>();
              for (String city : leaderboardEntriesByRegion.keySet()) {
                List<QRCodeLeaderboardEntry> entries = leaderboardEntriesByRegion.get(city);
                Collections.sort(entries);

                leaderboards.add(new Leaderboard<>(city, entries));
              }
              leaderboards.sort(Comparator.comparing(Leaderboard::getName));

              // Return leaderboards
              callback.accept(null, leaderboards);
            });
  }

  public void handleEntryClick(PlayerLeaderboardEntry entry) {
    Player player = entry.getPlayer();
    if (player.getDeviceId().equals(gameController.getActivePlayer().getDeviceId())) {
      gameController.setBody(new MyProfileFragment(gameController));
    } else {
      gameController.setBody(new OtherProfileFragment(gameController, player.getDeviceId()));
    }
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

  public void onFilterButtonClick() {
    LeaderboardSettingsFragment settingsFragment = new LeaderboardSettingsFragment(this);
    getGameController().setPopup(settingsFragment);
  }

  public void setIsFilteredByFollowedPlayers(boolean filteredByFollowedPlayers) {
    if (filteredByFollowedPlayers != this.filteredByFollowedPlayers) {
      this.filteredByFollowedPlayers = filteredByFollowedPlayers;

      fragment.clearCachesAndReloadLeaderboard();
    }
  }

  public boolean isFilteredByFollowedPlayers() {
    return filteredByFollowedPlayers;
  }

  public GameController getGameController() {
    return gameController;
  }

  public LeaderboardFragment getFragment() {
    return fragment;
  }
}
