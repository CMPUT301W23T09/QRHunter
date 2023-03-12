package com.cmput301w23t09.qrhunter.social.leaderboard;

import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import java.util.List;

public class QRLeaderboard extends Leaderboard<QRCode> {

  public QRLeaderboard(List<LeaderboardEntry<QRCode>> leaderboardEntries) {
    super(leaderboardEntries);
  }
}
