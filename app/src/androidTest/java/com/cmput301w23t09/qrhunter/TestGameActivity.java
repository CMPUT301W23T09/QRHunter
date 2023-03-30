package com.cmput301w23t09.qrhunter;

import static org.junit.Assert.assertTrue;

import android.content.Intent;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.robotium.solo.Solo;
import java.util.ArrayList;
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

    // Insert a player with our device UUID
    CountDownLatch databaseSetup = new CountDownLatch(1);
    Player player =
        new Player(
            getDeviceUUID(), "User", "123-456-7890", "example@example.com", new ArrayList<>());
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
