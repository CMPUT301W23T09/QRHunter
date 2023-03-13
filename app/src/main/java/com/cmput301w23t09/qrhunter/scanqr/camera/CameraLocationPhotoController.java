package com.cmput301w23t09.qrhunter.scanqr.camera;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.lifecycle.LifecycleOwner;
import com.cmput301w23t09.qrhunter.scanqr.LocationPhotoController;
import com.cmput301w23t09.qrhunter.scanqr.LocationPhotoFragment;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Enables the ImageCapture camera use case to let players take location photos.
 *
 * @author John Mabanta
 * @version 1.0
 * @see CameraController
 */
public class CameraLocationPhotoController extends CameraController {

  private LocationPhotoController controller;
  private ImageCapture imageCapture;

  /**
   * Creates a CameraLocationPhotoController that can take location photos
   *
   * @param fragment The fragment that uses the camera
   * @param previewView The UI element in fragment to show camera preview on
   * @param controller location photo controller
   */
  public CameraLocationPhotoController(
      LocationPhotoFragment fragment, PreviewView previewView, LocationPhotoController controller) {
    super(fragment, previewView);
    this.controller = controller;
  }

  /**
   * Enables the camera to take photos using the ImageCapture use case
   *
   * @param cameraProviderFuture The camera's provider object
   * @throws ExecutionException
   * @throws InterruptedException
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
    // cameraProvider.unbindAll();
    // Create ImageCapture use case to allow for location photo taking
    imageCapture =
        new ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build();
    controller.setCameraFields(cameraExecutor, imageCapture);
    cameraProvider.bindToLifecycle(
        (LifecycleOwner) fragment, cameraSelector, preview, imageCapture);
  }
}
