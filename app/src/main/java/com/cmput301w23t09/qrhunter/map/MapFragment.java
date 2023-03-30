package com.cmput301w23t09.qrhunter.map;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.cmput301w23t09.qrhunter.BaseFragment;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.database.DatabaseConsumer;
import com.cmput301w23t09.qrhunter.database.DatabaseQueryResults;
import com.cmput301w23t09.qrhunter.leaderboard.Leaderboard;
import com.cmput301w23t09.qrhunter.leaderboard.LeaderboardEntry;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class MapFragment extends BaseFragment implements OnMapReadyCallback {
  private boolean locationPermissionGranted;
  private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
  private GoogleMap map;
  private Location lastKnownLocation;
  private FusedLocationProviderClient fusedLocationProviderClient;
  private static final String TAG = "MapFragment";
  private static final int DEFAULT_ZOOM = 14;
  private LatLng defaultLocation = new LatLng(53.523565919249364, -113.52815038503842);

  private static LatLng[] placeholderQR;
  private LatLng currentLocation;
  private List<LatLng> latLngsList;
  private SearchView qrSearcher;
  private SearchQRController searchController;


  public MapFragment(GameController gameController) {
    super(gameController);
  }

  private void getLocationPermission() {
    /**
     * Request location permission, so that we can get the location of the device. The result of the
     * permission request is handled by a callback, onRequestPermissionsResult.
     */
    if (ContextCompat.checkSelfPermission(
            getContext().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      locationPermissionGranted = true;
    } else {
      ActivityCompat.requestPermissions(
          getActivity(),
          new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
          PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }
  }

  private void getDeviceLocation() {
    /**
     * Get the best and most recent location of the device, which may be null in rare cases when a
     * location is not available.
     */
    //    fusedLocationProviderClient =
    // LocationServices.getFusedLocationProviderClient(getContext());

    try {
      if (locationPermissionGranted) {
        // Get the last known location
        Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(
            getActivity(),
            new OnCompleteListener<Location>() {
              @Override
              public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                  // Set the map's camera position to the current location of the device.
                  lastKnownLocation = task.getResult();
                  if (lastKnownLocation != null) {
                    currentLocation =
                        new LatLng(
                            lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(currentLocation, DEFAULT_ZOOM));
                    map.addMarker(new MarkerOptions().position(currentLocation).title("YOU"));
                  }
                } else {
                  // Sets the map camera to the a set default location if lastKnownLocation is null
                  Log.d(TAG, "Current location is null. Using defaults.");
                  Log.e(TAG, "Exception: %s", task.getException());
                  map.animateCamera(
                      CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                  map.getUiSettings().setMyLocationButtonEnabled(false);
                }
              }
            });
      }
    } catch (SecurityException e) {
      Log.e("Exception: %s", e.getMessage(), e);
    }
  }

  /** Updates the UI depending on the whether location permissions are granted by the user */
  private void updateLocationUI() {
    if (map == null) {
      return;
    }
    try {
      if (locationPermissionGranted) {
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
      } else {
        // Don't get the location and instead ask for location permissions
        map.setMyLocationEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        lastKnownLocation = null;
        getLocationPermission();
      }
    } catch (SecurityException e) {
      Log.e("Exception: %s", e.getMessage());
    }
  }

  /** Handles the users response to permission dialogue box popup */
  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    locationPermissionGranted = false;
    if (requestCode
        == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) { // If request is cancelled, the result arrays
      // are empty.
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        locationPermissionGranted = true;
      }
    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    updateLocationUI();
  }

//    private void getQRFromDB() {
//      QRCodeDatabase.getInstance().getAllQRCodes(task -> {
//        if (!task.isSuccessful()) {
//          // do error handling here as database query failed
//          return;
//        }
//
//        // do stuff with ALL qr codes
//        List<LatLng> latLngs = new ArrayList<>();
//        if (task.isSuccessful()) {
//          for (QRCode qrCode : task.getData()) {
//            // Extract the latitude and longitude values for each QRCode.
//            Location loc = qrCode.getLoc();
//            double latitude = loc.getLatitude();
//            double longitude = loc.getLongitude();
//            // Create a new LatLng object and add it to the list.
//            LatLng latLng = new LatLng(latitude, longitude);
//            latLngs.add(latLng);
//          }
//        }
//      });
//    }
  public void getQRFromDB(DatabaseConsumer<List<LatLng>> callback) {
    QRCodeDatabase.getInstance().getAllQRCodes(task -> {
      if (!task.isSuccessful()) {
        // do error handling here as database query failed
        return;
      }

      latLngsList = new ArrayList<>();
      if (task.isSuccessful()) {
        for (QRCode qrCode : task.getData()) {
          Location loc = qrCode.getLoc();
          double latitude = loc.getLatitude();
          double longitude = loc.getLongitude();
          LatLng latLng = new LatLng(latitude, longitude);
          latLngsList.add(latLng);
        }
      }
      callback.accept(new DatabaseQueryResults<>(latLngsList));
    });
  }

//  public void getTopQRCodesLeaderboard(BiConsumer<Exception, Leaderboard> callback) {
//    QRCodeDatabase.getInstance()
//            .getAllQRCodes(
//                    task -> {
//                      if (!task.isSuccessful()) {
//                        callback.accept(task.getException(), null);
//                        return;
//                      }
//
//                      List<LeaderboardEntry> entries = new ArrayList<>();
//                      for (QRCode qrCode : task.getData()) {
//                        entries.add(new LeaderboardEntry(qrCode.getName(), qrCode.getScore(), "points"));
//                      }
//
//                      Collections.sort(entries);
//                      callback.accept(null, new Leaderboard(entries));
//                    });
//  }


  /**
   * @param inflater The LayoutInflater object that can be used to inflate any views in the
   *     fragment,
   * @param container If non-null, this is the parent view that the fragment's UI should be attached
   *     to. The fragment should not add the view itself, but this can be used to generate the
   *     LayoutParams of the view.
   * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
   *     saved state as given here.
   * @return View the view which is inflated displaying the the R.layout.map xml file
   */
  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Places.initialize(
        getContext().getApplicationContext(), "AIzaSyDniTKVk4HDVsQVG-uDxQ-eFV4nCWeM-gU");


    View view = inflater.inflate(R.layout.fragment_map, container, false);

    SupportMapFragment mapFragment =
            (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
    // Checks if support map fragment is found, if so pass current fragment as callback
    if (mapFragment != null) {
      // Gets a googleMap object
      mapFragment.getMapAsync(this);
    }

    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());


    // create search view
    qrSearcher = view.findViewById(R.id.qr_searcher);
    searchController = new SearchQRController(qrSearcher, this);
    qrSearcher.setOnQueryTextListener(searchController.handleSearch());

    return view;
  }

//  /**
//   * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
//   * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
//   *     saved state as given here.
//   */
//  @Override
//  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//    super.onViewCreated(view, savedInstanceState);
//
//  }

  /**
   * @param map the Google maps
   */
  @Override
  public void onMapReady(GoogleMap map) {
    this.map = map;
    // Turn on the My Location layer and the related control on the map.
    updateLocationUI();

    // Get the current location of the device and set the position of the map.
    getDeviceLocation();
    //loop through arraylist
    getQRFromDB(latLngList -> {
      for (LatLng latLng : latLngsList) {
        // add a marker for each LatLng on the map
        map.addMarker(new MarkerOptions().position(latLng));
      }
    });




//    placeholderQR =
//        new LatLng[] {
//          new LatLng(53.52748572137864, -113.52965526862573), // Engineering Physics Club
//          new LatLng(53.52644615688437, -113.52453761405557), // CAB
//          new LatLng(53.526790555323736, -113.52716617858209), // CSC
//          new LatLng(53.52606986652603, -113.52166228586451), // Rutherford Library
//          new LatLng(53.523182298052724, -113.52719701274522), // Butterdome
//          new LatLng(53.520845993958204, -113.523585870797) // Ualberta hospital
//        };
//    //replace with listQR
//    for (LatLng location : placeholderQR) {
//      map.addMarker(new MarkerOptions().position(location));
    }
}
