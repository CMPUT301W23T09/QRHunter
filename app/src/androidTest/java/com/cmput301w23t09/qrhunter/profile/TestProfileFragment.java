package com.cmput301w23t09.qrhunter.profile;

import static org.junit.Assert.assertTrue;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.cmput301w23t09.qrhunter.GameActivity;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import com.robotium.solo.Solo;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test classes for profile activity
 */
public class TestProfileFragment {
  private Solo solo;

  @Rule
  public ActivityTestRule<GameActivity> rule = new ActivityTestRule(GameActivity.class, true, true);

  /**
   * Runs before all tests and creates solo instance
   * @throws Exception
   */
  @Before
  public void setUp() throws Exception {
    // create solo
    solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    // navigate to profile fragment
    solo.clickOnView(solo.getView(R.id.navigation_my_profile));
  }

  /**
   * Checks if the current fragment is correct
   */
  @Test
  public void testStartProfile() {
    // get current fragment and check if it is a profile fragment
    GameController gc = ((GameActivity) solo.getCurrentActivity()).getController();
    assertTrue(gc.getBody() instanceof ProfileFragment);
  }

  /**
   * Checks if the spinners are displayed correctly
   */
  @Test
  public void testSpinnerView() {
    // click on spinner
    solo.clickOnView(solo.getView(R.id.order_spinner));
    // check if both Ascending and Descending options appear on screen
    TestCase.assertTrue(solo.waitForText("Ascending", 1, 2000));
    TestCase.assertTrue(solo.waitForText("Descending", 1, 2000));
  }
}
