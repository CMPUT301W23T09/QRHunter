package com.cmput301w23t09.qrhunter;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.anything;

import android.widget.TextView;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.profile.ProfileFragment;
import com.cmput301w23t09.qrhunter.profile.ProfileSettingsFragment;
import com.cmput301w23t09.qrhunter.qrcode.DeleteQRCodeFragment;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;

public class TestMyProfileFragment extends TestProfileFragment {

  @Override
  protected Player getProfilePlayer() {
    return ((GameActivity) solo.getCurrentActivity()).getController().getActivePlayer();
  }

  @Override
  protected List<QRCode> getProfileQRCodesToAdd() {
    return new ArrayList<QRCode>() {
      {
        this.add(new QRCode("0424974c68530290458c8d58674e2637f65abc127057957d7b3acbd24c208f93"));
        this.add(new QRCode("b5a384ee0ec5a8b625de9b24a96627c9ea5d246b70eb34a3a4f9ee781e581731"));
      }
    };
  }

  @Override
  protected void openProfile() {
    // navigate to profile fragment
    onView(withId(R.id.navigation_my_profile)).perform(click());
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(
            () ->
                ((GameActivity) solo.getCurrentActivity()).getController().getBody()
                    instanceof ProfileFragment);

    // Wait for the default profile to no longer exist.
    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              TextView usernameView = (TextView) solo.getView(R.id.username);
              TextView totalPoints = (TextView) solo.getView(R.id.total_points);
              return !usernameView.getText().toString().equals("")
                  && !totalPoints.getText().toString().equals("");
            });
  }

  /** Check that selecting a QR should open up the deletion QR fragment. */
  @Test
  public void testShouldShowDeleteButtonOnQRCodes() {
    waitForProfileQRsToAppear();

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

  /** Checks if the fragment is properly changed when the settings button is clicked */
  @Test
  public void testSettingsButton() {
    // click the settings button
    onView(withId(R.id.contact_info_button)).perform(click());
    // check the current fragment
    await()
        .until(
            () ->
                ((GameActivity) solo.getCurrentActivity()).getController().getBody()
                    instanceof ProfileSettingsFragment);
  }

  /** Test that the back button on the settings page redirects the player to the profile page. */
  @Test
  public void testSettingsBackBtn() {
    // navigate to settings
    onView(withId(R.id.contact_info_button)).perform(click());
    // click on the back button from settings
    onView(withId(R.id.settings_back_button)).perform(click());
    // check the current fragment
    await()
        .until(
            () ->
                ((GameActivity) solo.getCurrentActivity()).getController().getBody()
                    instanceof ProfileFragment);
  }

  /** Checks if the player info is properly displayed in the settings */
  @Test
  public void testSettingsInfo() {
    // click the settings button to navigate to the settings fragment
    onView(withId(R.id.contact_info_button)).perform(click());
    // search for the player's phone and email
    onView(withId(R.id.settings_screen_phoneTextField))
        .check(matches(withText(localPlayer.getPhoneNo())));
    onView(withId(R.id.settings_screen_emailTextField))
        .check(matches(withText(localPlayer.getEmail())));
  }

  /** Checks the change of the user's phone number */
  @Test
  public void testPhoneNumChange() {
    // click the settings button to navigate to the settings
    onView(withId(R.id.contact_info_button)).perform(click());
    waitForSettingsPageToLoad();

    // clear the current phone number and enter a new one
    String newPhoneNo = "5872571509";
    onView(withId(R.id.settings_screen_phoneTextField)).perform(click(), clearText());
    onView(withId(R.id.settings_screen_phoneTextField))
        .perform(click(), typeText(newPhoneNo), closeSoftKeyboard());
    // check the phone number input
    onView(withId(R.id.settings_screen_phoneTextField)).check(matches(withText(newPhoneNo)));
    // press the save button
    onView(withId(R.id.settings_save_button)).perform(scrollTo(), click());

    AtomicReference<Player> updatedPlayer = new AtomicReference<>();
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(
            () -> {
              // If we have already fetched the player, check if the phone no was updated.
              Player databasePlayer = updatedPlayer.get();
              if (databasePlayer != null && databasePlayer.getPhoneNo().equals(newPhoneNo)) {
                return true; // Player was correctly updated!
              }

              // If the phone no was not updated yet or if we have not fetched the newest copy of
              // the player...
              // fetch the latest database saved entry.
              PlayerDatabase.getInstance()
                  .getPlayerByUsername(
                      localPlayer.getUsername(),
                      fetchedPlayer -> updatedPlayer.set(fetchedPlayer.getData()));
              return false; // Try again.
            });
  }

  /** Checks the change of the user's email */
  @Test
  public void testEmailChange() {
    // click the settings button to navigate to the settings
    onView(withId(R.id.contact_info_button)).perform(click());
    waitForSettingsPageToLoad();

    // clear the current email and enter a new one
    String newEmail = "irenerose.sun@gmail.com";
    onView(withId(R.id.settings_screen_emailTextField)).perform(click(), clearText());
    onView(withId(R.id.settings_screen_emailTextField))
        .perform(click(), typeText(newEmail), closeSoftKeyboard());
    // check email input
    onView(withId(R.id.settings_screen_emailTextField)).check(matches(withText(newEmail)));
    // press the save button
    onView(withId(R.id.settings_save_button)).perform(scrollTo(), click());

    AtomicReference<Player> updatedPlayer = new AtomicReference<>();
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(
            () -> {
              // If we have already fetched the player, check if the email was updated.
              Player databasePlayer = updatedPlayer.get();
              if (databasePlayer != null && databasePlayer.getEmail().equals(newEmail)) {
                return true; // Player was correctly updated!
              }

              // If the email was not updated yet or if we have not fetched the newest copy of
              // the player...
              // fetch the latest database saved entry.
              PlayerDatabase.getInstance()
                  .getPlayerByUsername(
                      localPlayer.getUsername(),
                      fetchedPlayer -> updatedPlayer.set(fetchedPlayer.getData()));
              return false; // Try again.
            });
  }

  /** Helper method to wait for the settings page details to load. */
  private void waitForSettingsPageToLoad() {
    // Wait for contact details to load in.
    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              TextInputEditText emailField =
                  (TextInputEditText) solo.getView(R.id.settings_screen_emailTextField);
              TextInputEditText phoneField =
                  (TextInputEditText) solo.getView(R.id.settings_screen_phoneTextField);
              return emailField.getText() != null
                  && !emailField.getText().toString().equals("")
                  && phoneField.getText() != null
                  && !phoneField.getText().toString().equals("");
            });
  }
}
