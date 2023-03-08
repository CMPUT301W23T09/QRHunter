package com.cmput301w23t09.qrhunter.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cmput301w23t09.qrhunter.BaseFragment;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;

public class ProfileSettingsFragment extends BaseFragment {
  private ProfileSettingsController controller;

  public ProfileSettingsFragment(GameController gameController) {
    super(gameController);
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    controller = new ProfileSettingsController(getMainController());
    View view = inflater.inflate(R.layout.fragment_profile_settings, container, false);

    view.findViewById(R.id.settings_back_button)
        .setOnClickListener(v -> controller.returnToProfile());

    return view;
  }
}
