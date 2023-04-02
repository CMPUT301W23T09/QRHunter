package com.cmput301w23t09.qrhunter.profile;

import android.view.View;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;

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
    return new MyProfileController(this, getGameController());
  }

  @Override
  protected void setupSocialMethods() {
    contactButton.setImageResource(R.drawable.baseline_settings_24);
    followButton.setVisibility(View.INVISIBLE);
  }
}
