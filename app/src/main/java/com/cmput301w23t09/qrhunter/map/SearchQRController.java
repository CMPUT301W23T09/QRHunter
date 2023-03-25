package com.cmput301w23t09.qrhunter.map;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.SearchView;
import android.widget.Toast;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import com.google.android.gms.maps.model.LatLng;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchQRController {
  private MapFragment fragment;
  private SearchView searchView;

  public SearchQRController(SearchView searchView, MapFragment fragment) {
    this.searchView = searchView;
    this.fragment = fragment;
    searchView.setQueryHint("Enter location");
  }

  public SearchView.OnQueryTextListener handleSearch() {
    return new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        String locationInput = searchView.getQuery().toString().trim();
        // parse location input
        LatLng loc = parseInput(locationInput);
        if (loc == null) {
          Toast msg =
              Toast.makeText(
                  fragment.getContext(),
                  "Invalid format, enter \"Here\", geolocation coordinates, or an address",
                  Toast.LENGTH_LONG);

          msg.show();
        } else {
          // query for nearby qr codes
          showNearbyQRCodes(loc);
        }
        return true;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        return false;
      }
    };
  }

  private LatLng parseInput(String locationInput) {
    // check if input is blank
    if (locationInput.equals("")) {
      return null;
    }

    // check if input is "here"
    // ... to be implemented ... //

    // check if a location coordinate was given
    if (locationInput.matches(".*\\d.*")) {
      String[] coords = {};
      if (locationInput.matches(".*,+.*")) {
        coords = locationInput.split(",");
      } else if (locationInput.matches(".* +.*")) {
        coords = locationInput.split(" ");
      }
      // check input format
      if (coords.length != 2) {
        return null;
      }
      // get location coordinates
      Double latitude = parseDoubleInput(coords[0]);
      Double longitude = parseDoubleInput(coords[1]);
      return new LatLng(latitude, longitude);
    }

    // check if a location address was given
    Geocoder geocoder = new Geocoder(fragment.getContext());
    List<Address> addresses;
    // parse location name
    try {
      addresses = geocoder.getFromLocationName(locationInput, 1);
    } catch (IOException e) {
      return null;
    }
    // get address coordinates
    if (addresses.size() != 1) {
      return null;
    }
    Address address = addresses.get(0);
    return new LatLng(address.getLatitude(), address.getLongitude());
  }

  private Double parseDoubleInput(String str) {
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
  private void showNearbyQRCodes(LatLng loc) {
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
