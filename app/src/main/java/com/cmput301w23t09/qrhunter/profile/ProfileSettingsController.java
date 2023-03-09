package com.cmput301w23t09.qrhunter.profile;

import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.util.DeviceUtils;
import com.cmput301w23t09.qrhunter.util.ValidationUtils;
import java.util.function.Consumer;

public class ProfileSettingsController {

  private final GameController gameController;

  public ProfileSettingsController(GameController gameController) {
    this.gameController = gameController;
  }

  /**
   * Saves the changes of the player's new contact details. It is assumed that the email and phone
   * number are valid.
   *
   * @param email the new email
   * @param phoneNo the new phone number
   * @param callback callback to call on completion or failure. True if completed successfully.
   */
  public void saveChanges(String email, String phoneNo, Consumer<Boolean> callback) {
    assert ValidationUtils.isValidEmail(email);
    assert ValidationUtils.isValidPhoneNo(phoneNo);

    requestPlayerData(
        player -> {
          if (player == null) {
            callback.accept(false);
            return;
          }

          // Update player details
          player.setEmail(email);
          player.setPhoneNo(phoneNo);

          PlayerDatabase.getInstance()
              .update(
                  player,
                  task -> {
                    if (task.getException() != null) {
                      callback.accept(false);
                      return;
                    }

                    callback.accept(true);
                  });
        });
  }

  /** Returns to the profile fragment */
  public void returnToProfile() {
    gameController.setBody(new ProfileFragment(gameController));
  }

  /**
   * Request the player of this settings page.
   *
   * @param callback callback to call with the player or null if an exception occurred while
   *     fetching the player.
   */
  public void requestPlayerData(Consumer<Player> callback) {
    PlayerDatabase.getInstance()
        .getPlayerByDeviceId(
            DeviceUtils.getDeviceUUID(gameController.getActivity()),
            task -> {
              if (task.getException() != null) {
                callback.accept(null);
              } else {
                callback.accept(task.getData());
              }
            });
  }
}
