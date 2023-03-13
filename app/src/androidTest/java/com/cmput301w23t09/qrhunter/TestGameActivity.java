package com.cmput301w23t09.qrhunter;

import android.content.SharedPreferences;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import com.cmput301w23t09.qrhunter.player.MockPlayerDatabase;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.util.DeviceUtils;
import com.robotium.solo.Solo;
import java.util.UUID;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

public class TestGameActivity {

  private Solo solo;
  private static MockPlayerDatabase database;

  @Rule
  public ActivityScenarioRule<LandingActivity> activityScenarioRule =
      new ActivityScenarioRule<>(LandingActivity.class);

  @BeforeClass
  public static void setUpDatabase() {
    UUID ourDeviceUUID = UUID.randomUUID();
    SharedPreferences preferences =
        InstrumentationRegistry.getInstrumentation()
            .getContext()
            .getSharedPreferences(DeviceUtils.DEVICE_UUID_FILE, 0);
    preferences
        .edit()
        .putString(DeviceUtils.DEVICE_UUID_FILE_FIELD, ourDeviceUUID.toString())
        .apply();

    database = new MockPlayerDatabase();
    database.add(
        new Player(ourDeviceUUID, "User", "123-456-7890", "example@example.com"), ignored -> {});
    PlayerDatabase.mockInstance(database);
  }

  @Before
  public void setUpTest() {
    activityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              solo = new Solo(InstrumentationRegistry.getInstrumentation(), activity);
            });
  }

  @Test
  public void shouldSwitchToGameActivityIfHasExistingPlayer() {
    solo.waitForActivity(GameActivity.class, 15000);
    solo.assertCurrentActivity("Player did not move to game activity.", GameActivity.class);
  }
}
