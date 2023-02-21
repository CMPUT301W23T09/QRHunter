package com.cmput301w23t09.qrhunter.landing;

import android.telephony.PhoneNumberUtils;
import android.widget.Toast;

import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;

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
        if (!isValidUsername(username)) {
            landingScreenFragment.displayErrorMessage("Username must be between 1 and 20 characters.");
            return;
        } else if (!isValidPhoneNo(phoneNo)) {
            landingScreenFragment.displayErrorMessage("Invalid phone number.");
            return;
        } else if (!isValidEmail(email)) {
            landingScreenFragment.displayErrorMessage("Invalid email.");
            return;
        }

        // Does an existing player already have this username?
        Player existingPlayerByUsername = PlayerDatabase.getInstance().getPlayerByUsername(username);
        if (existingPlayerByUsername != null) {
            landingScreenFragment.displayErrorMessage("The username is already in use.");
            return;
        }

        // Register player into database.
        UUID deviceUUID = landingScreenFragment.getMainController().getDeviceUUID();
        Player player = new Player();

        // TODO: Change screen to ScanQR page when complete as user is now logged in.
    }

    /**
     * Checks if the provided username is a valid username that is between lengths 1-20 inclusive.
     * @param username username to check
     * @return if the username is valid
     */
    private boolean isValidUsername(String username) {
        return username.length() > 0 && username.length() <= 20;
    }

    /**
     * Checks if the provided phone number is a valid phone number.
     * @param phoneNo phone number to check
     * @return if the phone number is valid
     */
    private boolean isValidPhoneNo(String phoneNo) {
        return PhoneNumberUtils.isGlobalPhoneNumber(phoneNo);
    }

    /**
     * Checks if the provided email is as valid email.
     * @param email email to check
     * @return if the email is valid
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[\\w\\d.]+@[\\w\\d.]+\\.[\\w\\d.]+\\w$");
    }

}
