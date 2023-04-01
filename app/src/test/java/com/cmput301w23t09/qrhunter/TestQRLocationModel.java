package com.cmput301w23t09.qrhunter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cmput301w23t09.qrhunter.map.QRLocation;
import org.junit.jupiter.api.Test;

public class TestQRLocationModel {
  // Test if we can construct QRLocation from a locationString stored in Firebase
  @Test
  public void testStringConstructor() {
    String locationString = "53.52678;-113.52708;Edmonton"; // CSC
    QRLocation location = new QRLocation(locationString);
    assertEquals(53.52678, location.getLatitude());
    assertEquals(-113.52708, location.getLongitude());
    assertEquals("Edmonton", location.getRegion());
  }

  // Test if we can construct QRLocation from a pair of latitude and longitude coordinates
  @Test
  public void testLatLonConstructor() {
    QRLocation location = new QRLocation("Edmonton", 53.52678, -113.52708); // CSC
    assertEquals(53.52678, location.getLatitude());
    assertEquals(-113.52708, location.getLongitude());
  }

  // Test if we can calculate the distance between two close points
  @Test
  public void testShortDistanceCalculation() {
    QRLocation csc = new QRLocation("Edmonton", 53.52678, -113.52708); // CSC
    QRLocation athabasca =
        new QRLocation("Edmonton", 53.52671, -113.52663); // Athabasca Hall (within 100m of CSC)
    assertTrue(csc.distanceTo(athabasca) <= 31); // These points are roughly 31 meters of each other
    assertEquals(csc.distanceTo(athabasca), athabasca.distanceTo(csc)); // Order shouldn't matter
  }

  // Test if we can calculate the distance between two far-away points
  @Test
  public void testLongDistanceCalculation() {
    QRLocation csc = new QRLocation("Edmonton", 53.52678, -113.52708); // CSC
    QRLocation buckinghamPalace =
        new QRLocation("Edmonton", 51.50136, -0.14189); // Buckingham Palace
    assertTrue(
        csc.distanceTo(buckinghamPalace)
            <= 6821700); // CSC and Buckingham palace are roughly 6281.7km apart
    assertEquals(
        csc.distanceTo(buckinghamPalace),
        buckinghamPalace.distanceTo(csc)); // Order shouldn't matter
  }
}
