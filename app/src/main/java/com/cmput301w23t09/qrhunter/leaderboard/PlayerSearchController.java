package com.cmput301w23t09.qrhunter.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;

import java.util.ArrayList;
import java.util.Comparator;
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

                            Set<Player> allPlayersSet = allPlayers.getData();
                            List<Player> allPlayersList = new ArrayList<>(allPlayersSet);

                            // filters the set of all players leaving only those containing the query as a substring
                            List<Player> relatedUsernamePlayers = getRelatedUsernamePlayers(allPlayersList, usernameQuery);

                            // if no players are found display Player Not Found message
                            if (relatedUsernamePlayers.size() == 0) {
                                TextView noPlayersFoundMessage = createNoPlayersFoundTextView(context);
                                searchLinearLayout.addView(noPlayersFoundMessage);

                            }

                            for (Player relatedPlayer : relatedUsernamePlayers) {
                                searchQueryEntries.add(new SearchQueryEntry(relatedPlayer.getUsername(), relatedPlayer.getDeviceId()));
                            }
                            entryAdapter.notifyDataSetChanged();
                        }
                );
    }

    private List<Player> getRelatedUsernamePlayers(List<Player> allPlayersList, String usernameQuery) {
        List<Player> relatedUsernamePlayers = allPlayersList.stream()
                .filter(player -> player
                        .getUsername()
                        .toLowerCase()
                        .contains(usernameQuery.toLowerCase()))
                // makes exact matches come first in the list
                .sorted(Comparator.comparing(player -> {
                    if (player.getUsername().equalsIgnoreCase(usernameQuery)) {
                        return -1;
                    } else {
                        return player.getUsername().toLowerCase().indexOf(usernameQuery.toLowerCase());
                    }
                }))
                .collect(Collectors.toList());
        return relatedUsernamePlayers;
    }

    private TextView createNoPlayersFoundTextView(Context context) {
        TextView noPlayersFoundMessage = new TextView(context);
        noPlayersFoundMessage.setText("Player Not Found.");
        noPlayersFoundMessage.setTextSize(24);
        noPlayersFoundMessage.setGravity(Gravity.CENTER);
        return noPlayersFoundMessage;
    }

//    public void handleSearchQueryListClick(SearchQueryEntry entry) {
//        gameController.setBody(new OtherProfileFragment(gameController, entry.getDeviceId()));
//        }
//    }
}
