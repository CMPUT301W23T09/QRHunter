package com.cmput301w23t09.qrhunter.qrcode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestQRModel {
  // create a mock hash
  private String mockHash() {
    return "8926bb85b4e02cf2c877070dd8dc920acbf6c7e0153b735a3d9381ec5c2ac11d";
  }

  // create a mock qr code
  private QRCode mockCode() {
    return new QRCode(mockHash());
  }

  @Test
  public void testGetCorrectHash() {
    assertEquals(mockCode().getHash(), mockHash());
  }

  @Test
  public void testGetCorrectName() {
    assertEquals(mockCode().getName(), "RobaqinectTiger✿");
  }

  @Test
  public void testGetCorrectScore() {
    assertEquals((int) mockCode().getScore(), 32);
  }

  @Test
  public void testGetLoc() {
    assertEquals(mockCode().getLoc(), null);
  }
}
