package com.cmput301w23t09.qrhunter.profile;

import com.cmput301w23t09.qrhunter.GameActivity;
import com.cmput301w23t09.qrhunter.GameController;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class TestProfileFragment {
  private Solo solo;

  @Rule
  public ActivityTestRule<GameActivity> rule = new ActivityTestRule(GameActivity.class, true, true);

  @Before
  public void setUp() throws Exception {
    solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    solo.clickOnView(solo.getView(R.id.navigation_profile));
  }

  @Test
  public void testStartProfile() {
    GameController gc = ((GameActivity) solo.getCurrentActivity()).getController();
    assertTrue(gc.getBody() instanceof ProfileFragment);
  }
}
