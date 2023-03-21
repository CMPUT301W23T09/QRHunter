package com.cmput301w23t09.qrhunter.qrcode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestQRModel {
  // create a mock hash
  private String mockHash() {
    return "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
  }

  // create a mock qr code
  private QRCode mockCode() {
    QRCode qr = new QRCode();
    qr.setHash(mockHash());
    qr.setName("mockqr");
    qr.setScore(10);
    return qr;
  }

  @Test
  public void testGetHash() {
    assertEquals(mockCode().getHash(), mockHash());
  }

  @Test
  public void testGetName() {
    assertEquals(mockCode().getName(), "mockqr");
  }

  @Test
  public void testGetScore() {
    assertEquals((int) mockCode().getScore(), 10);
  }

  @Test
  public void testGetLoc() {
    assertEquals(mockCode().getLoc(), null);
  }
}
