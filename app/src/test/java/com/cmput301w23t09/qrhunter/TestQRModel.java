package com.cmput301w23t09.qrhunter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cmput301w23t09.qrhunter.map.QRLocation;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import java.util.ArrayList;
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

  @Test
  public void testAddLocation() {
    QRLocation csc = new QRLocation("Edmonton", 53.52678, -113.52708); // CSC
    QRCode mockQR = mockCode();
    mockQR.addLocation(csc);
    assertTrue(mockQR.getLocations().contains(csc));
  }

  @Test
  public void testAddLocationTooClose() {
    QRLocation csc = new QRLocation("Edmonton", 53.52678, -113.52708); // CSC
    QRLocation athabasca =
        new QRLocation("Edmonton", 53.52671, -113.52663); // Athabasca Hall (within 100m of CSC)
    QRCode mockQR = mockCode();
    mockQR.addLocation(csc);
    mockQR.addLocation(athabasca); // Should not add Athabasca
    assertTrue(mockQR.getLocations().contains(csc));
    assertFalse(mockQR.getLocations().contains(athabasca));
  }

  @Test
  public void testSetAndRemoveLocation() {
    QRLocation csc = new QRLocation("Edmonton", 53.52678, -113.52708); // CSC
    ArrayList<QRLocation> locations = new ArrayList<>();
    locations.add(csc);
    QRCode mockQR = mockCode();
    mockQR.setLocations(locations);
    assertTrue(mockQR.getLocations().contains(csc));
    mockQR.removeLocation(csc);
    assertFalse(mockQR.getLocations().contains(csc));
  }
}
