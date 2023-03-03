package com.cmput301w23t09.qrhunter.social.leaderboard;

public class LeaderboardController {

  private final LeaderboardFragment fragment;
  private final LeaderboardManager manager;

  public LeaderboardController(LeaderboardFragment fragment) {
    this.fragment = fragment;
    this.manager = new LeaderboardManager();
  }

  public LeaderboardManager getLeaderboardManager() {
    return manager;
  }
}
