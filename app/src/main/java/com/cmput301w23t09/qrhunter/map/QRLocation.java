package com.cmput301w23t09.qrhunter.map;

import android.location.Location;
import androidx.annotation.Nullable;

/** Holds a QR code's location, created from a location string stored in the database */
public class QRLocation extends Location {
  /**
   * Creates a location object based on a location string
   *
   * @param provider Location provider
   * @param locStr Location string
   */
  public QRLocation(@Nullable String provider, String locStr) {
    super(provider);
    int splitPoint = locStr.indexOf(';');
    Double lat = Double.parseDouble(locStr.substring(0, splitPoint));
    Double lon = Double.parseDouble(locStr.substring(splitPoint + 1));
    setLatitude(lat);
    setLongitude(lon);
  }

  /**
   * Converts the Location object to a string that can be stored on Firebase
   *
   * @return A string in the format "latitude;longitude"
   */
  public String getLocationString() {
    return "" + getLatitude() + ";" + getLongitude();
  }
}
