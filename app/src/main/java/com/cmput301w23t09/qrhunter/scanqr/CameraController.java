package com.cmput301w23t09.qrhunter.scanqr;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages Camera preview and capture using Google's CameraX library
 * https://developer.android.com/training/camerax
 * (Used ChatGPT to convert Kotlin to Java)
 *
 * @author John Mabanta
 * @version 1.0
 */
public class CameraController {

    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private ScannerFragment scannerFragment;

    private static final String TAG = "QRHunterCamera";
    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    public static final int REQUEST_CODE_PERMISSIONS = 10;
    public static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA
    };

    public CameraController(ScannerFragment scannerFragment) {
        this.scannerFragment = scannerFragment;
        if (!allPermissionsGranted())
            scannerFragment.requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * Starts the camera and binds it's preview to the PreviewView UI element.
     *
     * @param previewView The PreviewView UI element to show the camera preview on.
     */
    public void startCamera(PreviewView previewView) {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture
                = ProcessCameraProvider.getInstance(scannerFragment.getContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Attach preview screen
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Use back camera
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                // Bind camera
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle((LifecycleOwner) scannerFragment, cameraSelector, preview);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }, ContextCompat.getMainExecutor(scannerFragment.getContext()));
    }

    public void onDestroy() {
        cameraExecutor.shutdown();
    }

    /**
     * Checks if all required permissions are granted
     *
     * @return True if every permission under CameraController.REQUIRED_PERMISSIONS is granted
     */
    public boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(scannerFragment.getContext(), permission)
                    == PackageManager.PERMISSION_DENIED)
                return false;
        }
        return true;
    }

}
