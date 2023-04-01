package com.cmput301w23t09.qrhunter.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.profile.OtherProfileFragment;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controls the PlayerSearchFragment
 *
 * @author Andy Nguyen
 * @version 1.0
 */
public class PlayerSearchController {
  /** Game controller used to transition between fragments */
  GameController gameController;

  /**
   * Creates a PlayerSearchController
   *
   * @param gameController Used to transition between fragments
   */
  public PlayerSearchController(GameController gameController) {
    this.gameController = gameController;
  }

  /**
   * Handles the switch from the PlayerSearchFragment to the Leaderboard fragment when the back
   * button is pressed
   */
  public void handleBackButton() {
    LeaderboardFragment leaderboardFragment = new LeaderboardFragment(gameController);
    gameController.setBody(leaderboardFragment);
  }

  /**
   * Handles the user's search query by transitioning to the search fragment
   *
   * @param query Username query the user is searching for
   */
  public void handleSearchQuery(String query) {
    PlayerSearchFragment searchFragment = new PlayerSearchFragment(gameController);
    Bundle args = new Bundle();
    args.putString("search_query", query);
    searchFragment.setArguments(args);
    gameController.setBody(searchFragment);
  }

  /**
   * Displays the search query data in the list view of the PlayerSearchFragment
   *
   * @param usernameQuery Username query the user is searching for
   * @param searchQueryEntries All related entries to the user's username query
   * @param entryAdapter Adapter for the search query entries
   * @param searchLinearLayout Container for the search queries
   * @param context Used to access the context of the fragment
   */
  public void displaySearchQueryData(
      String usernameQuery,
      List<SearchQueryEntry> searchQueryEntries,
      SearchQueryEntryAdapter entryAdapter,
      LinearLayout searchLinearLayout,
      Context context) {
    PlayerDatabase.getInstance()
        .getAllPlayers(
            allPlayers -> {
              if (!allPlayers.isSuccessful()) {
                return;
              }

              if (allPlayers.getData() == null) {
                return;
              }

              // Information on how to convert a set to a list
                // Source: https://www.digitalocean.com/community/tutorials/set-to-list-in-java
                // By: Jayant Verma (08/03/22)
                // License: CC BY-NC-SA 4.0
              Set<Player> allPlayersSet = allPlayers.getData();
              List<Player> allPlayersList = new ArrayList<>(allPlayersSet);

              // filters the set of all players leaving only those containing the query as a
              // substring
              List<Player> relatedUsernamePlayers =
                  getRelatedUsernamePlayers(allPlayersList, usernameQuery);

              // if no players are found display Player Not Found message
              if (relatedUsernamePlayers.size() == 0) {
                TextView noPlayersFoundMessage = createNoPlayersFoundTextView(context);
                searchLinearLayout.addView(noPlayersFoundMessage);
              }

              // add all players with relevant usernames to the search query to the list
              for (Player relatedPlayer : relatedUsernamePlayers) {
                searchQueryEntries.add(
                    new SearchQueryEntry(relatedPlayer.getUsername(), relatedPlayer.getDeviceId()));
              }
              entryAdapter.notifyDataSetChanged();
            });
  }

  /**
   * Filters the list of all players leaving only those containing the query as a substring
   *
   * Information on how to filter a set:
   * Source: https://stackoverflow.com/questions/122105/how-to-filter-a-java-collection-based-on-predicate
   * By: Mario Fusco (https://stackoverflow.com/users/112779/mario-fusco) (09/06/10)
   * License: cc-wiki
   *
   * Information on how a comparator works:
   * Source: https://stackoverflow.com/questions/23136998/how-does-this-comparator-work
   * By: user3484803 and Denis Kulagin (https://stackoverflow.com/users/1065145/denis-kulagin) (04/17/14)
   * License: cc-wiki
   *
   * @param allPlayersList List of all the players in the database
   * @param usernameQuery Username query the user is searching for
   * @return List of players containing the query as a substring
   */
  private List<Player> getRelatedUsernamePlayers(
      List<Player> allPlayersList, String usernameQuery) {
    List<Player> relatedUsernamePlayers =
        allPlayersList.stream()
            .filter(
                player -> player.getUsername().toLowerCase().contains(usernameQuery.toLowerCase()))
            // makes exact matches come first in the list
            .sorted(
                Comparator.comparing(
                    player -> {
                      if (player.getUsername().equalsIgnoreCase(usernameQuery)) {
                        return -1;
                      } else {
                        return player
                            .getUsername()
                            .toLowerCase()
                            .indexOf(usernameQuery.toLowerCase());
                      }
                    }))
            .collect(Collectors.toList());
    return relatedUsernamePlayers;
  }

  /**
   * Creates the text view that if no players are found for the user's search query
   *
   * Information on how to create a TextView
   * Source: https://stackoverflow.com/questions/5918320/dynamically-add-textviews-to-a-linearlayout
   * By: Joseph Earl (https://stackoverflow.com/users/290028/joseph-earl) (05/07/11)
   *
   * @param context Used to access the context of the fragment
   * @return TextView with a player not found message
   */
  private TextView createNoPlayersFoundTextView(Context context) {
    int textViewId = View.generateViewId();
    TextView noPlayersFoundMessage = new TextView(context);
    noPlayersFoundMessage.setText("Player Not Found.");
    noPlayersFoundMessage.setTextSize(24);
    noPlayersFoundMessage.setGravity(Gravity.CENTER);
    noPlayersFoundMessage.setId(textViewId);
    return noPlayersFoundMessage;
  }

  /**
   * Handles when user clicking on a search entry by transitioning to their profile
   *
   * @param entry Entry the user clicks on
   */
  public void handleSearchQueryListClick(SearchQueryEntry entry) {
    gameController.setBody(new OtherProfileFragment(gameController, entry.getDeviceId()));
  }
}
