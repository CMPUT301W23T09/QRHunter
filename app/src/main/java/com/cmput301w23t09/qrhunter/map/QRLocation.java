package com.cmput301w23t09.qrhunter.map;

import android.location.Location;

/** Holds a QR code's location, created from a location string stored in the database */
public class QRLocation {
  private String locationString = "";
  private double latitude;
  private double longitude;
  private String region;

  /**
   * Creates a QRLocation object based on a location string
   *
   * @param locStr Location string
   */
  public QRLocation(String locStr) {
    String[] parts = locStr.split(";");
    this.locationString = locStr;
    Double lat = Double.parseDouble(parts[0]);
    Double lon = Double.parseDouble(parts[1]);
    String region = parts[2];
    latitude = lat;
    longitude = lon;
    this.region = region;
  }

  /**
   * Creates a QRLocation object based on an existing location object
   *
   * @param location Location object
   */
  public QRLocation(String region, Location location) {
    latitude = location.getLatitude();
    longitude = location.getLongitude();
    locationString = "" + latitude + ";" + longitude + ";" + region;
  }

  /**
   * Creates a QRLocation object from a set of coordinates.
   *
   * @param region region
   * @param lat Latitude
   * @param lon Longitude
   */
  public QRLocation(String region, double lat, double lon) {
    this.region = region;
    latitude = lat;
    longitude = lon;
    locationString = "" + lat + ";" + lon + ";" + region;
  }

  /**
   * Converts the Location object to a string that can be stored on Firebase
   *
   * @return A string in the format "latitude;longitude"
   */
  public String getLocationString() {
    return locationString;
  }

  /**
   * @return Returns QRLocation's latitude
   */
  public double getLatitude() {
    return latitude;
  }

  /**
   * @return Returns QRLocation's longitude
   */
  public double getLongitude() {
    return longitude;
  }

  public String getRegion() {
    return region;
  }

  /**
   * Calculates distance between this QRLocation and another QRLocation using Haversine's formula
   *
   * <p>Adapted From: https://stackoverflow.com/a/27943 By: user1921 (no longer exists) (2008-08-26)
   * Edited By: Deduplicator (https://stackoverflow.com/users/3204551/deduplicator) (2014-07-31)
   * License: CC BY-SA
   *
   * @param other The other QRLocation to calculate distance to
   * @return The distance between this and other (in metres)
   */
  public double distanceTo(QRLocation other) {
    double earthRadius = 6378100; // Radius of earth (m)
    double radLat = Math.toRadians(other.latitude - latitude);
    double radLon = Math.toRadians(other.longitude - longitude);
    double a =
        Math.sin(radLat / 2) * Math.sin(radLat / 2)
            + Math.cos(Math.toRadians(latitude))
                * Math.cos(Math.toRadians(other.latitude))
                * Math.sin(radLon / 2)
                * Math.sin(radLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return earthRadius * c;
  }
}
