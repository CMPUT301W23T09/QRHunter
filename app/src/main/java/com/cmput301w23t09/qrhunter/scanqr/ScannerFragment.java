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

  /**
   * Creates the ScannerFragment where users can scan and add new QR Codes to their profile
   *
   * @param gameController
   */
  public ScannerFragment(GameController gameController) {
    super(gameController);
  }

  /**
   * Sets up the camera preview and ImageAnalysis usage on the ScannerFragment
   *
   * @param inflater The LayoutInflater object that can be used to inflate any views in the
   *     fragment,
   * @param container If non-null, this is the parent view that the fragment's UI should be attached
   *     to. The fragment should not add the view itself, but this can be used to generate the
   *     LayoutParams of the view.
   * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
   *     saved state as given here.
   * @return The view that will show the user the 'Scan QR' page.
   */
  @Nullable @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentScanqrBinding.inflate(inflater, container, false);
    scannerController = new ScannerController(this, getActivePlayer());
    cameraController =
        new CameraScannerController(this, binding.scanQrCameraPreview, scannerController);
    return binding.getRoot();
  }

  /**
   * Requests user to give app camera permissions
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
    if (requestCode == CameraController.REQUEST_CODE_PERMISSIONS) {
      if (cameraController.cameraPermissionsGranted()) {
        cameraController.startCamera();
      } else {
        Toast.makeText(getContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT)
            .show();
      }
    }
  }

  /**
   * Cleans up the camera when the user navigates from 'Scan QR', or when fragment is garbage
   * collected
   */
  @Override
  public void onDestroy() {
    super.onDestroy();
    cameraController.onDestroy();
  }
}
