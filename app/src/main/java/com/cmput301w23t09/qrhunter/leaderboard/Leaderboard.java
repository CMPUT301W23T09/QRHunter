package com.cmput301w23t09.qrhunter.leaderboard;

import java.util.List;

/** This class represents a leaderboard. */
public class Leaderboard {
  /** Name of the leaderboard */
  private final String name;
  /** All leaderboard entries within this leaderboard. */
  private final List<LeaderboardEntry> entries;

  /**
   * Constructor for a leaderboard
   *
   * @param name leaderboard name
   * @param entries entries of this leaderboard.
   */
  public Leaderboard(String name, List<LeaderboardEntry> entries) {
    this.name = name;
    this.entries = entries;
  }

  /**
   * Constructor for a leaderboard
   *
   * @param entries entries of this nameless leaderboard
   */
  public Leaderboard(List<LeaderboardEntry> entries) {
    this.name = null;
    this.entries = entries;
  }

  public String getName() {
    return name;
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
