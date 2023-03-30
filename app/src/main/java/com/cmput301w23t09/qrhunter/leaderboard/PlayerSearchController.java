package com.cmput301w23t09.qrhunter.leaderboard;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
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
    public void displaySearchQueryData(String usernameQuery, List<SearchQueryEntry> searchQueryEntries, SearchQueryEntryAdapter entryAdapter) {
        PlayerDatabase.getInstance()
                .getAllPlayers(
                        allPlayers -> {
                            if (!allPlayers.isSuccessful()) {
                                return;
                            }

                            if (allPlayers.getData() == null) {
                                return;
                            }

                            for (Player player : allPlayers.getData()) {
                                Log.d("AllPlayers", player.getUsername());
                            }

                            Set<Player> relatedUsernamePlayers = allPlayers.getData().stream()
                                    .filter(obj -> obj.getUsername().contains(usernameQuery))
                                    .collect(Collectors.toSet());

                            for (Player relatedPlayer : relatedUsernamePlayers) {
                                Log.d("RelatedPlayer", relatedPlayer.getUsername());
                                searchQueryEntries.add(new SearchQueryEntry(relatedPlayer.getUsername()));
                            }
                            entryAdapter.notifyDataSetChanged();
                        }
                );
    }
}
