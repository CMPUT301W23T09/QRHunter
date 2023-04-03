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
import com.cmput301w23t09.qrhunter.util.Tuple;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

/** Controller that handles retrieving and updating leaderboards. */
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
              List<Tuple<Player, Long>> rawEntries = new ArrayList<>();
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
                            rawEntries.add(new Tuple<>(player, score));
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
                            // Sort in descending order by score and then by username
                            rawEntries.sort(
                                (a, b) -> {
                                  int scoreCompare = (int) (b.getRight() - a.getRight());
                                  if (scoreCompare == 0) {
                                    return b.getLeft()
                                        .getUsername()
                                        .compareTo(a.getLeft().getUsername());
                                  }

                                  return scoreCompare;
                                });

                            List<PlayerLeaderboardEntry> entries = new ArrayList<>();
                            for (int i = 0; i < rawEntries.size(); i++) {
                              entries.add(
                                  new PlayerLeaderboardEntry(
                                      i + 1,
                                      rawEntries.get(i).getLeft(),
                                      rawEntries.get(i).getRight(),
                                      "points"));
                            }
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

              List<Tuple<Player, Long>> rawEntries = new ArrayList<>();
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
                rawEntries.add(new Tuple<>(player, scans));
              }

              // Sort in descending order by score and then by username
              rawEntries.sort(
                  (a, b) -> {
                    int scoreCompare = (int) (b.getRight() - a.getRight());
                    if (scoreCompare == 0) {
                      return b.getLeft().getUsername().compareTo(a.getLeft().getUsername());
                    }

                    return scoreCompare;
                  });

              List<PlayerLeaderboardEntry> entries = new ArrayList<>();
              for (int i = 0; i < rawEntries.size(); i++) {
                entries.add(
                    new PlayerLeaderboardEntry(
                        i + 1, rawEntries.get(i).getLeft(), rawEntries.get(i).getRight(), "codes"));
              }
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

              // Sort in descending order by score and then hash
              task.getData()
                  .sort(
                      (a, b) -> {
                        int scoreCompare = (int) (b.getScore() - a.getScore());
                        if (scoreCompare == 0) {
                          return b.getHash().compareTo(a.getHash());
                        }
                        return scoreCompare;
                      });

              List<QRCodeLeaderboardEntry> entries = new ArrayList<>();
              for (int i = 0; i < task.getData().size(); i++) {
                QRCode qrCode = task.getData().get(i);
                entries.add(new QRCodeLeaderboardEntry(i + 1, qrCode, qrCode.getScore(), "points"));
              }

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
              Map<String, List<Tuple<QRCode, Integer>>> rawEntriesByRegion = new HashMap<>();
              Map<String, Set<String>> leaderboardHashesAddedByRegion = new HashMap<>();

              for (QRCode qrCode : task.getData()) {
                // For each QR, add it to each of the locations the QR was found in
                for (QRLocation qrLocation : qrCode.getLocations()) {
                  if (qrLocation != null) {
                    rawEntriesByRegion.putIfAbsent(qrLocation.getRegion(), new ArrayList<>());
                    leaderboardHashesAddedByRegion.putIfAbsent(
                        qrLocation.getRegion(), new HashSet<>());

                    // Check that we haven't added this QR to this location already (if we haven't,
                    // add it!)
                    boolean tryAddingQRToLocation =
                        leaderboardHashesAddedByRegion
                            .get(qrLocation.getRegion())
                            .add(qrCode.getHash());
                    if (tryAddingQRToLocation) {
                      rawEntriesByRegion
                          .get(qrLocation.getRegion())
                          .add(new Tuple<>(qrCode, qrCode.getScore()));
                    }
                  }
                }
              }

              // Sort all of the leaderboards
              List<Leaderboard<QRCodeLeaderboardEntry>> leaderboards = new ArrayList<>();
              for (String city : rawEntriesByRegion.keySet()) {
                List<Tuple<QRCode, Integer>> rawEntries = rawEntriesByRegion.get(city);
                // Sort in descending order by score and then hash
                rawEntries.sort(
                    (a, b) -> {
                      int scoreCompare = (int) (b.getRight() - a.getRight());
                      if (scoreCompare == 0) {
                        return b.getLeft().getHash().compareTo(a.getLeft().getHash());
                      }

                      return scoreCompare;
                    });

                List<QRCodeLeaderboardEntry> entries = new ArrayList<>();
                for (int i = 0; i < rawEntries.size(); i++) {
                  QRCode qrCode = rawEntries.get(i).getLeft();
                  entries.add(
                      new QRCodeLeaderboardEntry(i + 1, qrCode, qrCode.getScore(), "points"));
                }
                leaderboards.add(new Leaderboard<>(city, entries));
              }
              leaderboards.sort(Comparator.comparing(Leaderboard::getName));

              // Return leaderboards
              callback.accept(null, leaderboards);
            });
  }

  /**
   * Called when a player leaderboard entry is clicked
   *
   * @param entry player entry
   */
  public void handleEntryClick(PlayerLeaderboardEntry entry) {
    Player player = entry.getPlayer();
    if (player.getDeviceId().equals(gameController.getActivePlayer().getDeviceId())) {
      gameController.setBody(new MyProfileFragment(gameController));
    } else {
      gameController.setBody(new OtherProfileFragment(gameController, player.getDeviceId()));
    }
  }

  /**
   * Called when a qr leaderboard entry is clicked
   *
   * @param entry qr entry
   */
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

  /** Called when the filter button is clicked */
  public void onFilterButtonClick() {
    LeaderboardSettingsFragment settingsFragment = new LeaderboardSettingsFragment(this);
    getGameController().setPopup(settingsFragment);
  }

  /**
   * Modify whether or not the leaderboards requested should only filter to followed players
   *
   * @param filteredByFollowedPlayers whether or not to apply the filter
   */
  public void setIsFilteredByFollowedPlayers(boolean filteredByFollowedPlayers) {
    if (filteredByFollowedPlayers != this.filteredByFollowedPlayers) {
      this.filteredByFollowedPlayers = filteredByFollowedPlayers;

      fragment.clearCachesAndReloadLeaderboard();
    }
  }

  /**
   * Check if the leaderboard is currently filtering to followed players
   *
   * @return if the filter is active
   */
  public boolean isFilteredByFollowedPlayers() {
    return filteredByFollowedPlayers;
  }

  /**
   * Retrieve the game controller
   *
   * @return game controller
   */
  public GameController getGameController() {
    return gameController;
  }

  /**
   * Retrieve the leaderboard fragment
   *
   * @return fragment
   */
  public LeaderboardFragment getFragment() {
    return fragment;
  }
}
