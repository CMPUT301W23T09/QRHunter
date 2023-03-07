package com.cmput301w23t09.qrhunter.scanqr.camera;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.lifecycle.LifecycleOwner;
import com.cmput301w23t09.qrhunter.scanqr.ScannerController;
import com.cmput301w23t09.qrhunter.scanqr.ScannerFragment;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.ExecutionException;

public class CameraScannerController extends CameraController {

  private ScannerController scannerController;

  /**
   * Creates a CameraScannerController for previewing and scanning QR codes
   *
   * @param fragment The fragment that uses the camera.
   * @param previewView The UI element in fragment to show camera preview on.
   * @param scannerController Manages QR Code scanning.
   * @see ScannerController
   */
  public CameraScannerController(
      ScannerFragment fragment, PreviewView previewView, ScannerController scannerController) {
    super(fragment, previewView);
    this.scannerController = scannerController;
  }

  /**
   * Enables the camera to scan for QR codes using the ImageAnalysis use case
   *
   * @param cameraProviderFuture The camera's provider object
   */
  @Override
  protected void setupCamera(ListenableFuture<ProcessCameraProvider> cameraProviderFuture)
      throws ExecutionException, InterruptedException {
    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

    // Attach preview screen
    Preview preview = new Preview.Builder().build();
    preview.setSurfaceProvider(previewView.getSurfaceProvider());

    // Use back camera
    CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

    // Bind camera
    // Create ImageAnalysis use case if camera is being used to scan QR codes
    ImageAnalysis imageAnalysis =
        new ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build();
    imageAnalysis.setAnalyzer(cameraExecutor, image -> scannerController.scanCode(image));
    cameraProvider.bindToLifecycle(
        (LifecycleOwner) fragment, cameraSelector, preview, imageAnalysis);
  }
}
