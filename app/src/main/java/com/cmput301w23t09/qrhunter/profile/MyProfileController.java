package com.cmput301w23t09.qrhunter.profile;

import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.util.DeviceUtils;

public class MyProfileController extends ProfileController {

  /**
   * This initializes the controller with its corresponding fragment
   *
   * @param fragment This is the fragment the controller manages
   * @param gameController The game controller that controls the global view
   */
  public MyProfileController(ProfileFragment fragment, GameController gameController) {
    super(fragment, gameController, DeviceUtils.getDeviceUUID(gameController.getActivity()));
  }

  @Override
  protected void updateFollowDetails() {
    PlayerDatabase.getInstance()
        .getPlayerByDeviceId(
            deviceUUID,
            results -> {
              if (!results.isSuccessful()) {
                showMsg("An error occurred while loading in your player data.");
                return;
              }

              // Update count
              followingText.setText(
                  fragment.getString(
                      R.string.profile_following, results.getData().getFollowing().size()));
              followersText.setText(
                  fragment.getString(
                      R.string.profile_followers, results.getData().getFollowers().size()));
            });
  }

  @Override
  public void handleContactButtonClick() {
    // Display edit settings fragment
    ProfileSettingsFragment settingsFragment =
        new ProfileSettingsFragment(
            gameController, DeviceUtils.getDeviceUUID(gameController.getActivity()));
    gameController.setBody(settingsFragment);
  }
}
