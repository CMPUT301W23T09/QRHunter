package com.cmput301w23t09.qrhunter.profile;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.database.DatabaseQueryResults;
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

  @Override
  public void setupFollowDetails(
      TextView followingText, TextView followersText, Button followButton) {
    super.setupFollowDetails(followingText, followersText, followButton);
    followButton.setVisibility(View.VISIBLE);
  }

  public void handleFollowButtonClick() {
    if (followButton.isEnabled()) {
      followButton.setEnabled(false);
      followButton.setText(R.string.ellipses);

      // Get the latest following data of the profile user
      PlayerDatabase.getInstance()
          .getPlayerByDeviceId(
              deviceUUID,
              otherPlayer -> {
                if (!otherPlayer.isSuccessful()) {
                  Toast.makeText(
                          fragment.getContext(),
                          "An exception occurred while issuing a follow action.",
                          Toast.LENGTH_SHORT)
                      .show();
                  return;
                }

                // Which action do we take? follow or unfollow?
                boolean weFollowingTheUser =
                    otherPlayer
                        .getData()
                        .getFollowers()
                        .contains(gameController.getActivePlayer().getDeviceId());
                if (weFollowingTheUser) {
                  // Unfollow request
                  PlayerDatabase.getInstance()
                      .unfollow(
                          gameController.getActivePlayer(),
                          otherPlayer.getData(),
                          this::onUnfollowTask);
                } else {
                  // Follow request
                  PlayerDatabase.getInstance()
                      .follow(
                          gameController.getActivePlayer(),
                          otherPlayer.getData(),
                          this::onFollowTask);
                }
              });
    }
  }

  private void onUnfollowTask(DatabaseQueryResults<Void> task) {
    if (!task.isSuccessful()) {
      Toast.makeText(
              fragment.getContext(), "An exception occurred while unfollowing.", Toast.LENGTH_SHORT)
          .show();
      return;
    }
    updateFollowDetails();
  }

  private void onFollowTask(DatabaseQueryResults<Void> task) {
    if (!task.isSuccessful()) {
      Toast.makeText(
              fragment.getContext(), "An exception occurred while following.", Toast.LENGTH_SHORT)
          .show();
      return;
    }
    updateFollowDetails();
  }
}
