package com.cmput301w23t09.qrhunter.profile;

import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import java.util.UUID;

public class OtherProfileController extends ProfileController {

  /**
   * This initializes the controller with its corresponding fragment
   *
   * @param fragment This is the fragment the controller manages
   * @param gameController The game controller that controls the global view
   * @param deviceUUID Device UUID of the profile.
   */
  public OtherProfileController(
      ProfileFragment fragment, GameController gameController, UUID deviceUUID) {
    super(fragment, gameController, deviceUUID);
  }

  @Override
  public void handleContactButtonClick() {
    // Display contact info popup
    PlayerDatabase.getInstance()
        .getPlayerByDeviceId(
            deviceUUID,
            task -> {
              if (task.getException() != null) {
                showMsg(
                    "An exception occurred while trying to load this player's contact details.");
                return;
              }

              Player player = task.getData();
              fragment.displayContactInfo(player.getEmail(), player.getPhoneNo());
            });
  }
}
