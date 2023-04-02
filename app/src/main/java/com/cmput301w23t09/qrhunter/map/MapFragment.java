package com.cmput301w23t09.qrhunter.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cmput301w23t09.qrhunter.BaseFragment;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;

import java.util.List;

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
  private long LOCATION_UPDATE_INTERVAL = 30000;

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
              new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
              PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }
  }

  private void getDeviceLocation() {
    try {
      if (locationPermissionGranted) {
        Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
          @Override
          public void onComplete(@NonNull Task<Location> task) {
            if (task.isSuccessful()) {
              Location lastKnownLocation = task.getResult();
              if (lastKnownLocation != null) {
                LatLng currentLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                if (map != null) {
                  map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, DEFAULT_ZOOM));
                  map.addMarker(new MarkerOptions().position(currentLocation).title("YOU"));
                }
              } else {
                Log.d(TAG, "Last known location is null. Using default location.");
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                map.getUiSettings().setMyLocationButtonEnabled(false);
              }
            } else {
              Log.d(TAG, "Error getting last known location.");
              map.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
              map.getUiSettings().setMyLocationButtonEnabled(false);
            }
          }
        });
      }
    } catch (SecurityException e) {
      Log.e(TAG, "Security exception occurred: " + e.getMessage());
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

  private void startLocationUpdates() {
    // Set the location request parameters
    LocationRequest locationRequest = LocationRequest.create();
    locationRequest.setInterval(10000);
    locationRequest.setFastestInterval(5000);
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    // Request location updates
    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      return;
    }
    fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper());
  }

  private LocationCallback locationCallback = new LocationCallback() {
    @Override
    public void onLocationResult(LocationResult locationResult) {
      if (locationResult == null) {
        return;
      }
      for (Location location : locationResult.getLocations()) {
        if (location != null) {
          // Update the last known location and move the camera
          lastKnownLocation = location;
          LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
          if (map != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, DEFAULT_ZOOM));
            map.addMarker(new MarkerOptions().position(currentLocation).title("YOU"));
          }
        }
      }
    }
  };

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

  public void displayQRCodeMarkersOnMap(GoogleMap mMap) {
    QRCodeDatabase.getInstance()
        .getAllQRCodes(
            qrCodes -> {
              if (qrCodes.isSuccessful()) {
                // Get the list of QRCode objects from the callback result
                List<QRCode> qrCodeList = qrCodes.getData();
                Log.d(TAG, "Query successful. Result count: " + qrCodes.getData().size());

                // Loop through the list of QRCode objects and add markers to the Google Map
                for (QRCode qrCode : qrCodeList) {
                  QRLocation loc = qrCode.getLoc();
                  if (loc != null) {
                    LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                    Marker marker =
                        mMap.addMarker(
                            new MarkerOptions().position(latLng).title(qrCode.getName()));
                    marker.setTag(qrCode);
                    Log.d(
                        TAG,
                        "Marker added for QR code: "
                            + qrCode.getName()
                            + " at "
                            + latLng.toString());
                  }
                }
              }
            });

    // Set up the marker click listener to display QR code properties
    mMap.setOnMarkerClickListener(
        marker -> {
          QRCode qrCode = (QRCode) marker.getTag(); // Retrieve the associated QR code object
          if (qrCode != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            // Displays QR attributes as text
            builder
                .setTitle(qrCode.getName())
                .setMessage(
                    "QR Name: "
                        + qrCode.getName()
                        + "\n"
                        + "Location: "
                        + qrCode.getLoc().toString()
                        + "\n"
                        + "Score: "
                        + qrCode.getScore().toString()
                        + "\n"
                        + "Date created: "
                        + qrCode.getComments())
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            // Displays the QR image if available
            if (qrCode.getPhotos() != null) {
              // Handle imges
            }
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;

          } else {
            return false;
          }
        });
  }

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
  //   * @param savedInstanceState If non-null, this fragment is being re-constructed from a
  // previous
  //   *     saved state as given here.
  //   */
  //  @Override
  //  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
  //    super.onViewCreated(view, savedInstanceState);
  //
  //  }

  /**
   * @param googleMap the Google maps
   */
  @Override
  public void onMapReady(GoogleMap googleMap) {
    this.map = googleMap;
    // Turn on the My Location layer and the related control on the map.
    updateLocationUI();

    // Get the current location of the device and set the position of the map.
    getDeviceLocation();
    startLocationUpdates();

    if (this.map != null) {
      displayQRCodeMarkersOnMap(map);
    }
    // loop through arraylist
    //    getQRFromDB(latLngList -> {
    //      for (LatLng latLng : latLngsList) {
    //        // add a marker for each LatLng on the map
    //        map.addMarker(new MarkerOptions().position(latLng));
    //      }
    //    });

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

  public boolean getLocationPermissionGranted() {
    return locationPermissionGranted;
  }

  public FusedLocationProviderClient getFusedLocationProviderClient() {
    return fusedLocationProviderClient;
  }
}
