package com.cmput301w23t09.qrhunter.leaderboard;

public class LeaderboardEntry implements Comparable<LeaderboardEntry> {

  private final String name;
  private final long score;
  private final String scoreSuffix;

  public LeaderboardEntry(String name, long score, String scoreSuffix) {
    this.name = name;
    this.score = score;
    this.scoreSuffix = scoreSuffix;
  }

  public String getName() {
    return name;
  }

  public long getScore() {
    return score;
  }

  public String getScoreSuffix() {
    return scoreSuffix;
  }

  @Override
  public int compareTo(LeaderboardEntry otherEntry) {
    int entryComparisonScore = Long.compare(otherEntry.getScore(), getScore());

    // Compare name if score is equal.
    if (entryComparisonScore == 0) {
      return otherEntry.getName().compareTo(getName());
    }

    return entryComparisonScore;
  }
}
