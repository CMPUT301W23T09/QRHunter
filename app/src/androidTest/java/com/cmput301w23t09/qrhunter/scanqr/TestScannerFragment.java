package com.cmput301w23t09.qrhunter.scanqr;

import static org.junit.Assert.assertTrue;

import android.Manifest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import com.cmput301w23t09.qrhunter.GameActivity;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import com.robotium.solo.Solo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class TestScannerFragment {
  private Solo solo;

  @Rule
  public ActivityTestRule<GameActivity> rule =
      new ActivityTestRule<>(GameActivity.class, true, true);

  @Rule
  public GrantPermissionRule permissionsRule =
      GrantPermissionRule.grant(Manifest.permission.CAMERA);

  @Before
  public void setUp() throws Exception {
    solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    solo.clickOnView(solo.getView(R.id.navigation_scan_qr));
  }

  @Test
  public void testStartScanQr() {
    GameController gc = ((GameActivity) solo.getCurrentActivity()).getController();
    assertTrue(gc.getBody() instanceof ScannerFragment);
  }
}
