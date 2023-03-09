package com.cmput301w23t09.qrhunter.scanqr.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.cmput301w23t09.qrhunter.scanqr.ScannerController;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages Camera preview and capture using Google's CameraX library
 * https://developer.android.com/training/camerax (Used ChatGPT to convert Kotlin to Java)
 *
 * <p>Extended in subclasses to add functionality, such as QRCode scanning and photo taking
 * (location photos)
 *
 * @author John Mabanta
 * @version 1.0
 * @see CameraScannerController
 */
public abstract class CameraController {

  protected ExecutorService cameraExecutor;
  protected Fragment fragment;
  protected PreviewView previewView;
  protected ScannerController scannerController;

  public static final int REQUEST_CODE_PERMISSIONS = 10;
  public static final String[] CAMERA_PERMISSIONS =
      new String[] {
        Manifest.permission.CAMERA,
      };

  /**
   * Creates a basic CameraController that displays its preview to a view.
   *
   * @param fragment The fragment that uses the camera.
   * @param previewView The UI element in fragment to show camera preview on.
   */
  public CameraController(Fragment fragment, PreviewView previewView) {
    this.fragment = fragment;
    this.previewView = previewView;
    this.scannerController = null;
    if (cameraPermissionsGranted()) startCamera();
    else fragment.requestPermissions(CAMERA_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
    cameraExecutor = Executors.newSingleThreadExecutor();
  }

  /** Starts the camera and binds it's preview to the PreviewView UI element. */
  public void startCamera() {
    ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
        ProcessCameraProvider.getInstance(fragment.getContext());
    cameraProviderFuture.addListener(
        () -> {
          try {
            setupCamera(cameraProviderFuture);
          } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
          }
        },
        ContextCompat.getMainExecutor(fragment.getContext()));
  }

  /**
   * Adds in the camera's functionality
   *
   * @param cameraProviderFuture The camera's provider object
   */
  protected abstract void setupCamera(ListenableFuture<ProcessCameraProvider> cameraProviderFuture)
      throws ExecutionException, InterruptedException;

  /** Shut down the camera when it's no longer needed */
  public void onDestroy() {
    cameraExecutor.shutdown();
  }

  /**
   * Checks if all required permissions are granted
   *
   * @return True if every permission under CameraController.REQUIRED_PERMISSIONS is granted
   */
  public boolean cameraPermissionsGranted() {
    for (String permission : CAMERA_PERMISSIONS) {
      if (ContextCompat.checkSelfPermission(fragment.getContext(), permission)
          == PackageManager.PERMISSION_DENIED) return false;
    }
    return true;
  }
}
