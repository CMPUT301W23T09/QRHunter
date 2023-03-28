package com.cmput301w23t09.qrhunter.qrcode;

import com.cmput301w23t09.qrhunter.player.Player;

/** Represents an entry on the QRCode scans list. */
public class QRPlayerScanEntry {
  private final Player player;
  private final int score;

  public QRPlayerScanEntry(Player player, int score) {
    this.player = player;
    this.score = score;
  }

  /**
   * Retrieve the player this entry represents
   *
   * @return the player
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * Retrieve the score of the player
   *
   * @return the score
   */
  public int getScore() {
    return score;
  }
}
