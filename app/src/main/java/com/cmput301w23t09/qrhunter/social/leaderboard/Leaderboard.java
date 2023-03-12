package com.cmput301w23t09.qrhunter.social.leaderboard;

import java.util.List;

public abstract class Leaderboard<T> {

  private final List<LeaderboardEntry<T>> entries;

  public Leaderboard(List<LeaderboardEntry<T>> entries) {
    this.entries = entries;
  }

  public List<LeaderboardEntry<T>> getEntries() {
    return entries;
  }
}
