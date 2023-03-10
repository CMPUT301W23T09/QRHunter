package com.cmput301w23t09.qrhunter.qrcode;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;

import android.Manifest;
import android.content.Intent;
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
              activity.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
              qrCodeFragment.show(activity.getSupportFragmentManager(), "QRCodeFragment");
            });
    await().until(() -> qrCodeFragment.getDialog() != null);
    await().until(() -> qrCodeFragment.getDialog().isShowing());
  }

  @Test
  public void testQRNameDisplay() {
    // TODO: Currently, QRCodeFragment shows hash, CHANGE THIS TO NAME ONCE IMPLEMENTED
    onView(withId(R.id.qr_name)).inRoot(isDialog()).check(matches(withText("test-hash123")));
  }

  @Test
  public void testQRSetLocation() {
    onView(withId(R.id.location_request_box)).inRoot(isDialog()).perform(click());
    await().atMost(30, TimeUnit.SECONDS).until(() -> qrCode.getLoc() != null);
  }

  @Test
  public void testQRRemoveLocation() {
    onView(withId(R.id.location_request_box))
        .check(matches(isNotChecked()))
        .inRoot(isDialog())
        .perform(click());
    onView(withId(R.id.location_request_box))
        .check(matches(isChecked()))
        .inRoot(isDialog())
        .perform(click());
    await().atMost(30, TimeUnit.SECONDS).until(() -> qrCode.getLoc() == null);
  }

  @Test
  public void testSnapLocationPhoto() {
    assertEquals(0, qrCode.getPhotos().size());
    onView(withId(R.id.take_location_photo_btn)).inRoot(isDialog()).perform(click());
    await().until(() -> qrCodeFragment.getLocationPhotoFragment().getDialog().isShowing());
    onView(withId(R.id.location_photo_shutter)).inRoot(isDialog()).perform(click());
    await().until(() -> qrCodeFragment.getLocationPhotoFragment().getDialog() == null);
    await().atMost(30, TimeUnit.SECONDS).until(() -> qrCode.getPhotos().size() > 0);
  }

  @Test
  public void testRemoveLocationPhoto() throws InterruptedException {
    onView(withId(R.id.take_location_photo_btn)).inRoot(isDialog()).perform(click());
    await().until(() -> qrCodeFragment.getLocationPhotoFragment().getDialog().isShowing());
    onView(withId(R.id.location_photo_shutter)).inRoot(isDialog()).perform(click());
    await().until(() -> qrCodeFragment.getLocationPhotoFragment().getDialog() == null);
    onView(withId(R.id.take_location_photo_btn)).check(matches(withText("Remove Location Photo")));
    onView(withId(R.id.take_location_photo_btn)).inRoot(isDialog()).perform(click());
    await().atMost(30, TimeUnit.SECONDS).until(() -> qrCode.getPhotos().size() == 0);
  }
}
