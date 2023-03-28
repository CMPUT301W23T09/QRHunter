package com.cmput301w23t09.qrhunter.qrcode;

import android.location.Location;

public class QRLocation extends Location {

  private String city;

  public QRLocation(String provider) {
    super(provider);
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }
}
