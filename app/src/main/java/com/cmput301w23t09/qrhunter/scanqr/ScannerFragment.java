package com.cmput301w23t09.qrhunter.scanqr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cmput301w23t09.qrhunter.BaseFragment;
import com.cmput301w23t09.qrhunter.MainController;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.databinding.FragmentScanqrBinding;

public class ScannerFragment extends BaseFragment {

    private ScannerController scannerController;
    private FragmentScanqrBinding binding;

    public ScannerFragment(MainController mainController) {
        super(mainController);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        scannerController = new ScannerController();
        binding = FragmentScanqrBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
