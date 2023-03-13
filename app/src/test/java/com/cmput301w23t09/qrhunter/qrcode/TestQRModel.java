package com.cmput301w23t09.qrhunter.qrcode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Comparator;
import org.junit.jupiter.api.Test;

public class TestQRModel {
  // create a mock hash
  private String mockHash() {
    return "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
  }
  // create a mock qr code
  private QRCode mockCode() {
    return new QRCode(mockHash(), "mock", null, 10, null, null, null, null);
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
    assertEquals(mockCode().getName(), "");
  }

  @Test
  public void testGetScore() {
    assertEquals((int) mockCode().getScore(), 0);
  }

  @Test
  public void testGetLoc() {
    assertEquals(mockCode().getLoc(), null);
  }

  @Test
  public void testQRSort() {
    ArrayList<QRCode> mockList = mockList();
    mockList.add(mockCode());
    // add another qr code with a higher score
    Comparator<QRCode> comparator = new ScoreComparator();
    mockList.sort(comparator);
    // check if order is correct
    // check with comparator.reversed()
  }
}
