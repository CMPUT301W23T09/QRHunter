package com.cmput301w23t09.qrhunter;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import com.cmput301w23t09.qrhunter.player.MockPlayerDatabase;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.robotium.solo.Solo;
import java.util.HashSet;
import java.util.UUID;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/** Tests the landing activity to ensure that all UI is operational. */
public class TestLandingActivity {

  private Solo solo;
  private static MockPlayerDatabase database;

  @Rule
  public ActivityScenarioRule<LandingActivity> activityScenarioRule =
      new ActivityScenarioRule<>(LandingActivity.class);

  @BeforeClass
  public static void setUpDatabase() {
    database = new MockPlayerDatabase();
    PlayerDatabase.mockInstance(database);
  }

  @Before
  public void setUpTest() {
    database.reset();
    activityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              activity.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
              solo = new Solo(InstrumentationRegistry.getInstrumentation(), activity);
            });
  }

  /** Players who do not have an account should stay on the landing page. */
  @Test
  public void testStayOnLandingWithoutRegisteredPlayer() {
    solo.waitForActivity(GameActivity.class, 15000);
    solo.assertCurrentActivity(
        "Player is on game activity without having an account.", LandingActivity.class);
  }

  /** When registering, the username must match the validation rules. 1-20 characters */
  @Test
  public void testUsernameValidation() {
    String invalidUsername = "";
    String validPhoneNo = "123-456-7890";
    String validEmail = "example@example.com";

    onView(withId(R.id.landing_screen_usernameTextField))
        .perform(click(), typeText(invalidUsername), closeSoftKeyboard());
    onView(withId(R.id.landing_screen_phoneNoTextField))
        .perform(click(), typeText(validPhoneNo), closeSoftKeyboard());
    onView(withId(R.id.landing_screen_emailTextField))
        .perform(click(), typeText(validEmail), closeSoftKeyboard());
    onView(withId(R.id.landing_screen_register_button)).perform(click());

    solo.waitForActivity(GameActivity.class, 5000);
    solo.assertCurrentActivity("Allowed invalid username registration.", LandingActivity.class);
  }

  /** When registering, using an existing username should not be allowed. */
  @Test
  public void testUsernameExisting() {
    String validUsername = "User";
    String validPhoneNo = "123-456-7890";
    String validEmail = "example@example.com";

    database.add(
        new Player(UUID.randomUUID(), validUsername, validPhoneNo, validEmail, new HashSet<>()),
        task -> {
          onView(withId(R.id.landing_screen_usernameTextField))
              .perform(click(), typeText(validUsername.toLowerCase()), closeSoftKeyboard());
          onView(withId(R.id.landing_screen_phoneNoTextField))
              .perform(click(), typeText(validPhoneNo), closeSoftKeyboard());
          onView(withId(R.id.landing_screen_emailTextField))
              .perform(click(), typeText(validEmail), closeSoftKeyboard());
          onView(withId(R.id.landing_screen_register_button)).perform(click());

          solo.waitForActivity(GameActivity.class, 5000);
          solo.assertCurrentActivity(
              "Allowed invalid existing username registration.", LandingActivity.class);
        });
  }

  /** When registering, the phone number must be valid. */
  @Test
  public void testPhoneNumberValidation() {
    String validUsername = "User";
    String invalidPhoneNo = "not valid";
    String validEmail = "example@example.com";

    onView(withId(R.id.landing_screen_usernameTextField))
        .perform(click(), typeText(validUsername), closeSoftKeyboard());
    onView(withId(R.id.landing_screen_phoneNoTextField))
        .perform(click(), typeText(invalidPhoneNo), closeSoftKeyboard());
    onView(withId(R.id.landing_screen_emailTextField))
        .perform(click(), typeText(validEmail), closeSoftKeyboard());
    onView(withId(R.id.landing_screen_register_button)).perform(click());

    solo.waitForActivity(GameActivity.class, 5000);
    solo.assertCurrentActivity("Allowed invalid phone number registration.", LandingActivity.class);
  }

  /** When registering, the email must be valid. */
  @Test
  public void testEmailValidation() {
    String validUsername = "User";
    String validPhoneNo = "123-456-7890";
    String invalidEmail = "example.com";

    onView(withId(R.id.landing_screen_usernameTextField))
        .perform(click(), typeText(validUsername), closeSoftKeyboard());
    onView(withId(R.id.landing_screen_phoneNoTextField))
        .perform(click(), typeText(validPhoneNo), closeSoftKeyboard());
    onView(withId(R.id.landing_screen_emailTextField))
        .perform(click(), typeText(invalidEmail), closeSoftKeyboard());
    onView(withId(R.id.landing_screen_register_button)).perform(click());

    solo.waitForActivity(GameActivity.class, 5000);
    solo.assertCurrentActivity("Allowed invalid email registration.", LandingActivity.class);
  }

  /**
   * It should create an account when you enter valid credentials and forward you to the
   * GameActivity after.
   */
  @Test
  public void testRegistration() {
    String validUsername = "User";
    String validPhoneNo = "123-456-7890";
    String invalidEmail = "example.com";

    onView(withId(R.id.landing_screen_usernameTextField))
        .perform(click(), typeText(validUsername), closeSoftKeyboard());
    onView(withId(R.id.landing_screen_phoneNoTextField))
        .perform(click(), typeText(validPhoneNo), closeSoftKeyboard());
    onView(withId(R.id.landing_screen_emailTextField))
        .perform(click(), typeText(invalidEmail), closeSoftKeyboard());
    onView(withId(R.id.landing_screen_register_button)).perform(click());

    solo.waitForActivity(GameActivity.class, 5000);
    solo.assertCurrentActivity("Did not switch to game activity.", LandingActivity.class);
  }
}
