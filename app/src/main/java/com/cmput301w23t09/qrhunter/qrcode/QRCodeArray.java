package com.cmput301w23t09.qrhunter.qrcode;

import java.util.ArrayList;

/** This is a class representing a list of QR codes */
public class QRCodeArray extends ArrayList<QRCode> {

  public QRCodeArray() {
    super();
  }

  public int getTotalScore() {
    int total = 0;
    for (QRCode qrCode : this) {
      total += qrCode.getScore();
    }
    return total;
  }

  public int getTopScore() {
    this.sort(new ScoreComparator().reversed());
    if (this.size() > 0) {
      QRCode topQR = this.get(0);
      return topQR.getScore();
    } else {
      return 0;
    }
  }
}
