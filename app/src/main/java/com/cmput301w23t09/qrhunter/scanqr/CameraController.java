package com.cmput301w23t09.qrhunter.scanqr;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.camera.core.ImageCapture;
import androidx.core.content.ContextCompat;

import java.util.concurrent.ExecutorService;

/**
 * Manages Camera preview and capture using Google's CameraX library
 * https://developer.android.com/training/camerax
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
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.CAMERA
    };

    public CameraController(ScannerFragment scannerFragment) {
        this.scannerFragment = scannerFragment;
    }

    private boolean checkPermissionsGranted() {
        for (String permission: REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(scannerFragment.getContext(), permission) == PackageManager.PERMISSION_DENIED)
                return false;
        }
        return true;
    }

    private void requestPermissions() {
        scannerFragment.requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
    }

    public void onDestroy() {
        cameraExecutor.shutdown();
    }


}
