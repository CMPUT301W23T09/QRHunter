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
import com.cmput301w23t09.qrhunter.map.LocationHandler;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.scanqr.LocationPhotoController;
import com.cmput301w23t09.qrhunter.scanqr.LocationPhotoFragment;
import com.cmput301w23t09.qrhunter.scanqr.camera.CameraLocationPhotoController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

  private ImageView locationPhoto;
  private QRCode qrCode;
  private Button takeLocationPhotoBtn;
  private CheckBox locationCheckbox;
  private LocationHandler locationHandler;
  private LocationPhotoFragment locationPhotoFragment;
  private Player activePlayer;
  private FloatingActionButton addButton;
  private FloatingActionButton deleteButton;
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
    locationPhoto = view.findViewById(R.id.location_photo);
    locationCheckbox = view.findViewById(R.id.location_request_box);

    TextView qrName = view.findViewById(R.id.qr_name);
    qrName.setText(qrCode.getName());

    TextView qrScore = view.findViewById(R.id.qr_points);
    qrScore.setText(qrCode.getScore().toString() + " PTS");

    ImageView qrCodeVisual = view.findViewById(R.id.qr_code_visual);
    qrCodeVisual.setImageBitmap(qrCode.getVisualRepresentation());

    takeLocationPhotoBtn = view.findViewById(R.id.take_location_photo_btn);
    takeLocationPhotoBtn.setOnClickListener(
        v -> {
          if (qrCode.getPhotos().size() > 0) {
            qrCode.deletePhoto(qrCode.getPhotos().get(0));
            updateLocationPhoto();
          } else {
            locationPhotoFragment = LocationPhotoFragment.newInstance(qrCode, this, activePlayer);
            locationPhotoFragment.show(getParentFragmentManager(), "Take Location Photo");
          }
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

    updateAddDeleteButton();

    // implementing the add button
    addButton.setOnClickListener(
        v -> {
          qrCodeDatabase.addQRCode(qrCode);
          qrCodeDatabase.addPlayerToQR(activePlayer, qrCode);
          addButton.setVisibility(View.GONE);
          deleteButton.setVisibility(View.VISIBLE);
        });

    // implementing the delete button
    deleteButton.setOnClickListener(
        v -> {
          qrCodeDatabase.removeQRCodeFromPlayer(activePlayer, qrCode);
          addButton.setVisibility(View.VISIBLE);
          deleteButton.setVisibility(View.GONE);
        });
  }

  /**
   * Updates the locationPhoto image view to show the newly-captured location photo
   *
   * @see CameraLocationPhotoController
   * @see LocationPhotoController
   */
  public void updateLocationPhoto() {
    if (qrCode.getPhotos() != null && qrCode.getPhotos().size() > 0) {
      takeLocationPhotoBtn.setText(R.string.remove_location_photo);
      locationPhoto.setImageBitmap(qrCode.getPhotos().get(0).getPhoto());
    } else {
      takeLocationPhotoBtn.setText(R.string.take_location_photo);
      locationPhoto.setImageResource(android.R.color.transparent);
    }
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
}
