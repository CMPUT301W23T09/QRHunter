package com.cmput301w23t09.qrhunter.leaderboard;

import com.cmput301w23t09.qrhunter.player.Player;

/** Represents a player leaderboard entry item. */
public class PlayerLeaderboardEntry extends LeaderboardEntry {

  private final Player player;

  /**
   * Constructor for a player leaderboard entry
   *
   * @param player player of the leaderboard entry
   * @param score score of the leaderboard entry
   * @param scoreSuffix suffix to use for the leaderboard entry
   */
  public PlayerLeaderboardEntry(Player player, long score, String scoreSuffix) {
    super(player.getUsername(), score, scoreSuffix);
    this.player = player;
  }

  /**
   * Retrieve the player this entry represents.
   *
   * @return the player
   */
  public Player getPlayer() {
    return player;
  }
}
