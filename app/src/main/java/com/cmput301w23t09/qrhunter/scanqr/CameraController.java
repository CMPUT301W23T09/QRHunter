package com.cmput301w23t09.qrhunter.scanqr;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.cmput301w23t09.qrhunter.BaseFragment;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages Camera preview and capture using Google's CameraX library
 * https://developer.android.com/training/camerax
 * (Used ChatGPT to convert Kotlin to Java)
 * <p>
 * Used to scan qr codes and to let user take location pictures
 *
 * @author John Mabanta
 * @version 1.0
 */
public class CameraController {

    private ExecutorService cameraExecutor;
    private BaseFragment fragment;
    private PreviewView previewView;
    private ScannerController scannerController;

    private static final String TAG = "QRHunterCamera";
    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    public static final int REQUEST_CODE_PERMISSIONS = 10;
    public static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
    };

    /**
     * Creates a CameraController only for previewing anc capturing location photos.
     *
     * @param fragment    The fragment that uses the camera.
     * @param previewView The UI element in fragment to show camera preview on.
     */
    public CameraController(BaseFragment fragment, PreviewView previewView) {
        this.fragment = fragment;
        this.previewView = previewView;
        this.scannerController = null;
        if (allPermissionsGranted())
            startCamera();
        else
            fragment.requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * Creates a CameraController for previewing and scanning QR codes.
     *
     * @param fragment          The fragment that uses the camera.
     * @param previewView       The UI element in fragment to show camera preview on.
     * @param scannerController Manages QR Code scanning.
     * @see ScannerController
     */
    public CameraController(BaseFragment fragment, PreviewView previewView,
                            ScannerController scannerController) {
        this(fragment, previewView);
        this.scannerController = scannerController;
    }

    /**
     * Starts the camera and binds it's preview to the PreviewView UI element.
     */
    public void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture
                = ProcessCameraProvider.getInstance(fragment.getContext());
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
                if (scannerController == null)
                    cameraProvider.bindToLifecycle((LifecycleOwner) fragment, cameraSelector, preview);
                else {
                    // Create ImageAnalysis use case if camera is being used to scan QR codes
                    ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build();
                    imageAnalysis.setAnalyzer(cameraExecutor,
                            image -> scannerController.scanCode(image));
                    cameraProvider.bindToLifecycle((LifecycleOwner) fragment, cameraSelector,
                            preview, imageAnalysis);
                }

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }, ContextCompat.getMainExecutor(fragment.getContext()));
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
            if (ContextCompat.checkSelfPermission(fragment.getContext(), permission)
                    == PackageManager.PERMISSION_DENIED)
                return false;
        }
        return true;
    }

}
