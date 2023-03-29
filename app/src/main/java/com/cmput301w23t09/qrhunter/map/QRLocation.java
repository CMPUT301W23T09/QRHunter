package com.cmput301w23t09.qrhunter.map;

import android.location.Location;

/** Holds a QR code's location, created from a location string stored in the database */
public class QRLocation extends Location {
  private String locationString = "";

  /**
   * Creates a QRLocation object based on a location string
   *
   * @param locStr Location string
   */
  public QRLocation(String locStr) {
    super("");
    this.locationString = locStr;
    int splitPoint = locStr.indexOf(';');
    Double lat = Double.parseDouble(locStr.substring(0, splitPoint));
    Double lon = Double.parseDouble(locStr.substring(splitPoint + 1));
    setLatitude(lat);
    setLongitude(lon);
  }

  /**
   * Creates a QRLocation object based on an existing location object
   *
   * @param location Location object
   */
  public QRLocation(Location location) {
    super("");
    double lat = location.getLatitude();
    double lon = location.getLongitude();
    setLatitude(lat);
    setLongitude(lon);
    locationString = "" + lat + ";" + lon;
  }

  /**
   * Creates a QRLocation object from a set of coordinates.
   *
   * @param lat Latitude
   * @param lon Longitude
   */
  public QRLocation(double lat, double lon) {
    super("");
    locationString = "" + lat + ";" + lon;
    setLatitude(lat);
    setLongitude(lon);
  }

  /**
   * Converts the Location object to a string that can be stored on Firebase
   *
   * @return A string in the format "latitude;longitude"
   */
  public String getLocationString() {
    return locationString;
  }
}
