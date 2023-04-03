package com.cmput301w23t09.qrhunter.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.cmput301w23t09.qrhunter.BaseFragment;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.qrcode.DeleteQRCodeFragment;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapFragment extends BaseFragment implements OnMapReadyCallback {
  private static final int SEARCH_RADIUS_IN_M = 100;
  private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
  private static final String API_KEY = "AIzaSyDniTKVk4HDVsQVG-uDxQ-eFV4nCWeM-gU";
  private static final String TAG = "MapFragment";
  private static final int DEFAULT_ZOOM = 14;
  private static final int MARKER_WIDTH_PIXELS = 70;
  private static final int MARKER_HEIGHT_PIXELS = 70;

  private boolean locationPermissionGranted;
  private GoogleMap map;
  private FusedLocationProviderClient fusedLocationProviderClient;
  private SearchView qrSearchbar;
  private Button qrSearchButton;

  private Marker ourPlayer = null;
  private List<Marker> currentQRMarkers = new ArrayList<>();

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
    try {
      if (locationPermissionGranted) {
        Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(
            getActivity(),
            new OnCompleteListener<Location>() {
              @Override
              public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                  Location lastKnownLocation = task.getResult();
                  if (lastKnownLocation != null) {
                    LatLng currentLocation =
                        new LatLng(
                            lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    if (map != null) {
                      map.animateCamera(
                          CameraUpdateFactory.newLatLngZoom(currentLocation, DEFAULT_ZOOM));
                      Bitmap playerProfile;
                      try {
                        playerProfile = getActivePlayer().getProfilePic();
                      } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                      } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                      }
                      Bitmap scaledBitmap =
                          Bitmap.createScaledBitmap(
                              playerProfile,
                              MARKER_WIDTH_PIXELS + 20,
                              MARKER_HEIGHT_PIXELS + 20,
                              false);
                      ourPlayer =
                          map.addMarker(
                              new MarkerOptions()
                                  .position(currentLocation)
                                  .title("YOU")
                                  .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap)));
                    }
                  } else {
                    Log.d(TAG, "Last known location is null. Using default location.");
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                  }
                } else {
                  Log.d(TAG, "Error getting last known location.");
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
    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    fusedLocationProviderClient.requestLocationUpdates(
        locationRequest, locationCallback, Looper.getMainLooper());
  }

  private LocationCallback locationCallback =
      new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
          if (locationResult == null) {
            return;
          }
          for (Location location : locationResult.getLocations()) {
            if (location != null) {
              // Update the last known location and move the camera
              LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
              if (map != null) {
                Bitmap playerProfile;
                try {
                  playerProfile = getActivePlayer().getProfilePic();
                } catch (InterruptedException e) {
                  throw new RuntimeException(e);
                } catch (ExecutionException e) {
                  throw new RuntimeException(e);
                }
                Bitmap scaledBitmap =
                    Bitmap.createScaledBitmap(
                        playerProfile, MARKER_WIDTH_PIXELS + 20, MARKER_HEIGHT_PIXELS + 20, false);

                if (ourPlayer != null) {
                  ourPlayer.remove();
                }
                ourPlayer =
                    map.addMarker(
                        new MarkerOptions()
                            .position(currentLocation)
                            .title("YOU")
                            .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap)));
                displayQRCodeMarkersWithinRadiusOnMap(map, currentLocation);
              }
            }
          }
        }
      };

  private void movePlayer(LatLng newPosition) {
    if (ourPlayer != null) {
      ourPlayer.remove();
    }

    Bitmap playerAvatar;
    try {
      playerAvatar = getActivePlayer().getProfilePic();
    } catch (InterruptedException | ExecutionException exception) {
      Log.w(TAG, "Failed to retrieve player avatar");
      return;
    }

    Bitmap scaledBitmap =
        Bitmap.createScaledBitmap(
            playerAvatar, MARKER_WIDTH_PIXELS + 20, MARKER_HEIGHT_PIXELS + 20, false);
    ourPlayer =
        map.addMarker(
            new MarkerOptions()
                .position(newPosition)
                .title("YOU")
                .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap)));
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

  // Function to retrieve QR codes within a certain radius from the user's current location and
  // display them on the map
  public void displayQRCodeMarkersWithinRadiusOnMap(GoogleMap mMap, LatLng currentLocation) {
    if (currentLocation == null) {
      return;
    }

    QRCodeDatabase.getInstance()
        .getAllQRCodes(
            qrCodes -> {
              if (qrCodes.isSuccessful()) {
                List<QRCode> qrCodeList = qrCodes.getData();
                Log.d(TAG, "Query successful. Result count: " + qrCodes.getData().size());

                List<Marker> newMarkers = new ArrayList<>();
                for (QRCode qrCode : qrCodeList) {
                  for (QRLocation location : qrCode.getLocations()) {
                    float[] results = new float[1];
                    Location.distanceBetween(
                        currentLocation.latitude,
                        currentLocation.longitude,
                        location.getLatitude(),
                        location.getLongitude(),
                        results);
                    float distanceInMeters = results[0];
                    if (distanceInMeters <= SEARCH_RADIUS_IN_M) {
                      LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                      try {
                        Bitmap qrBitmap = qrCode.getVisualRepresentation();
                        Bitmap scaledBitmap =
                            Bitmap.createScaledBitmap(
                                qrBitmap, MARKER_WIDTH_PIXELS, MARKER_HEIGHT_PIXELS, false);
                        Marker addedQR =
                            mMap.addMarker(
                                new MarkerOptions()
                                    .position(latLng)
                                    .title(qrCode.getName())
                                    .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap)));
                        addedQR.setTag(qrCode);
                        newMarkers.add(addedQR);
                      } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                      } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                      }
                    }
                  }
                }

                for (Marker currentMarker : currentQRMarkers) {
                  currentMarker.remove();
                }
                currentQRMarkers.addAll(newMarkers);
              }
            });
  }

  private boolean onMarkerClick(Marker marker) {
    Object tag = marker.getTag();
    if (tag instanceof QRCode) {
      QRCode qrCode = (QRCode) tag;

      // Create and show the QRCodeFragment
      QRCodeDatabase.getInstance()
          .getQRCodeByHash(
              qrCode.getHash(),
              qrTask -> {
                QRCode latestQR = qrTask.getData();

                // Which QRFragment do we display?
                boolean playerHasQR =
                    qrTask.getData().getPlayers().contains(getActivePlayer().getDocumentId());
                QRCodeFragment qrCodeFragment;
                if (playerHasQR) {
                  // Show delete QRFragment
                  qrCodeFragment = DeleteQRCodeFragment.newInstance(latestQR, getActivePlayer());
                } else {
                  // Show regular QRFragment
                  qrCodeFragment = QRCodeFragment.newInstance(latestQR, getActivePlayer());
                }
                getGameController().setPopup(qrCodeFragment);
              });
      return true; // Return true to indicate that the click event has been consumed
    }
    return false; // Return false to indicate that the click event has not been consumed
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
    Places.initialize(getContext().getApplicationContext(), API_KEY);
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
    qrSearchbar = view.findViewById(R.id.qr_searchbar);
    qrSearchButton = view.findViewById(R.id.qr_searcher);
    searchController = new SearchQRController(qrSearchbar, qrSearchButton, this);
    qrSearchbar.setOnQueryTextListener(searchController.searchNearbyCodes());
    qrSearchButton.setOnClickListener(searchController.getNearbyCodes());

    return view;
  }

  /**
   * Called when the map has been initialized
   *
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
    displayQRCodeMarkersWithinRadiusOnMap(map, null);
    googleMap.setOnMarkerClickListener(this::onMarkerClick);
  }

  public boolean getLocationPermissionGranted() {
    return locationPermissionGranted;
  }

  public FusedLocationProviderClient getFusedLocationProviderClient() {
    return fusedLocationProviderClient;
  }
}
