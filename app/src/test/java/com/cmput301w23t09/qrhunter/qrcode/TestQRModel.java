package com.cmput301w23t09.qrhunter.qrcode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

public class TestQRModel {
  // create a mock hash
  private String mockHash() {
    return "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
  }

  // create a mock qr code
  private QRCode mockCode() {
    try {
      return new QRCode(mockHash(), "mockqr", 10, null, null, null, null);
    } catch (ExecutionException | InterruptedException e) {
      throw (new RuntimeException(e));
    }
  }

  // create a mock qr code list
  private ArrayList<QRCode> mockList() {
    return new ArrayList<QRCode>();
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
