package com.cmput301w23t09.qrhunter.leaderboard;

import com.cmput301w23t09.qrhunter.qrcode.QRCode;

public class QRCodeLeaderboardEntry extends LeaderboardEntry<QRCode> {

  private final QRCode qrCode;

  /**
   * Constructor for a leaderboard entry
   *
   * @param qrCode qrCode of the leaderboard entry
   * @param score score of the leaderboard entry
   * @param scoreSuffix suffix to use for the leaderboard entry
   */
  public QRCodeLeaderboardEntry(QRCode qrCode, long score, String scoreSuffix) {
    super(qrCode.getName(), score, scoreSuffix);
    this.qrCode = qrCode;
  }

  public QRCode getQRCode() {
    return qrCode;
  }
}
