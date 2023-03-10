package com.cmput301w23t09.qrhunter.leaderboard;

import java.util.List;

/** This class represents a leaderboard. */
public class Leaderboard {
  /** All leaderboard entries within this leaderboard. */
  private final List<LeaderboardEntry> entries;

  /**
   * Constructor for a leaderboard
   *
   * @param entries entries of this leaderboard.
   */
  public Leaderboard(List<LeaderboardEntry> entries) {
    this.entries = entries;
  }

  /**
   * Retrieve the entries of the leaderboard.
   *
   * @return leaderboard entries
   */
  public List<LeaderboardEntry> getEntries() {
    return entries;
  }
}
