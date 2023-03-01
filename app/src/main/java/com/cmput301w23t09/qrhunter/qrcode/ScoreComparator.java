package com.cmput301w23t09.qrhunter.qrcode;

import java.util.Comparator;

/** This implements the comparator for sorting QR codes by score */
public class ScoreComparator implements Comparator<QRCode> {

  @Override
  public int compare(QRCode o1, QRCode o2) {
    return o1.getScore().compareTo(o2.getScore());
  }
}
