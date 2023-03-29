package com.cmput301w23t09.qrhunter.leaderboard;

import android.os.Bundle;

import com.cmput301w23t09.qrhunter.GameController;

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
}
