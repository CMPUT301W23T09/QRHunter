package com.cmput301w23t09.qrhunter;

import android.content.SharedPreferences;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.cmput301w23t09.qrhunter.landing.LandingScreenFragment;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;

import java.util.UUID;

/**
 * The MainController handles controlling the content to be shown onscreen.
 */
public class GameController {
    private static final String DEVICE_UUID_FILE = "device_uuid.dat";
    private static final String DEVICE_UUID_FILE_FIELD = "uuid";

    private final MainActivity activity;

    private boolean navbarEnabled = true;
    private Fragment body;
    private DialogFragment popup;


    public GameController(MainActivity activity) {
        this.activity = activity;
        this.setNavbarEnabled(false);

        // Check if player is registered to determine which screen to show on launch.
        PlayerDatabase.getInstance().getPlayerByDeviceId(getDeviceUUID(), results -> {
            if (!results.isSuccessful()) {
                activity.displayToast("An error occurred while loading in your player data.");
                return;
            }

            if (results.getData() != null) {
                // Player has existing data
                this.setNavbarEnabled(true);
                // TODO: Show ScanQR screen
            } else {
                // Player has no data
                this.setBody(new LandingScreenFragment(this));
            }
        });
    }

    /**
     * Retrieve the MainActivity this controller controls.
     * @return MainActivity
     */
    public MainActivity getActivity() {
        return activity;
    }

    /**
     * Retrieve if the navbar is currently enabled.
     * @return if the navbar is enabled
     */
    public boolean isNavbarEnabled() {
        return this.navbarEnabled;
    }

    /**
     * Modify whether or not the navbar should be enabled.
     * @param enabled if the navbar should be enabled.
     */
    public void setNavbarEnabled(boolean enabled) {
        if (this.navbarEnabled != enabled) {
            this.navbarEnabled = enabled;
            getActivity().onControllerNavbarVisibilityUpdate(enabled);
        }
    }

    /**
     * Retrieve the current body fragment.
     * @return body fragment or null if none is set.
     */
    public Fragment getBody() {
        return body;
    }

    /**
     * Set the current body fragment to another fragment or null to show no fragment.
     * @param fragment fragment to display as the body of the screen.
     */
    public void setBody(Fragment fragment) {
        if (fragment != body) {
            body = fragment;
            getActivity().onControllerBodyUpdate(fragment);
        }
    }

    /**
     * Retrieve the current popup dialog.
     * @return current popup dialog.
     */
    public DialogFragment getPopup() {
        if (popup == null || !popup.isVisible()) {
            return null;
        }

        return popup;
    }

    /**
     * Change the current popup fragment.
     * @param dialog popup dialog to display on screen.
     */
    public void setPopup(DialogFragment dialog) {
        if (getPopup() != dialog) {
            popup = dialog;
            getActivity().onControllerPopupUpdate(dialog);
        }
    }

    /**
     * Retrieve the UUID associated with this device.
     * @return device UUID
     */
    public UUID getDeviceUUID() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(DEVICE_UUID_FILE, 0);
        String savedDeviceId = sharedPreferences.getString(DEVICE_UUID_FILE_FIELD, null);

        if (savedDeviceId == null) {
            savedDeviceId = UUID.randomUUID().toString();
            sharedPreferences.edit().putString(DEVICE_UUID_FILE_FIELD, savedDeviceId).apply();
        }

        return UUID.fromString(savedDeviceId);
    }

}
