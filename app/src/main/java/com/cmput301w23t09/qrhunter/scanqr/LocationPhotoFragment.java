package com.cmput301w23t09.qrhunter.scanqr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.scanqr.camera.CameraLocationPhotoController;

public class LocationPhotoFragment extends DialogFragment {

  private LocationPhotoController controller;
  private ScannerFragment scannerFragment;
  private CameraLocationPhotoController cameraController;

  public static LocationPhotoFragment newInstance() {
    // TODO: May need to bundle arguments
    LocationPhotoFragment fragment = new LocationPhotoFragment();
    return fragment;
  }

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_location_photo, null);
    controller = new LocationPhotoController(this);
    cameraController =
        new CameraLocationPhotoController(
            this, view.findViewById(R.id.locationPhotoCameraPreview), controller);
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    return builder.setView(view).create();
  }
}
