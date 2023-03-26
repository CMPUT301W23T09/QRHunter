package com.cmput301w23t09.qrhunter.qrcode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.locationphoto.LocationPhotoAdapter;
import com.cmput301w23t09.qrhunter.locationphoto.LocationPhotoController;
import com.cmput301w23t09.qrhunter.locationphoto.LocationPhotoFragment;
import com.cmput301w23t09.qrhunter.locationphoto.LocationPhotoStorage;
import com.cmput301w23t09.qrhunter.map.LocationHandler;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.scanqr.camera.CameraLocationPhotoController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smarteist.autoimageslider.SliderView;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

/**
 * Displays information about a specific QRCode. It also lets the user:
 *
 * <ul>
 *   <li>Add scanned QR code to profile
 *   <li>Remove selected QR code from profile
 *   <li>Record geolocation of scanned QR code
 *   <li>Take location photo of scanned qr code
 * </ul>
 *
 * @author John Mabanta
 * @version 1.0
 */
public class QRCodeFragment extends DialogFragment implements Serializable {

  private QRCode qrCode;
  private Button takeLocationPhotoBtn;
  private CheckBox locationCheckbox;

  private LocationHandler locationHandler;

  private LocationPhotoFragment locationPhotoFragment;
  private LocationPhotoController locationPhotoController;
  private LocationPhotoStorage locationPhotoStorage;
  private SliderView locationPhotoSlider;
  private LocationPhotoAdapter locationPhotoAdapter;

  private Player activePlayer;

  private FloatingActionButton addButton;
  private FloatingActionButton deleteButton;
  private FloatingActionButton loadingButton;

  private QRCodeDatabase qrCodeDatabase;

  /**
   * Creates a new QRCodeFragment to display a specific QR Code
   *
   * @param qrCode The QR code to view
   * @param activePlayer The currently active/logged-in player
   * @return QRCodeFragment
   */
  public static QRCodeFragment newInstance(QRCode qrCode, Player activePlayer) {
    Bundle args = new Bundle();
    args.putSerializable("qrcode", qrCode);
    args.putSerializable("activePlayer", activePlayer);
    QRCodeFragment fragment = new QRCodeFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_qrcode, null);
    qrCode = (QRCode) getArguments().getSerializable("qrcode");
    activePlayer = (Player) getArguments().getSerializable("activePlayer");

    locationHandler = new LocationHandler(this);

    locationPhotoFragment = LocationPhotoFragment.newInstance(qrCode, this, activePlayer);
    locationPhotoStorage = LocationPhotoStorage.getInstance();
    locationPhotoController =
        new LocationPhotoController(locationPhotoFragment, qrCode, activePlayer);
    qrCodeDatabase = QRCodeDatabase.getInstance();
    try {
      setupViews(view);
    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }
    updateLocationPhoto();
    return createAlertDialog(view);
  }

  /**
   * Binds the UI components with the attributes of the QRCode
   *
   * @param view The view that displays fragment_qrcode.xml
   */
  private void setupViews(View view) throws ExecutionException, InterruptedException {
    locationCheckbox = view.findViewById(R.id.location_request_box);

    TextView qrName = view.findViewById(R.id.qr_name);
    qrName.setText(qrCode.getName());

    TextView qrScore = view.findViewById(R.id.qr_points);
    qrScore.setText(qrCode.getScore().toString() + " PTS");

    ImageView qrCodeVisual = view.findViewById(R.id.qr_code_visual);
    qrCodeVisual.setImageBitmap(qrCode.getVisualRepresentation());

    locationPhotoSlider = view.findViewById(R.id.location_photos);
    locationPhotoAdapter = new LocationPhotoAdapter(this.getContext(), qrCode);
    locationPhotoSlider.setSliderAdapter(locationPhotoAdapter, false);

    takeLocationPhotoBtn = view.findViewById(R.id.take_location_photo_btn);
    takeLocationPhotoBtn.setOnClickListener(
        v -> {
          locationPhotoStorage.playerHasLocationPhoto(
              qrCode,
              activePlayer,
              (hasPhoto) -> {
                if (hasPhoto) {
                  locationPhotoStorage.deletePhoto(
                      qrCode,
                      activePlayer,
                      isSuccessful -> {
                        if (isSuccessful) updateLocationPhoto();
                      });
                } else {
                  locationPhotoFragment.show(getParentFragmentManager(), "Take Location Photo");
                }
              });
        });
    locationCheckbox.setOnCheckedChangeListener(
        (buttonView, isChecked) -> {
          if (isChecked) {
            locationHandler.setQrToLastLocation(qrCode);
          } else {
            qrCode.setLoc(null);
          }
        });

    addButton = view.findViewById(R.id.addButton);
    deleteButton = view.findViewById(R.id.deleteButton);
    loadingButton = view.findViewById(R.id.loadingButton);

    updateAddDeleteButton();

    // implementing the add/delete button listeners
    addButton.setOnClickListener(this::onAddQRClicked);
    deleteButton.setOnClickListener(this::onRemoveQRClicked);
  }

  /**
   * Called when the add QR button is clicked
   *
   * @param view view
   */
  private void onAddQRClicked(View view) {
    addButton.setVisibility(View.GONE);
    loadingButton.setVisibility(View.VISIBLE);

    // Add QR to database, when the QR has been added, allow the deletion of the QRCode.
    // First check if the qr code exists.
    qrCodeDatabase.getQRCodeByHash(
        qrCode.getHash(),
        qrCodeHash -> {
          if (qrCodeHash.getException() != null) {
            addButton.setVisibility(View.VISIBLE);
            loadingButton.setVisibility(View.GONE);
            return;
          }

          // If it doesn't exist, add the QR
          if (qrCodeHash.getData() == null) {
            qrCodeDatabase.addQRCode(
                qrCode,
                task -> {
                  if (!task.isSuccessful()) {
                    addButton.setVisibility(View.VISIBLE);
                    loadingButton.setVisibility(View.GONE);
                    return;
                  }

                  // Add the player to the QR
                  qrCodeDatabase.addPlayerToQR(
                      activePlayer,
                      qrCode,
                      ignored -> {
                        deleteButton.setVisibility(View.VISIBLE);
                        loadingButton.setVisibility(View.GONE);
                      });
                });

          } else {
            // QRCode already exists, add player to the QR
            qrCodeDatabase.addPlayerToQR(
                activePlayer,
                qrCode,
                ignored -> {
                  deleteButton.setVisibility(View.VISIBLE);
                  loadingButton.setVisibility(View.GONE);
                });
          }
        });
  }

  /**
   * Called when the remove QR button is clicked
   *
   * @param view view
   */
  private void onRemoveQRClicked(View view) {
    deleteButton.setVisibility(View.GONE);
    loadingButton.setVisibility(View.VISIBLE);

    // Remove QR from player
    qrCodeDatabase.removeQRCodeFromPlayer(
        activePlayer,
        qrCode,
        ignored2 -> {
          addButton.setVisibility(View.VISIBLE);
          loadingButton.setVisibility(View.GONE);
        });
  }

  /**
   * Updates the locationPhoto image view to show the newly-captured location photo
   *
   * @see CameraLocationPhotoController
   * @see LocationPhotoController
   */
  public void updateLocationPhoto() {
    locationPhotoStorage.playerHasLocationPhoto(
        qrCode,
        activePlayer,
        (hasPhoto) -> {
          if (hasPhoto) {
            takeLocationPhotoBtn.setText(R.string.remove_location_photo);
            locationPhotoSlider.setCurrentPagePosition(
                locationPhotoAdapter.getPlayerLocationPhoto(activePlayer));
          } else takeLocationPhotoBtn.setText(R.string.take_location_photo);
        });
    locationPhotoAdapter.renewLocationPhotos(
        photos -> {
          if (photos.size() == 0) locationPhotoSlider.setVisibility(View.GONE);
          else locationPhotoSlider.setVisibility(View.VISIBLE);
        });
  }

  /**
   * Disables the "Record QR Location" box if the user has not granted location permissions
   *
   * @param requestCode The request code passed in {@link #requestPermissions(String[], int)}.
   * @param permissions The requested permissions. Never null.
   * @param grantResults The grant results for the corresponding permissions which is either {@link
   *     android.content.pm.PackageManager#PERMISSION_GRANTED} or {@link
   *     android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
   */
  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == LocationHandler.REQUEST_CODE_PERMISSIONS) {
      if (!locationHandler.locationPermissionsGranted()) {
        locationCheckbox.setEnabled(false);
      }
    }
  }

  /**
   * Creates a dialog box to display QRCode information in
   *
   * @param view The view that displays fragment_qrcode.xml
   * @return An AlertDialog that displays QRCode information
   */
  private AlertDialog createAlertDialog(View view) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    return builder.setView(view).setPositiveButton("Close", null).create();
  }

  /**
   * Display the add (+) QRCode button if the player does not have the QRCode to their name, else
   * display the remove (x) QRCode button if the player has it.
   */
  private void updateAddDeleteButton() {
    qrCodeDatabase.playerHasQRCode(
        activePlayer,
        qrCode,
        results -> {
          if (results.isSuccessful()) {
            if (results.getData()) {
              // QR code hash is already added to the player's account
              // Thus, display delete button
              addButton.setVisibility(View.GONE);
              deleteButton.setVisibility(View.VISIBLE);
            } else {
              // QR code hash is not yet added to the player's account
              // Thus, display add button
              addButton.setVisibility(View.VISIBLE);
              deleteButton.setVisibility(View.GONE);
            }
          } else {
            Log.w("QRCodeFragment", "Error getting player by device ID.", results.getException());
            Toast.makeText(getContext(), "Error getting player by device ID.", Toast.LENGTH_SHORT)
                .show();
          }
        });
  }

  /**
   * @return The location photo fragment
   */
  public LocationPhotoFragment getLocationPhotoFragment() {
    return locationPhotoFragment;
  }

  public LocationPhotoController getLocationPhotoController() {
    return locationPhotoController;
  }

  public LocationPhotoAdapter getLocationPhotoAdapter() {
    return locationPhotoAdapter;
  }
}
