package com.cmput301w23t09.qrhunter.scanqr;

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
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeFragment;
import com.cmput301w23t09.qrhunter.scanqr.camera.CameraLocationPhotoController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LocationPhotoFragment extends DialogFragment {

  private LocationPhotoController controller;
  private QRCode qrCode;
  private QRCodeFragment qrCodeFragment;
  private CameraLocationPhotoController cameraController;

  public static LocationPhotoFragment newInstance(QRCode qrCode, QRCodeFragment qrCodeFragment) {
    // TODO: May need to bundle arguments
    Bundle args = new Bundle();
    args.putSerializable("qrcode", qrCode);
    args.putSerializable("qrcodefrag", qrCodeFragment);
    LocationPhotoFragment fragment = new LocationPhotoFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_location_photo, null);
    qrCode = (QRCode) getArguments().getSerializable("qrcode");
    qrCodeFragment = (QRCodeFragment) getArguments().getSerializable("qrcodefrag");
    controller = new LocationPhotoController(this, qrCode);
    cameraController =
        new CameraLocationPhotoController(
            this, view.findViewById(R.id.locationPhotoCameraPreview), controller);
    FloatingActionButton shutterButton = view.findViewById(R.id.location_photo_shutter);
    shutterButton.setOnClickListener(
        v -> {
          controller.takePhoto();
        });
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    return builder.setView(view).create();
  }

  @Override
  public void onDismiss(@NonNull DialogInterface dialog) {
    super.onDismiss(dialog);
    qrCodeFragment.updateLocationPhoto();
  }
}
