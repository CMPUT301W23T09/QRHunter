package com.cmput301w23t09.qrhunter;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.anything;

import android.widget.Button;
import android.widget.TextView;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.profile.OtherProfileFragment;
import com.cmput301w23t09.qrhunter.profile.ProfileFragment;
import com.cmput301w23t09.qrhunter.qrcode.DeleteQRCodeFragment;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;

public class TestOtherProfileFragment extends TestProfileFragment {

  private QRCode higherScoreQR;
  private QRCode lowerScoreQR;

  @Override
  public void setUp() throws Exception {
    higherScoreQR = new QRCode("b5a384ee0ec5a8b625de9b24a96627c9ea5d246b70eb34a3a4f9ee781e581731");
    lowerScoreQR = new QRCode("0424974c68530290458c8d58674e2637f65abc127057957d7b3acbd24c208f93");

    super.setUp();
  }

  @Override
  protected Player getProfilePlayer() {
    return new Player(
        UUID.randomUUID(),
        "DaPerson",
        "123-456-7890",
        "example@example.com",
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>());
  }

  @Override
  protected List<QRCode> getProfileQRCodesToAdd() {
    return new ArrayList<QRCode>() {
      {
        this.add(higherScoreQR);
        this.add(lowerScoreQR);
      }
    };
  }

  @Override
  protected void openProfile() {
    GameActivity gameActivity = (GameActivity) solo.getCurrentActivity();
    OtherProfileFragment otherProfileFragment =
        new OtherProfileFragment(gameActivity.getController(), profilePlayer.getDeviceId());
    gameActivity.getController().setBody(otherProfileFragment);

    await().until(() -> gameActivity.getController().getBody() instanceof ProfileFragment);

    // Wait for the default profile to no longer exist.
    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              TextView usernameView = (TextView) solo.getView(R.id.username);
              TextView totalPoints = (TextView) solo.getView(R.id.total_points);
              Button followButton = (Button) solo.getView(R.id.follow_button);
              return !usernameView.getText().toString().equals("")
                  && !totalPoints.getText().toString().equals("")
                  && !followButton
                      .getText()
                      .toString()
                      .equals(gameActivity.getString(R.string.ellipses));
            });
  }

  @Test
  public void testShouldNotShowDeleteButtonOnQRCodeIfNotOwned() {
    // Click the first QR
    onData(anything()).inAdapterView(withId(R.id.code_list)).atPosition(0).perform(click());

    // DeleteQRCodeFragment should NOT be shown.
    await()
        .atMost(15, TimeUnit.SECONDS)
        .until(
            () -> {
              GameActivity gameActivity = (GameActivity) solo.getCurrentActivity();
              return !(gameActivity.getController().getPopup() instanceof DeleteQRCodeFragment);
            });
  }

  @Test
  public void testShouldShowDeleteButtonOnQRCodeIfOwned() throws InterruptedException {
    // Assign the first QR as owned.
    CountDownLatch addQRToPlayerDBTask = new CountDownLatch(1);
    QRCodeDatabase.getInstance()
        .addPlayerToQR(ourPlayer, higherScoreQR, task -> addQRToPlayerDBTask.countDown());
    addQRToPlayerDBTask.await();

    // Click the first QR
    onData(anything()).inAdapterView(withId(R.id.code_list)).atPosition(0).perform(click());

    // DeleteQRCodeFragment should be shown.
    await()
        .atMost(15, TimeUnit.SECONDS)
        .until(
            () -> {
              GameActivity gameActivity = (GameActivity) solo.getCurrentActivity();
              return gameActivity.getController().getPopup() instanceof DeleteQRCodeFragment;
            });
  }

  /** Tests that clicking the contact info button displays the contact information for the user. */
  @Test
  public void testContactInfoButton() {
    onView(withId(R.id.contact_info_button)).perform(click());

    solo.waitForView(R.id.contact_information_title);

    onView(withId(R.id.contact_email))
        .check(matches(withText(String.format("Email: %s", getProfilePlayer().getEmail()))));
    onView(withId(R.id.contact_phoneNo))
        .check(
            matches(withText(String.format("Phone Number: %s", getProfilePlayer().getPhoneNo()))));
  }

  /** Tests that a player is followed when clicking the follow button. */
  @Test
  public void testShouldFollowPlayer() {
    onView(withId(R.id.follow_button)).perform(click());

    // Wait for unfollow text to appear
    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              Button followButton = (Button) solo.getView(R.id.follow_button);
              String unfollowString = "Unfollow";
              return followButton.getText().toString().equals(unfollowString);
            });

    // Check database entries to ensure everything is correct.

    // First check that our player says that we are following this player.
    AtomicReference<Player> updatedPlayer = new AtomicReference<>();
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(
            () -> {
              // If we have already fetched the player, check that the QRCode is not within the
              // Player.
              Player databasePlayer = updatedPlayer.get();
              if (databasePlayer != null
                  && databasePlayer.getFollowing().contains(profilePlayer.getDeviceId())) {
                return true; // Player was correctly updated!
              }

              // then fetch the latest database saved entry.
              PlayerDatabase.getInstance()
                  .getPlayerByDeviceId(
                      getDeviceUUID(), fetchedPlayer -> updatedPlayer.set(fetchedPlayer.getData()));
              return false; // Try again.
            });

    // Now check that the target profile is storing that we are following them.
    updatedPlayer.set(null);
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(
            () -> {
              // If we have already fetched the player, check that the QRCode is not within the
              // Player.
              Player databasePlayer = updatedPlayer.get();
              if (databasePlayer != null
                  && databasePlayer.getFollowers().contains(ourPlayer.getDeviceId())) {
                return true; // Player was correctly updated!
              }

              // then fetch the latest database saved entry.
              PlayerDatabase.getInstance()
                  .getPlayerByDeviceId(
                      profilePlayer.getDeviceId(),
                      fetchedPlayer -> updatedPlayer.set(fetchedPlayer.getData()));
              return false; // Try again.
            });

    String expectedFollowersString = "1 Followers";
    onView(withId(R.id.followers_count)).check(matches(withText(expectedFollowersString)));
  }

  /** Tests that a player is followed when clicking the follow button. */
  @Test
  public void testShouldUnfollowPlayer() {
    onView(withId(R.id.follow_button)).perform(click());

    // Wait for unfollow text to appear
    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              Button followButton = (Button) solo.getView(R.id.follow_button);
              String unfollowString = "Unfollow";
              return followButton.getText().toString().equals(unfollowString);
            });

    // Unfollow user
    onView(withId(R.id.follow_button)).perform(click());

    // Wait for follow text to appear
    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              Button followButton = (Button) solo.getView(R.id.follow_button);
              String followString = "Follow";
              return followButton.getText().toString().equals(followString);
            });

    // Check database entries to ensure everything is correct.

    // First check that our player says that we are NOT following this player.
    AtomicReference<Player> updatedPlayer = new AtomicReference<>();
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(
            () -> {
              // If we have already fetched the player, check that the QRCode is not within the
              // Player.
              Player databasePlayer = updatedPlayer.get();
              if (databasePlayer != null
                  && !databasePlayer.getFollowing().contains(profilePlayer.getDeviceId())) {
                return true; // Player was correctly updated!
              }

              // then fetch the latest database saved entry.
              PlayerDatabase.getInstance()
                  .getPlayerByDeviceId(
                      getDeviceUUID(), fetchedPlayer -> updatedPlayer.set(fetchedPlayer.getData()));
              return false; // Try again.
            });

    // Now check that the target profile is storing that we are NOT following them.
    updatedPlayer.set(null);
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(
            () -> {
              // If we have already fetched the player, check that the QRCode is not within the
              // Player.
              Player databasePlayer = updatedPlayer.get();
              if (databasePlayer != null
                  && !databasePlayer.getFollowers().contains(ourPlayer.getDeviceId())) {
                return true; // Player was correctly updated!
              }

              // then fetch the latest database saved entry.
              PlayerDatabase.getInstance()
                  .getPlayerByDeviceId(
                      profilePlayer.getDeviceId(),
                      fetchedPlayer -> updatedPlayer.set(fetchedPlayer.getData()));
              return false; // Try again.
            });

    String expectedFollowersString = "0 Followers";
    onView(withId(R.id.followers_count)).check(matches(withText(expectedFollowersString)));
  }

  /** Tests that after following a user, that the "Following" text on your profile changes. */
  @Test
  public void testShouldUpdateMyProfileFollowedOnFollow() {
    onView(withId(R.id.follow_button)).perform(click());

    // Wait for unfollow text to appear
    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              Button followButton = (Button) solo.getView(R.id.follow_button);
              String unfollowString = "Unfollow";
              return followButton.getText().toString().equals(unfollowString);
            });

    // Navigate to our profile.
    onView(withId(R.id.navigation_my_profile)).perform(click());

    // Check for the following count to be updated.
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(
            () -> {
              TextView textView = (TextView) solo.getView(R.id.following_count);
              return textView.getText().toString().equals("1 Following");
            });
  }
}
