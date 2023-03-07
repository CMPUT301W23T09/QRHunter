package com.cmput301w23t09.qrhunter.scanqr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cmput301w23t09.qrhunter.BaseFragment;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.databinding.FragmentScanqrBinding;
import com.cmput301w23t09.qrhunter.scanqr.camera.CameraController;
import com.cmput301w23t09.qrhunter.scanqr.camera.CameraScannerController;

/**
 * The 'Scan QR' page where users can use their rear camera to scan QR codes
 *
 * @author John Mabanta
 * @version 1.0
 */
public class ScannerFragment extends BaseFragment {

  private ScannerController scannerController;
  private CameraScannerController cameraController;
  private FragmentScanqrBinding binding;

  public ScannerFragment(GameController gameController) {
    super(gameController);
  }

  @Nullable @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentScanqrBinding.inflate(inflater, container, false);
    scannerController = new ScannerController(this);
    cameraController =
        new CameraScannerController(this, binding.scanQrCameraPreview, scannerController);
    return binding.getRoot();
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == CameraController.REQUEST_CODE_PERMISSIONS) {
      if (cameraController.allPermissionsGranted()) {
        cameraController.startCamera();
      } else {
        Toast.makeText(getContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT)
            .show();
      }
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    cameraController.onDestroy();
  }
}
