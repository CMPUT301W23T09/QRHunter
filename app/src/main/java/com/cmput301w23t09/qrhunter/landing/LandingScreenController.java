package com.cmput301w23t09.qrhunter.landing;

import android.telephony.PhoneNumberUtils;

import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.util.ValidationUtils;

import java.util.UUID;

public class LandingScreenController {

    private final LandingScreenFragment landingScreenFragment;

    public LandingScreenController(LandingScreenFragment fragment) {
        landingScreenFragment = fragment;
    }

    /**
     * Attempts to register player given the credentials
     * @param username username to register account with
     * @param phoneNo phone number to register account with
     * @param email email to register account with
     */
    public void onRegistration(String username, String phoneNo, String email) {
        // Validate user information first.
        if (!ValidationUtils.isValidUsername(username)) {
            landingScreenFragment.displayRegistrationError("Username must be between 1 and 20 characters.");
            return;
        } else if (!ValidationUtils.isValidPhoneNo(phoneNo)) {
            landingScreenFragment.displayRegistrationError("Invalid phone number.");
            return;
        } else if (!ValidationUtils.isValidEmail(email)) {
            landingScreenFragment.displayRegistrationError("Invalid email.");
            return;
        }

        // Does an existing player already have this username?
        PlayerDatabase.getInstance().getPlayerByUsername(username, results -> {
            if (!results.isSuccessful()) {
                landingScreenFragment.displayRegistrationError("An exception occurred while fetching player data from the database.");
                return;
            }

            if (results.getData() != null) {
                landingScreenFragment.displayRegistrationError("The username is already in use.");
                return;
            }

            // Register player into database.
            UUID deviceUUID = landingScreenFragment.getMainController().getDeviceUUID();
            Player player = new Player(deviceUUID, username, phoneNo, email);
            PlayerDatabase.getInstance().add(player, addResults -> {
                if (!addResults.isSuccessful()) {
                    landingScreenFragment.displayRegistrationError("An exception occurred while registering your user credentials.");
                    return;
                }

                // TODO: Change screen to ScanQR screen as user is now FULLY registered.
            });
        });
    }

}
