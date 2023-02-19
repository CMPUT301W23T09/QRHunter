package com.cmput301w23t09.qrhunter.landing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cmput301w23t09.qrhunter.BaseFragment;
import com.cmput301w23t09.qrhunter.MainController;
import com.cmput301w23t09.qrhunter.R;

public class LandingFragment extends BaseFragment {

    private LandingController controller;

    public LandingFragment(MainController mainController) {
        super(mainController);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        controller = new LandingController();

        return inflater.inflate(R.layout.fragment_landing, container, false);
    }

}
