package com.cmput301w23t09.qrhunter.locationphoto;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeFragment;
import com.cmput301w23t09.qrhunter.scanqr.camera.CameraLocationPhotoController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * The fragment that lets a user take a "location photo" to assist other users in locating the QR
 * Code
 *
 * @author John Mabanta
 * @version 1.0
 */
public class LocationPhotoFragment extends DialogFragment {

  private QRCode qrCode;
  private QRCodeFragment qrCodeFragment;
  private CameraLocationPhotoController cameraController;
  private Player activePlayer;

  /**
   * Create a LocationPhotoFragment that lets the user snap a location photo for the given QR code
   *
   * @param qrCode The QRCode to add the location photo to
   * @param qrCodeFragment The QRCodeFragment to update once the location photo has been taken
   * @param activePlayer The current logged in player
   * @see QRCodeFragment
   * @return The LocationPhotoFragment to display
   */
  public static LocationPhotoFragment newInstance(
      QRCode qrCode, QRCodeFragment qrCodeFragment, Player activePlayer) {
    // TODO: May need to bundle arguments
    Bundle args = new Bundle();
    args.putSerializable("qrcode", qrCode);
    args.putSerializable("qrcodefrag", qrCodeFragment);
    args.putSerializable("activePlayer", activePlayer);
    LocationPhotoFragment fragment = new LocationPhotoFragment();
    fragment.setArguments(args);
    return fragment;
  }

  /**
   * Creates the Dialog that shows the camera's preview and shutter button
   *
   * @param savedInstanceState The last saved instance state of the Fragment, or null if this is a
   *     freshly created Fragment
   * @return The Dialog that the user will use to take a location photo
   */
  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_location_photo, null);
    qrCode = (QRCode) getArguments().getSerializable("qrcode");
    qrCodeFragment = (QRCodeFragment) getArguments().getSerializable("qrcodefrag");
    activePlayer = (Player) getArguments().getSerializable("activePlayer");
    cameraController =
        new CameraLocationPhotoController(
            this,
            view.findViewById(R.id.locationPhotoCameraPreview),
            qrCodeFragment.getLocationPhotoController());
    FloatingActionButton shutterButton = view.findViewById(R.id.location_photo_shutter);
    shutterButton.setOnClickListener(
        v -> {
          qrCodeFragment.getLocationPhotoController().takePhoto();
        });
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    return builder.setView(view).create();
  }

  /**
   * Once the user dismisses the LocationPhotoFragment, update the QRCodeFragment to show the
   * newly-taken location photo
   *
   * @param dialog the dialog that was dismissed will be passed into the method
   */
  @Override
  public void onDismiss(@NonNull DialogInterface dialog) {
    super.onDismiss(dialog);
    qrCodeFragment.updateLocationPhoto();
  }
}
