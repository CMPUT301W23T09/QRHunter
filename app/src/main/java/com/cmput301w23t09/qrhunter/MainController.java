package com.cmput301w23t09.qrhunter;

import androidx.fragment.app.Fragment;

import com.cmput301w23t09.qrhunter.landing.LandingFragment;

/**
 * The MainController handles controlling the content to be shown onscreen.
 */
public class MainController {

    private final MainActivity activity;

    private boolean navbarEnabled = true;
    private Fragment body;
    private Fragment popup;


    public MainController(MainActivity activity) {
        this.activity = activity;

        // TODO: Check if user is logged in or not, and then set initial data.
        // this.setNavbarEnabled(false);
        this.setBody(new LandingFragment(activity.getController()));
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
     * Set the current body fragment.
     * @param fragment fragment to display as the body of the screen.
     */
    public void setBody(Fragment fragment) {
        if (fragment != body) {
            body = fragment;
            getActivity().onControllerBodyUpdate(fragment);
        }
    }

    /**
     * Retrieve the current popup fragment.
     * @return current popup fragment.
     */
    public Fragment getPopup() {
        return popup;
    }

    /**
     * Change the current popup fragment.
     * @param fragment popup fragment to display on screen.
     */
    public void setPopup(Fragment fragment) {
        if (popup != fragment) {
            popup = fragment;
        }
    }

}
