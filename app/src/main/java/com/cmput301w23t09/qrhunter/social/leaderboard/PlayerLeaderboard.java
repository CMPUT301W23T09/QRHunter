package com.cmput301w23t09.qrhunter.social.leaderboard;

import com.cmput301w23t09.qrhunter.player.Player;
import java.util.List;

public class PlayerLeaderboard extends Leaderboard<Player> {

  public PlayerLeaderboard(List<LeaderboardEntry<Player>> leaderboardEntries) {
    super(leaderboardEntries);
  }
}
