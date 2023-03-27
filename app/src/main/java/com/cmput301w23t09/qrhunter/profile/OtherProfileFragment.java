package com.cmput301w23t09.qrhunter.profile;

import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import java.util.UUID;

public class OtherProfileFragment extends ProfileFragment {

  /** This is the UUID corresponding to the profile of this player */
  private UUID deviceUUID;

  /**
   * Initializes the fragment with the app controller
   *
   * @param gameController This is the app controller
   * @param playerDeviceId The device id this profile is for.
   */
  public OtherProfileFragment(GameController gameController, UUID playerDeviceId) {
    super(gameController);
    deviceUUID = playerDeviceId;
  }

  @Override
  public ProfileController getProfileController() {
    return new ProfileController(this, getGameController(), deviceUUID);
  }

  @Override
  protected void setupContactButton() {
    contactButton.setImageResource(R.drawable.info_button);
  }
}
