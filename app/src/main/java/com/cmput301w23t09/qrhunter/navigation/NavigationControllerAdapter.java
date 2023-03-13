package com.cmput301w23t09.qrhunter.navigation;

import android.view.MenuItem;
import androidx.annotation.NonNull;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.leaderboard.LeaderboardFragment;
import com.cmput301w23t09.qrhunter.profile.ProfileFragment;
import com.cmput301w23t09.qrhunter.scanqr.ScannerFragment;
import com.cmput301w23t09.qrhunter.util.DeviceUtils;
import com.google.android.material.navigation.NavigationBarView;
import java.util.UUID;

/** The NavigationController handles controlling the content to display. */
public class NavigationControllerAdapter implements NavigationBarView.OnItemSelectedListener {
  private final GameController gameController;

  public NavigationControllerAdapter(GameController gameController) {
    this.gameController = gameController;
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    int selectedItemId = item.getItemId();

    // We avoid a switch statement here as it is not recommended to have ids as case statements.
    if (selectedItemId == R.id.navigation_scan_qr) {
      gameController.setBody(new ScannerFragment(gameController));
    } else if (selectedItemId == R.id.navigation_my_profile) {
      UUID deviceUUID = DeviceUtils.getDeviceUUID(gameController.getActivity());
      gameController.setBody(new ProfileFragment(gameController, deviceUUID));
    } else if (selectedItemId == R.id.navigation_qr_finder) {
      // mainController.setBody(new QRFinderFragment());
    } else if (selectedItemId == R.id.navigation_social) {
      gameController.setBody(new LeaderboardFragment(gameController));
    } else {
      throw new UnsupportedOperationException(
          "The navigation item id provided is missing a handler.");
    }
    return true;
  }
}
