package com.cmput301w23t09.qrhunter.profile;

import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.util.DeviceUtils;

public class MyProfileFragment extends ProfileFragment {

  /**
   * Initializes the fragment with the app controller
   *
   * @param gameController This is the app controller
   */
  public MyProfileFragment(GameController gameController) {
    super(gameController);
  }

  @Override
  public ProfileController getProfileController() {
    return new ProfileController(
        this, getGameController(), DeviceUtils.getDeviceUUID(getGameController().getActivity()));
  }

  @Override
  protected void setupContactButton() {
    contactButton.setImageResource(R.drawable.baseline_settings_24);
  }
}
