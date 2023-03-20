package com.cmput301w23t09.qrhunter;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertTrue;

import android.Manifest;
import android.content.Intent;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.profile.ProfileFragment;
import com.cmput301w23t09.qrhunter.util.DeviceUtils;
import com.robotium.solo.Solo;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/** Test classes for profile activity */
public class TestProfileFragment extends BaseTest {
  private Solo solo;
  private Player player;

  @Rule
  public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

  @Rule
  public ActivityScenarioRule<GameActivity> activityScenarioRule =
      new ActivityScenarioRule<>(GameActivity.class);

  /**
   * Runs before all tests and creates solo instance
   *
   * @throws Exception
   */
  @Before
  public void setUp() throws Exception {
    CountDownLatch databaseSetup = new CountDownLatch(1);

    // get solo
    activityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              activity.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
              solo = new Solo(InstrumentationRegistry.getInstrumentation(), activity);

              // Retrieve our device UUID and add a player corresponding to that device UUID.
              UUID ourDeviceUUID = DeviceUtils.getDeviceUUID(activity);
              player =
                  new Player(
                      ourDeviceUUID, "Irene", "5873571506", "isun@ualberta.ca", new ArrayList<>());

              // When the player is added, notify the JUnit thread to continue.
              PlayerDatabase.getInstance().add(player, ignored -> databaseSetup.countDown());
            });

    // Wait for the JUnit thread to be told that the player was added to the database.
    databaseSetup.await();

    // navigate to profile fragment
    solo.clickOnView(solo.getView(R.id.navigation_my_profile));
    await()
        .until(
            () ->
                ((GameActivity) solo.getCurrentActivity()).getController().getBody()
                    instanceof ProfileFragment);
  }

  /** Checks if the current fragment is correct */
  @Test
  public void testStartProfile() {
    // get current fragment and check if it is a profile fragment
    GameController gc = ((GameActivity) solo.getCurrentActivity()).getController();
    assertTrue(gc.getBody() instanceof ProfileFragment);
  }

  /** Checks if the spinners are displayed correctly */
  /*@Test
  public void testSpinnerView() {
    // click on spinner
    solo.clickOnView(solo.getView(R.id.order_spinner));
    // check if both Ascending and Descending options appear on screen
    TestCase.assertTrue(solo.waitForText("Ascending", 1, 2000));
    TestCase.assertTrue(solo.waitForText("Descending", 1, 2000));
    // click on "ascending" option(since default is "descending")
    TestCase.assertFalse(solo.searchText("Descending"));
    TestCase.assertTrue(solo.searchText("Ascending"));

  }*/

  /** Checks if the username is correctly displayed */
  @Test
  public void testUsernameView() {
    // check if mockPlayer's username is displayed
    TestCase.assertTrue(solo.waitForText(player.getUsername(), 1, 2000));
  }

  /** Checks if the fragment is properly changed when the settings button is clicked */
  /*@Test
  public void testSettingsButton() {

    // click the settings button
    solo.clickOnView(solo.getView(R.id.contact_info_button));
    // check the current fragment
    GameController gc = ((GameActivity) solo.getCurrentActivity()).getController();
    assertTrue(gc.getBody() instanceof ProfileSettingsFragment);

  }*/

  /** Checks if the player info is properly displayed in the settings */
  @Test
  public void testSettingsView() {
    // click the settings button
    solo.clickOnView(solo.getView(R.id.contact_info_button));
    // search for the player's phone and email info
    assertTrue(solo.waitForText(player.getPhoneNo(), 1, 2000));
    assertTrue(solo.waitForText(player.getEmail(), 1, 2000));
  }

  /** Checks if the save changes button in the settings works */
  /*@Test
  public void testSaveChangesBtn() {
    // click the settings button
    solo.clickOnView(solo.getView(R.id.contact_info_button));
    // try changing the phone number
    solo.enterText((EditText) solo.getView(R.id.settings_screen_phoneTextField), "1");
    // press save changes button
    solo.clickOnView(solo.getView(R.id.settings_save_button));
    // go back to profile
    solo.clickOnView(solo.getView(R.id.settings_back_button));
    // go to settings again
    solo.clickOnView(solo.getView(R.id.contact_info_button));
    // check phone number
    assertTrue(solo.searchText(mockPlayer.getPhoneNo() + "1"));
  }*/
}
