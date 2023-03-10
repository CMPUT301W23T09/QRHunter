package com.cmput301w23t09.qrhunter.scanqr;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCase;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.Serializable;
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
 */
public class CameraController implements Serializable {

  private ExecutorService cameraExecutor;
  private Fragment fragment;
  private PreviewView scannerPreview;

  private ImageCapture imageCapture;
  private ImageAnalysis imageAnalysis;
  private CameraSelector cameraSelector;

  private ScannerController scannerController;

  private static CameraController instance;

  public static final int REQUEST_CODE_PERMISSIONS = 10;
  public static final String[] CAMERA_PERMISSIONS =
      new String[] {
        Manifest.permission.CAMERA,
      };

  // Define a private constructor as we CameraController is a singleton
  private CameraController() {}

  /**
   * Gets the instance of the current CameraController
   *
   * @return The active CameraController
   */
  public static CameraController getInstance() {
    if (instance == null) {
      synchronized (CameraController.class) {
        if (instance == null) instance = new CameraController();
      }
    }
    return instance;
  }

  /**
   * Initializes the CameraController
   *
   * @param fragment The fragment that uses the camera.
   * @param scannerPreview The Scanner PreviewView where the camera projects its view onto.
   * @param scannerController The ScannerController responsible for taking in scanned QR codes.
   */
  public void init(
      Fragment fragment, PreviewView scannerPreview, ScannerController scannerController) {
    this.fragment = fragment;
    this.scannerPreview = scannerPreview;
    this.scannerController = scannerController;
    cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA; // Always use back camera
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
            // Setup ImageCapture use case for taking location photos
            // This will be bound later when we switch use cases
            imageCapture =
                new ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build();

            // Setup ImageAnalysis use case for scanning QRCodes
            imageAnalysis =
                new ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build();
            imageAnalysis.setAnalyzer(cameraExecutor, image -> scannerController.scanCode(image));

            // Initially, camera is set to scan for QR codes
            useScanner();
          } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
          }
        },
        ContextCompat.getMainExecutor(fragment.getContext()));
  }

  /** Shut down the camera when it's no longer needed */
  public void onDestroy() {
    cameraExecutor.shutdown();
  }

  public void useScanner() throws ExecutionException, InterruptedException {
    Preview preview = new Preview.Builder().build();
    preview.setSurfaceProvider(scannerPreview.getSurfaceProvider());
    rebindUseCases(preview, imageAnalysis);
  }

  public void useLocationPhoto(PreviewView locationPhotoPreview)
      throws ExecutionException, InterruptedException {
    Preview preview = new Preview.Builder().build();
    preview.setSurfaceProvider(locationPhotoPreview.getSurfaceProvider());
    rebindUseCases(preview, imageCapture);
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

  public ExecutorService getCameraExecutor() {
    return cameraExecutor;
  }

  public ImageCapture getImageCapture() {
    return imageCapture;
  }

  private void rebindUseCases(UseCase... useCases) throws ExecutionException, InterruptedException {
    ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
        ProcessCameraProvider.getInstance(fragment.getContext());
    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
    cameraProvider.unbindAll();
    cameraProvider.bindToLifecycle((LifecycleOwner) fragment, cameraSelector, useCases);
  }
}
