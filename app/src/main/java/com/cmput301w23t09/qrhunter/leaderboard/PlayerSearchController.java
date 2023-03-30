package com.cmput301w23t09.qrhunter.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerSearchController {
    GameController gameController;

    public PlayerSearchController(GameController gameController) {
        this.gameController = gameController;
    }

    public void handleBackButton() {
        LeaderboardFragment leaderboardFragment = new LeaderboardFragment(gameController);
        gameController.setBody(leaderboardFragment);
    }

    public void handleSearchQuery(String query) {
        PlayerSearchFragment searchFragment = new PlayerSearchFragment(gameController);
        Bundle args = new Bundle();
        args.putString("search_query", query);
        searchFragment.setArguments(args);
        gameController.setBody(searchFragment);
    }

//    public void getSearchQueryData(String usernameQuery) {
//        PlayerDatabase.getInstance()
//                .getPlayersWithRelatedUsernames(
//                        usernameQuery,
//                        relatedPlayers -> {
//                            if (!relatedPlayers.isSuccessful()) {
//                                return;
//                            }
//
//                            if (relatedPlayers.getData() == null) {
//                                return;
//                            }
//
//                            for (Player relatedPlayer : relatedPlayers.getData()) {
//                                Log.d("relatedPlayer", relatedPlayer.getUsername());
//                            }
//                        }
//                );
//    }
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

                            // filters the set of all players leaving only those containing the query as a substring
                            Set<Player> relatedUsernamePlayers = allPlayers.getData().stream()
                                    .filter(obj -> obj
                                            .getUsername()
                                            .toLowerCase()
                                            .contains(usernameQuery.toLowerCase()))
                                    .collect(Collectors.toSet());

                            // if no players are found display Player Not Found message
                            if (relatedUsernamePlayers.size() == 0) {
                                TextView noPlayersFoundMessage = new TextView(context);
                                noPlayersFoundMessage.setText("Player Not Found.");
                                searchLinearLayout.addView(noPlayersFoundMessage);

                            }

                            for (Player relatedPlayer : relatedUsernamePlayers) {
                                searchQueryEntries.add(new SearchQueryEntry(relatedPlayer.getUsername(), relatedPlayer.getDeviceId()));
                            }
                            entryAdapter.notifyDataSetChanged();
                        }
                );
    }

//    public void handleSearchQueryListClick(SearchQueryEntry entry) {
//        gameController.setBody(new OtherProfileFragment(gameController, entry.getDeviceId()));
//        }
//    }
}
