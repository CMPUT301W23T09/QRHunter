package com.cmput301w23t09.qrhunter.profile;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import android.Manifest;
import android.content.Intent;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import com.cmput301w23t09.qrhunter.GameActivity;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.database.DatabaseConsumer;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.robotium.solo.Solo;
import java.util.UUID;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/** Test classes for profile activity */
public class TestProfileFragment {
  private Solo solo;

  @Rule
  public ActivityScenarioRule<GameActivity> activityScenarioRule =
      new ActivityScenarioRule<>(GameActivity.class);

  @Rule
  public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

  /**
   * Runs before all tests and creates solo instance
   *
   * @throws Exception
   */
  @Before
  public void setUp() throws Exception {
    // Mock PlayerDatabase
    PlayerDatabase mockPlayerDatabase = mock(PlayerDatabase.class);
    doNothing()
        .when(mockPlayerDatabase)
        .getPlayerByDeviceId(any(UUID.class), any(DatabaseConsumer.class));
    doNothing()
        .when(mockPlayerDatabase)
        .getPlayerByUsername(any(String.class), any(DatabaseConsumer.class));
    PlayerDatabase.mockInstance(mockPlayerDatabase);

    activityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              activity.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
              solo = new Solo(InstrumentationRegistry.getInstrumentation(), activity);
            });
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
  @Test
  public void testSpinnerView() {
    // click on spinner
    solo.clickOnView(solo.getView(R.id.order_spinner));
    // check if both Ascending and Descending options appear on screen
    TestCase.assertTrue(solo.waitForText("Ascending", 1, 2000));
    TestCase.assertTrue(solo.waitForText("Descending", 1, 2000));
  }
}
