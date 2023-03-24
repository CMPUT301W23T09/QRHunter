package com.cmput301w23t09.qrhunter;

import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.util.DeviceUtils;
import com.robotium.solo.Solo;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

public class TestGameActivity extends BaseTest {

  private Solo solo;

  @Rule
  public ActivityScenarioRule<LandingActivity> activityScenarioRule =
      new ActivityScenarioRule<>(LandingActivity.class);

  @BeforeClass
  public static void setupDatabase() throws Exception {
    initialize();

    // Retrieve our UUID
    SharedPreferences preferences =
        ApplicationProvider.getApplicationContext()
            .getSharedPreferences(DeviceUtils.DEVICE_UUID_FILE, 0);
    String existingUUIDField = preferences.getString(DeviceUtils.DEVICE_UUID_FILE_FIELD, null);

    // If our UUID doesn't exist yet, create one.
    if (existingUUIDField == null) {
      existingUUIDField = UUID.randomUUID().toString();
    }

    // Overwrite UUID with fetched UUID
    UUID playerUUID = UUID.fromString(existingUUIDField);
    preferences.edit().putString(DeviceUtils.DEVICE_UUID_FILE_FIELD, existingUUIDField).commit();

    // Insert a player with our device UUID
    CountDownLatch databaseSetup = new CountDownLatch(1);
    Player player =
        new Player(playerUUID, "User", "123-456-7890", "example@example.com", new ArrayList<>());
    PlayerDatabase.getInstance().add(player, ignored -> databaseSetup.countDown());
    databaseSetup.await();
  }

  @Before
  public void setUpTest() {
    activityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              activity.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
              solo = new Solo(InstrumentationRegistry.getInstrumentation(), activity);
            });
  }

  @Test
  public void testMigrateToGameActivityIfRegisteredPlayer() {
    solo.waitForActivity(GameActivity.class, 10000);

    boolean navigationBarExists = solo.waitForView(R.id.navigation_bar);
    assertTrue("The player is still on the landing activity.", navigationBarExists);
  }
}
