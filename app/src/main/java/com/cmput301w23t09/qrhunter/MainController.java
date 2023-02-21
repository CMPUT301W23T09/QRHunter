package com.cmput301w23t09.qrhunter;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.cmput301w23t09.qrhunter.landing.LandingScreenFragment;

/**
 * The MainController handles controlling the content to be shown onscreen.
 */
public class MainController {

    private final MainActivity activity;

    private boolean navbarEnabled = true;
    private Fragment body;
    private DialogFragment popup;


    public MainController(MainActivity activity) {
        this.activity = activity;

        // TODO: Check if user is logged in or not, and then set initial data.
        this.setBody(new LandingScreenFragment(activity.getController()));
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

}
