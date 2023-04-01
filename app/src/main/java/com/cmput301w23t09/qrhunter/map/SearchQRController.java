package com.cmput301w23t09.qrhunter.map;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import com.cmput301w23t09.qrhunter.qrcode.ScoreComparator;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchQRController {
  private MapFragment fragment;
  private SearchView searchView;
  private Button searchBtn;

  public SearchQRController(SearchView searchView, Button searchBtn, MapFragment fragment) {
    this.searchView = searchView;
    this.searchBtn = searchBtn;
    this.fragment = fragment;
  }

  /**
   * Gets an on-query listener that shows qr codes near the queried location
   *
   * @return Return the on-query listener
   */
  public SearchView.OnQueryTextListener searchNearbyCodes() {
    return new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        String locationInput = searchView.getQuery().toString().trim();
        // parse location input
        LatLng loc;
        try {
          loc = parseInput(locationInput);
        } catch (Exception err) {
          Toast.makeText(fragment.getContext(), err.getMessage(), Toast.LENGTH_LONG).show();
          return true;
        }
        // otherwise get nearby qr codes directly
        showNearbyQRCodes(loc);
        return true;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        return false;
      }
    };
  }

  /**
   * Get an on click listener that shows qr codes near the user's location
   *
   * @return Return the on-click listener
   */
  @SuppressLint("MissingPermission") // handled by LocationPermissionGranted
  public View.OnClickListener getNearbyCodes() {
    return new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // check if user wants to search from the current location
        if (fragment.getLocationPermissionGranted()) {
          fragment
              .getFusedLocationProviderClient()
              .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
              .addOnSuccessListener(
                  fragment.getActivity(),
                  location -> {
                    if (location != null) {
                      showNearbyQRCodes(
                          new LatLng(location.getLatitude(), location.getLongitude()));
                    } else {
                      Toast.makeText(
                              fragment.getContext(),
                              "Could not query your current location",
                              Toast.LENGTH_LONG)
                          .show();
                    }
                  });
        } else {
          Toast.makeText(
                  fragment.getContext(), "Location permission not granted", Toast.LENGTH_SHORT)
              .show();
        }
      }
    };
  }

  /**
   * Parse string containing a location into a LatLng object
   *
   * @param locationInput The string to parse
   * @return Return the LatLng object parsed from the string input
   * @throws Exception Throw exception if unable to find a location from the input
   */
  private LatLng parseInput(String locationInput) throws Exception {
    // check if input is blank
    if (locationInput.equals("")) {
      throw new Exception("Please enter a location");
    }

    // check if a location coordinate was given
    if (locationInput.matches(".*\\d.*")) {
      String[] coords = {};
      if (locationInput.matches(".*,.*")) {
        coords = locationInput.split(",");
      } else if (locationInput.matches(".* .*")) {
        coords = locationInput.split(" ");
      } else if (locationInput.matches(".*;.*")) {
        coords = locationInput.split(";");
      }
      // check input format
      if (coords.length == 2) {
        // get location coordinates
        Double latitude = parseDoubleInput(coords[0]);
        Double longitude = parseDoubleInput(coords[1]);
        if (latitude != null && longitude != null) {
          return new LatLng(latitude, longitude);
        }
      }
    }

    // check if a location address was given
    else if (locationInput.matches(".*\\w.*")) {
      Geocoder geocoder = new Geocoder(fragment.getContext());
      List<Address> addresses;
      // parse location name
      try {
        addresses = geocoder.getFromLocationName(locationInput, 1);
      } catch (IOException e) {
        throw new Exception("Error reading address");
      }
      // get address coordinates
      if (addresses.size() != 1) {
        throw new Exception("Failure to find a matching address");
      }
      Address address = addresses.get(0);
      return new LatLng(address.getLatitude(), address.getLongitude());
    }

    throw new Exception("Invalid format, enter geolocation coordinates, or an address");
  }

  /**
   * Parse a double from a string
   *
   * @param str The string to parse
   * @return Return the parsed double, or null if no double was found
   */
  private Double parseDoubleInput(String str) {
    try {
      return Double.parseDouble(str);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  /**
   * Show qr codes near given location
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
            if (qrCode.getLocations() != null) {
              // get the qr code's distance from the given location
              float[] distance = new float[1];
              for (QRLocation qrLocation : qrCode.getLocations()) {
                Location.distanceBetween(
                    loc.latitude,
                    loc.longitude,
                    qrLocation.getLatitude(),
                    qrLocation.getLongitude(),
                    distance);
                // add the qr code if one of its locations is nearby
                if (distance[0] < 100) {
                  nearbyCodes.add(qrCode);
                  break;
                }
              }
            }
          }
          // show query results
          if (nearbyCodes.size() == 0) {
            Toast.makeText(fragment.getContext(), "No nearby qr codes found", Toast.LENGTH_SHORT)
                .show();
          } else {
            nearbyCodes.sort(new ScoreComparator().reversed());
            new QRSearchResultFragment(nearbyCodes, fragment)
                .show(fragment.getChildFragmentManager(), "Show QR code search information");
          }
        });
  }
}
