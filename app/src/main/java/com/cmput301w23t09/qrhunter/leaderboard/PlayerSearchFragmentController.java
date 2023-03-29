package com.cmput301w23t09.qrhunter.leaderboard;

import android.os.Bundle;
import android.util.Log;

import com.cmput301w23t09.qrhunter.GameController;

public class PlayerSearchFragmentController {
    GameController gameController;

    public PlayerSearchFragmentController(GameController gameController) {
        this.gameController = gameController;
    }

    public void handleBackButton() {
        LeaderboardFragment leaderboardFragment = new LeaderboardFragment(gameController);
        gameController.setBody(leaderboardFragment);
    }
}
