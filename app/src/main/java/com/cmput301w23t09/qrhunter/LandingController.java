package com.cmput301w23t09.qrhunter;

import android.content.Intent;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.util.DeviceUtils;
import com.cmput301w23t09.qrhunter.util.ValidationUtils;
import java.util.ArrayList;
import java.util.UUID;

/**
 * The MainController handles controlling whether or not to log the player in and also registration.
 */
public class LandingController {
  private final LandingActivity activity;

  public LandingController(LandingActivity activity) {
    this.activity = activity;

    // Check if player is registered to determine which screen to show on launch.
    PlayerDatabase.getInstance()
        .getPlayerByDeviceId(
            DeviceUtils.getDeviceUUID(activity),
            results -> {
              if (!results.isSuccessful()) {
                activity.displayToast("An error occurred while loading in your player data.");
                return;
              }

              if (results.getData() != null) {
                // Player has existing data, switch to GameActivity.
                switchToGameActivity(results.getData());
                return;
              }

              // Otherwise show the registration screen.
              activity.showLandingPage();
            });
  }

  /**
   * Attempts to register player given the credentials
   *
   * @param username username to register account with
   * @param phoneNo phone number to register account with
   * @param email email to register account with
   */
  public void onRegistration(String username, String phoneNo, String email) {
    // Validate user information first.
    if (!checkIfInputIsValid(username, phoneNo, email)) {
      return;
    }

    // Does an existing player already have this username?
    PlayerDatabase.getInstance()
        .getPlayerByUsername(
            username,
            results -> {
              if (!results.isSuccessful()) {
                activity.displayRegistrationError(
                    "An exception occurred while fetching player data from the database.");
                return;
              }

              if (results.getData() != null) {
                activity.displayRegistrationError("The username is already in use.");
                return;
              }

              // Register the player with the details provided
              onSuccessfulRegistrationDetails(username, phoneNo, email);
            });
  }

  /**
   * Called after verifying that user credentials are valid and that no other user owns the
   * requested username. Adds the player to the database and logs the player in.
   *
   * @param username username to add
   * @param phoneNo phone number to add
   * @param email email to add
   */
  private void onSuccessfulRegistrationDetails(String username, String phoneNo, String email) {
    UUID deviceUUID = DeviceUtils.getDeviceUUID(activity);
    Player player =
        new Player(deviceUUID, username, phoneNo, email, new ArrayList<>(), new ArrayList<>());

    PlayerDatabase.getInstance()
        .add(
            player,
            addResults -> {
              if (!addResults.isSuccessful()) {
                activity.displayRegistrationError(
                    "An exception occurred while registering your user credentials.");
                return;
              }

              // Successfully registered.
              switchToGameActivity(addResults.getData());
            });
  }

  /**
   * Checks if the user details provided meet the validation rules. Failure to meet any requirement
   * sends an error message to the view.
   *
   * @param username username to check
   * @param phoneNo phone number to check
   * @param email email to check.
   * @return if validation was successful.
   */
  private boolean checkIfInputIsValid(String username, String phoneNo, String email) {
    if (!ValidationUtils.isValidUsername(username)) {
      activity.displayRegistrationError("Username must be between 1 and 20 characters.");
      return false;
    } else if (!ValidationUtils.isValidPhoneNo(phoneNo)) {
      activity.displayRegistrationError("Invalid phone number.");
      return false;
    } else if (!ValidationUtils.isValidEmail(email)) {
      activity.displayRegistrationError("Invalid email.");
      return false;
    }

    return true;
  }

  /** Switches the current activity to the game activity. */
  private void switchToGameActivity(Player player) {
    Intent switchToGameActivityIntent = new Intent(activity, GameActivity.class);
    switchToGameActivityIntent.putExtra("activePlayer", player);
    switchToGameActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    activity.startActivity(switchToGameActivityIntent);
  }
}
