package com.cmput301w23t09.qrhunter.leaderboard;

import java.util.List;

/** This class represents a leaderboard. */
public class Leaderboard<T extends LeaderboardEntry<?>> {
  /** All leaderboard entries within this leaderboard. */
  private final List<T> entries;

  /**
   * Constructor for a leaderboard
   *
   * @param entries entries of this leaderboard.
   */
  public Leaderboard(List<T> entries) {
    this.entries = entries;
  }

  /**
   * Retrieve the entries of the leaderboard.
   *
   * @return leaderboard entries
   */
  public List<T> getEntries() {
    return entries;
  }
}
