package com.cmput301w23t09.qrhunter.qrcode;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.Manifest;
import android.content.Intent;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import com.cmput301w23t09.qrhunter.GameActivity;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.player.Player;
import com.robotium.solo.Solo;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests the QRCodeFragment if it displays the QRCode's info correctly and if can modify the code
 * correctly
 *
 * @see QRCodeFragment
 * @author John Mabanta
 * @version 1.0
 */
public class TestQRCodeFragment {
  private QRCode qrCode;
  private Solo solo;
  private QRCodeFragment qrCodeFragment;

  private UUID mockPlayerUUID;
  private Player mockPlayer;

  @Rule
  public ActivityScenarioRule<GameActivity> activityScenarioRule =
      new ActivityScenarioRule<>(GameActivity.class);

  @Rule
  public GrantPermissionRule permissionRule =
      GrantPermissionRule.grant(
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_COARSE_LOCATION,
          Manifest.permission.CAMERA);

  /** Opens the QRCodeFragment, assuming we've scanned a QR code with hash "test-hash123" */
  @Before
  public void setUp() {
    mockPlayerUUID = UUID.randomUUID();
    mockPlayer =
        new Player(
            "001", mockPlayerUUID, "johndoe42", "7801234567", "doe@ualberta.ca", new ArrayList<>());

    qrCode = new QRCode("test-hash123");
    qrCodeFragment = QRCodeFragment.newInstance(qrCode, mockPlayer);
    activityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              activity.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
              solo = new Solo(InstrumentationRegistry.getInstrumentation(), activity);
              qrCodeFragment.show(activity.getSupportFragmentManager(), "QRCodeFragment");
            });
    await().until(() -> qrCodeFragment.getDialog() != null);
    await().until(() -> qrCodeFragment.getDialog().isShowing());
  }

  /** Checks if the QRCodeFragment displays the QRCode's name correctly */
  @Test
  public void testQRNameDisplay() {
    // TODO: Currently, QRCodeFragment shows hash, CHANGE THIS TO NAME ONCE IMPLEMENTED
    onView(withId(R.id.qr_name)).inRoot(isDialog()).check(matches(withText("test-hash123")));
  }

  /** Checks if we can set the QRCode's location by checking the checkbox */
  @Test
  public void testQRSetLocation() {
    solo.sleep(5000); // This test is really flaky on Github Actions :(
    solo.clickOnText("Record QR Location");
    assertTrue(solo.waitForCondition(() -> qrCode.getLoc() != null, 25000));
  }

  /** Checks if we can remove the QRCode's location by unchecking the checkbox */
  @Test
  public void testQRRemoveLocation() {
    solo.clickOnText("Record QR Location");
    solo.clickOnText("Record QR Location");
    assertTrue(solo.waitForCondition(() -> qrCode.getLoc() == null, 25000));
  }

  /** Test if we can take a location photo and if the player that took it is correctly logged */
  @Test
  public void testSnapLocationPhoto() {
    assertEquals(0, qrCode.getPhotos().size());
    onView(withId(R.id.take_location_photo_btn)).inRoot(isDialog()).perform(click());
    await().until(() -> qrCodeFragment.getLocationPhotoFragment().getDialog().isShowing());
    onView(withId(R.id.location_photo_shutter)).inRoot(isDialog()).perform(click());
    await().until(() -> qrCodeFragment.getLocationPhotoFragment().getDialog() == null);
    await().atMost(30, TimeUnit.SECONDS).until(() -> qrCode.getPhotos().size() > 0);
    // Check if player that snapped location photo is correct
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(() -> qrCode.getPhotos().get(0).getPlayer().equals(mockPlayer));
  }

  /** Test if after we take a location photo, we can remove it using the same button */
  @Test
  public void testRemoveLocationPhoto() {
    onView(withId(R.id.take_location_photo_btn)).inRoot(isDialog()).perform(click());
    await().until(() -> qrCodeFragment.getLocationPhotoFragment().getDialog().isShowing());
    onView(withId(R.id.location_photo_shutter)).inRoot(isDialog()).perform(click());
    await().until(() -> qrCodeFragment.getLocationPhotoFragment().getDialog() == null);
    onView(withId(R.id.take_location_photo_btn)).check(matches(withText("Remove Location Photo")));
    onView(withId(R.id.take_location_photo_btn)).inRoot(isDialog()).perform(click());
    await().atMost(30, TimeUnit.SECONDS).until(() -> qrCode.getPhotos().size() == 0);
  }
}
