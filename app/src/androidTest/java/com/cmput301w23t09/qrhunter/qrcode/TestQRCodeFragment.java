package com.cmput301w23t09.qrhunter.qrcode;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;

import android.Manifest;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;
import com.cmput301w23t09.qrhunter.GameActivity;
import com.cmput301w23t09.qrhunter.R;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class TestQRCodeFragment {
  private QRCode qrCode;
  private QRCodeFragment qrCodeFragment;

  @Rule
  public ActivityScenarioRule<GameActivity> activityScenarioRule =
      new ActivityScenarioRule<>(GameActivity.class);

  @Rule
  public GrantPermissionRule permissionRule =
      GrantPermissionRule.grant(
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_COARSE_LOCATION,
          Manifest.permission.CAMERA);

  @Before
  public void setUp() {
    qrCode = new QRCode("test-hash123");
    qrCodeFragment = QRCodeFragment.newInstance(qrCode);
    activityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              qrCodeFragment.show(activity.getSupportFragmentManager(), "QRCodeFragment");
            });
  }

  @Test
  public void testQRNameDisplay() {
    // TODO: Currently, QRCodeFragment shows hash, CHANGE THIS TO NAME ONCE IMPLEMENTED
    onView(withId(R.id.qr_name)).check(matches(withText("test-hash123")));
  }

  private void setLocation() {
    onView(withId(R.id.location_request_box)).perform(click());
    await().atMost(30, TimeUnit.SECONDS).until(() -> qrCode.getLoc() != null);
  }

  @Test
  public void testQRSetLocation() {
    setLocation();
  }

  @Test
  public void testQRRemoveLocation() {
    setLocation();
    onView(withId(R.id.location_request_box)).perform(click());
    await().atMost(30, TimeUnit.SECONDS).until(() -> qrCode.getLoc() == null);
  }

  private void snapLocationPhoto() {
    assertEquals(0, qrCode.getPhotos().size());
    onView(withId(R.id.take_location_photo_btn)).perform(click());
    onView(withId(R.id.location_photo_shutter)).perform(click());
    await().atMost(30, TimeUnit.SECONDS).until(() -> qrCode.getPhotos().size() > 0);
  }

  @Test
  public void testSnapLocationPhoto() {
    snapLocationPhoto();
  }

  @Test
  public void testRemoveLocationPhoto() {
    snapLocationPhoto();
    onView(withId(R.id.take_location_photo_btn)).perform(click());
    await().atMost(30, TimeUnit.SECONDS).until(() -> qrCode.getPhotos().size() == 0);
  }
}
