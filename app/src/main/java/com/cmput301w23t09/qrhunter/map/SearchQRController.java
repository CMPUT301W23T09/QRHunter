package com.cmput301w23t09.qrhunter.map;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import com.google.android.gms.maps.model.LatLng;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchQRController {
  private Fragment fragment;
  private SearchView searchView;

  public SearchQRController(SearchView searchView, Fragment fragment) {
    this.searchView = searchView;
    this.fragment = fragment;
  }

  public void setSuggestions() {}

  public SearchView.OnQueryTextListener handleSearch() {
    return new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        String locationInput = searchView.getQuery().toString().trim();
        // parse location input
        LatLng loc = parseInput(locationInput);
        if (loc == null) {
          Toast.makeText(
                  fragment.getContext(),
                  "Invalid format, enter a location name or location coordinates seperated by a comma",
                  Toast.LENGTH_SHORT)
              .show();
        }
        // query for nearby qr codes
        showNearbyQRCodes(loc);
        return true;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        return false;
      }
    };
  }

  public LatLng parseInput(String locationInput) {
    // check if input is blank
    if (locationInput.equals("")) {
      return null;
    }

    // check if a location coordinate was given
    if (locationInput.matches(".*\\d.*")) {
      if (locationInput.matches(".*,+.*")) {
        // check input format
        String[] coords = locationInput.split(",");
        if (coords.length != 2) {
          return null;
        }
        // get location coordinates
        Double latitude = parseDoubleInput(coords[0]);
        Double longitude = parseDoubleInput(coords[1]);
        return new LatLng(latitude, longitude);
      }
    }

    // if a location name was given
    Geocoder geocoder = new Geocoder(fragment.getContext());
    List<Address> addresses;
    // parse location name
    try {
      addresses = geocoder.getFromLocationName(locationInput, 1);
    } catch (IOException e) {
      return null;
    }
    // get address coordinates
    Address address = addresses.get(0);
    return new LatLng(address.getLatitude(), address.getLongitude());
  }

  public Double parseDoubleInput(String str) {
    try {
      return Double.parseDouble(str);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  /**
   * Return qr codes near given location
   *
   * @param loc Location to find nearby qr codes from
   */
  public void showNearbyQRCodes(LatLng loc) {
    ArrayList<QRCode> nearbyCodes = new ArrayList<>();
    QRCodeDatabase qrDatabase = QRCodeDatabase.getInstance();
    qrDatabase.getAllQRCodes(
        result -> {
          // check if error occurred
          if (result.getException() != null) {
            Toast.makeText(
                    searchView.getContext(),
                    "An error occurred querying for nearby QR codes",
                    Toast.LENGTH_SHORT)
                .show();
          }
          // if query was successful
          for (QRCode qrCode : result.getData()) {
            if (qrCode.getLoc() != null) {
              // get the qr code's distance from the given location
              float[] distance = new float[1];
              Location.distanceBetween(
                  loc.latitude,
                  loc.longitude,
                  qrCode.getLoc().getLatitude(),
                  qrCode.getLoc().getLongitude(),
                  distance);
              if (distance[0] < 100) {
                nearbyCodes.add(qrCode);
              }
            }
          }
          // show query results
          if (nearbyCodes.size() == 0) {
            Toast.makeText(fragment.getContext(), "No nearby qr codes found", Toast.LENGTH_SHORT)
                .show();
          } else {
            new QRSearchResultFragment(nearbyCodes, fragment)
                .show(fragment.getChildFragmentManager(), "Show QR code search information");
          }
        });
  }
}
