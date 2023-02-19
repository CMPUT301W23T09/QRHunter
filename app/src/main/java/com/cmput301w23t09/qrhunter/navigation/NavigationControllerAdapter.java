package com.cmput301w23t09.qrhunter.navigation;

import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.cmput301w23t09.qrhunter.MainController;
import com.cmput301w23t09.qrhunter.R;
import com.google.android.material.navigation.NavigationBarView;

/**
 * The NavigationController handles controlling the content to display.
 */
public class NavigationControllerAdapter implements NavigationBarView.OnItemSelectedListener {

    private final MainController mainController;

    public NavigationControllerAdapter(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int selectedItemId = item.getItemId();

        // We avoid a switch statement here as it is not recommended to have ids as case statements.
        if (selectedItemId == R.id.navigation_scan_qr) {
            // mainController.setBody(new ScanQRFragment());
        } else if (selectedItemId == R.id.navigation_my_profile) {
            // mainController.setBody(new ProfileFragment());
        } else if (selectedItemId == R.id.navigation_qr_finder) {
            // mainController.setBody(new QRFinderFragment());
        } else if (selectedItemId == R.id.navigation_social) {
            // mainController.setBody(new SocialFragment());
        } else {
            throw new UnsupportedOperationException("The navigation item id provided is missing a handler.");
        }
        return true;
    }
}