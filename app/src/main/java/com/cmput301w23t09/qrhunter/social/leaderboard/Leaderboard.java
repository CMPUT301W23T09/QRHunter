package com.cmput301w23t09.qrhunter.social.leaderboard;

import java.util.List;

public class Leaderboard {

  private final List<LeaderboardEntry> entries;

  public Leaderboard(List<LeaderboardEntry> entries) {
    this.entries = entries;
  }

  public List<LeaderboardEntry> getEntries() {
    return entries;
  }
}
