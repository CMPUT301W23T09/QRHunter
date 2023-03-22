package com.cmput301w23t09.qrhunter.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import java.util.ArrayList;

/**
 * Handles location permissions and retrieving the location of the player's device. Used to set the
 * location of a QRCode.
 *
 * @author John Mabanta
 * @version 1.0
 */
public class LocationHandler {

  private Fragment fragment;
  private FusedLocationProviderClient fusedLocationClient;

  public static final int REQUEST_CODE_PERMISSIONS = 20;
  public static final String[] LOCATION_PERMISSIONS =
      new String[] {
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
      };

  /**
   * Creates a new LocationHandler
   *
   * @param fragment The fragment that will use the user's location
   * @see QRCodeFragment
   */
  public LocationHandler(Fragment fragment) {
    this.fragment = fragment;
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(fragment.getContext());
    if (!locationPermissionsGranted())
      fragment.requestPermissions(LOCATION_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
  }

  /**
   * Sets the provided QRCode to the device's last known location
   *
   * @param qrCode The QRCode to set the location of.
   */
  @SuppressLint("MissingPermission") // Checked by locationPermissionsGranted()
  public void setQrToLastLocation(QRCode qrCode) {
    if (locationPermissionsGranted()) {
      fusedLocationClient
          .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
          .addOnSuccessListener(
              fragment.getActivity(),
              location -> {
                if (location != null) {
                  qrCode.setLoc(location);
                }
              });
    }
  }

  /**
   * Checks if at least one of the location permissions was granted.
   *
   * @return True if the user has granted access to either FINE or COARSE location, False otherwise.
   */
  public boolean locationPermissionsGranted() {
    for (String permission : LOCATION_PERMISSIONS) {
      if (ContextCompat.checkSelfPermission(fragment.getContext(), permission)
          == PackageManager.PERMISSION_GRANTED) return true;
    }
    return false;
  }

  public ArrayList<QRCode> getNearbyQRCodes() {
    ArrayList<QRCode> nearbyCodes = new ArrayList<>();
    QRCodeDatabase qrDatabase = QRCodeDatabase.getInstance();
    qrDatabase.getAllQRCodes(
        result -> {
          // check if error occurred
          if (result.getException() != null) {
            Toast.makeText(
                    fragment.getContext(),
                    "An error occurred querying for nearby QR codes",
                    Toast.LENGTH_SHORT)
                .show();
          }
          // if query was successful
          for (QRCode qrCode : result.getData()) {
            if (qrCode.getLoc() != null) {
              qrCode.getLoc().getLatitude();
              qrCode.getLoc().getLongitude();
            }
          }
        });
    return nearbyCodes;
  }
}
