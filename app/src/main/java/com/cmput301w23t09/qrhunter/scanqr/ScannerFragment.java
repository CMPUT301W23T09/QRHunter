package com.cmput301w23t09.qrhunter.scanqr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cmput301w23t09.qrhunter.BaseFragment;
import com.cmput301w23t09.qrhunter.MainController;
import com.cmput301w23t09.qrhunter.databinding.FragmentScanqrBinding;

public class ScannerFragment extends BaseFragment {

    private ScannerController scannerController;
    private CameraController cameraController;
    private FragmentScanqrBinding binding;

    public ScannerFragment(MainController mainController) {
        super(mainController);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentScanqrBinding.inflate(inflater, container, false);
        scannerController = new ScannerController();
        cameraController = new CameraController(this, binding.viewFinder);
        return binding.getRoot();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == CameraController.REQUEST_CODE_PERMISSIONS) {
            if (cameraController.allPermissionsGranted()) {
                cameraController.startCamera();
            } else {
                Toast.makeText(getContext(), "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
