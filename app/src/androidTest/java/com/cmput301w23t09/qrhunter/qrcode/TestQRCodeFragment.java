package com.cmput301w23t09.qrhunter.qrcode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.Manifest;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import com.cmput301w23t09.qrhunter.GameActivity;
import com.cmput301w23t09.qrhunter.R;
import com.robotium.solo.Condition;
import com.robotium.solo.Solo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class TestQRCodeFragment {
  private Solo solo;
  private QRCode qrCode;
  private QRCodeFragment qrCodeFragment;

  @Rule
  public ActivityTestRule<GameActivity> rule =
      new ActivityTestRule<>(GameActivity.class, true, true);

  @Rule
  public GrantPermissionRule permissionRule =
      GrantPermissionRule.grant(
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_COARSE_LOCATION,
          Manifest.permission.CAMERA);

  @Before
  public void setUp() throws Exception {
    solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    solo.clickOnView(solo.getView(R.id.navigation_scan_qr));
    qrCode = new QRCode("test-hash123");
    AppCompatActivity gameActivity = ((AppCompatActivity) solo.getCurrentActivity());
    qrCodeFragment = QRCodeFragment.newInstance(qrCode);
    qrCodeFragment.show(gameActivity.getSupportFragmentManager(), "QRCodeFragment");
    solo.sleep(1000);
  }

  @Test
  public void testStartQRFragment() {
    Fragment frag =
        ((AppCompatActivity) solo.getCurrentActivity())
            .getSupportFragmentManager()
            .findFragmentByTag("QRCodeFragment");
    assertNotNull(frag);
  }

  @Test
  public void testQRNameDisplay() {
    // TODO: Currently, QRCodeFragment shows hash, CHANGE THIS TO NAME ONCE IMPLEMENTED
    assertTrue(solo.searchText("test-hash123"));
    TextView nameView = (TextView) solo.getView(R.id.qr_name);
    assertEquals("test-hash123", nameView.getText().toString());
  }

  private void setLocation() {
    solo.clickOnView(solo.getView(R.id.location_request_box));
    assertTrue(
        solo.waitForCondition(
            new Condition() {
              @Override
              public boolean isSatisfied() {
                return qrCode.getLoc() != null;
              }
            },
            10000));
  }

  @Test
  public void testQRSetLocation() {
    // TODO: Figure out how to test with disabled permissions
    setLocation();
  }

  @Test
  public void testQRRemoveLocation() {
    setLocation();
    solo.clickOnView(solo.getView(R.id.location_request_box));
    assertTrue(
        solo.waitForCondition(
            new Condition() {
              @Override
              public boolean isSatisfied() {
                return qrCode.getLoc() == null;
              }
            },
            10000));
  }

  private void snapLocationPhoto() {
    assertEquals(0, qrCode.getPhotos().size());
    solo.clickOnView(solo.getView(R.id.take_location_photo_btn));
    assertTrue(solo.waitForView(R.id.location_photo_shutter));
    solo.clickOnView(solo.getView(R.id.location_photo_shutter));
    assertTrue(
        solo.waitForCondition(
            new Condition() {
              @Override
              public boolean isSatisfied() {
                return qrCode.getPhotos().size() > 0;
              }
            },
            10000));
  }

  @Test
  public void testSnapLocationPhoto() {
    snapLocationPhoto();
  }

  @Test
  public void testRemoveLocationPhoto() {
    snapLocationPhoto();
    solo.clickOnView(solo.getView(R.id.take_location_photo_btn));
    assertTrue(
        solo.waitForCondition(
            new Condition() {
              @Override
              public boolean isSatisfied() {
                return qrCode.getPhotos().size() == 0;
              }
            },
            10000));
  }
}
