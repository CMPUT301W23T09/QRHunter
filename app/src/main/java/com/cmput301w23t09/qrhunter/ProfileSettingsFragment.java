package com.cmput301w23t09.qrhunter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ProfileSettingsFragment extends BaseFragment {

  public ProfileSettingsFragment(GameController gameController) {
    super(gameController);
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    return inflater.inflate(R.layout.fragment_profile_settings, container, false);
  }
}
