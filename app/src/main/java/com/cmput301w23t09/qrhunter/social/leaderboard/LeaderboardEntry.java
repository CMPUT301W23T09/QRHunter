package com.cmput301w23t09.qrhunter.social.leaderboard;

public class LeaderboardEntry<T> implements Comparable<LeaderboardEntry<T>> {

  private final T key;
  private final long score;

  public LeaderboardEntry(T key, long score) {
    this.key = key;
    this.score = score;
  }

  public T getKey() {
    return key;
  }

  public long getScore() {
    return score;
  }

  @Override
  public int compareTo(LeaderboardEntry<T> otherEntry) {
    return Long.compare(otherEntry.getScore(), getScore());
  }
}
