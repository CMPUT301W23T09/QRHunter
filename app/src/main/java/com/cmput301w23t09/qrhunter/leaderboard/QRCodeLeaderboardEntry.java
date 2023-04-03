package com.cmput301w23t09.qrhunter.leaderboard;

import com.cmput301w23t09.qrhunter.qrcode.QRCode;

/** Represents the QR leaderboard entry this entry represents */
public class QRCodeLeaderboardEntry extends LeaderboardEntry {

  private final QRCode qrCode;

  /**
   * Constructor for a leaderboard entry
   *
   * @param qrCode qrCode of the leaderboard entry
   * @param score score of the leaderboard entry
   * @param scoreSuffix suffix to use for the leaderboard entry
   */
  public QRCodeLeaderboardEntry(int position, QRCode qrCode, long score, String scoreSuffix) {
    super(position, qrCode.getName(), score, scoreSuffix);
    this.qrCode = qrCode;
  }

  /**
   * Retrieve the QRCode for the entry this represents
   *
   * @return the qrcode
   */
  public QRCode getQRCode() {
    return qrCode;
  }
}
