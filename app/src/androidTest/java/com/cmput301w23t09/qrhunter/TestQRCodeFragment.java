package com.cmput301w23t09.qrhunter;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import com.cmput301w23t09.qrhunter.database.DatabaseConsumer;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeFragment;
import com.robotium.solo.Solo;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
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
  private ArrayList<QRCode> mockQRCollection; // Mimics collection on firestore

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
  public void setUp() throws ExecutionException, InterruptedException {
    mockPlayerUUID = UUID.randomUUID();
    mockPlayer =
        new Player(
            "001", mockPlayerUUID, "johndoe42", "7801234567", "doe@ualberta.ca", new ArrayList<>());
    mockQRCollection = new ArrayList<>();

    // Mock QRCodeDatabase
    QRCodeDatabase mockedQRCodeDatabase = mock(QRCodeDatabase.class);
    doNothing()
        .when(mockedQRCodeDatabase)
        .playerHasQRCode(any(Player.class), any(QRCode.class), any(DatabaseConsumer.class));

    // Mock adding QRCode to Firebase collection
    doAnswer(
            answer -> {
              mockQRCollection.add(answer.getArgument(0));
              return null;
            })
        .when(mockedQRCodeDatabase)
        .addQRCode(any(QRCode.class));

    // Mock adding QRCode to player's profile
    doAnswer(
            answer -> {
              Player playerArg = answer.getArgument(0);
              QRCode qrCodeArg = answer.getArgument(1);
              playerArg.getQRCodeHashes().add(qrCodeArg.getHash());
              qrCodeArg.addPlayer(playerArg.getDocumentId());
              return null;
            })
        .when(mockedQRCodeDatabase)
        .addPlayerToQR(any(Player.class), any(QRCode.class));

    // Mock removing QRCode from player's profile
    doAnswer(
            answer -> {
              Player playerArg = answer.getArgument(0);
              QRCode qrCodeArg = answer.getArgument(1);
              playerArg.getQRCodeHashes().remove(qrCodeArg.getHash());
              qrCodeArg.getPlayers().remove(playerArg.getDocumentId());
              return null;
            })
        .when(mockedQRCodeDatabase)
        .removeQRCodeFromPlayer(any(Player.class), any(QRCode.class));
    QRCodeDatabase.mockInstance(mockedQRCodeDatabase);

    // Mock QRCode Info
    // Actual Data: CMPUT301W23T09-QRHunter
    // Hash: 8926bb85b4e02cf2c877070dd8dc920acbf6c7e0153b735a3d9381ec5c2ac11d
    // Name: RobaqinectTiger✿
    // Score: 32 PTS
    qrCode = new QRCode("8926bb85b4e02cf2c877070dd8dc920acbf6c7e0153b735a3d9381ec5c2ac11d");

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
  public void testCorrectDisplayInfo() throws InterruptedException, ExecutionException {
    // TODO: Currently, QRCodeFragment shows hash, CHANGE THIS TO NAME ONCE IMPLEMENTED
    onView(withId(R.id.qr_name)).inRoot(isDialog()).check(matches(withText("RobaqinectTiger✿")));
    onView(withId(R.id.qr_points)).inRoot(isDialog()).check(matches(withText("32 PTS")));
    ImageView qrVisualView = (ImageView) solo.getView(R.id.qr_code_visual);
    Bitmap qrVisualBitmap = ((BitmapDrawable) qrVisualView.getDrawable()).getBitmap();
    assertTrue(qrVisualBitmap.sameAs(qrCode.getVisualRepresentation()));
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

  @Test
  public void testAddQRCode() {
    onView(withId(R.id.addButton)).inRoot(isDialog()).perform(click());
    // Check if QRCode is in player's profile
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(() -> mockPlayer.getQRCodeHashes().contains(qrCode.getHash()));
    // Check if QRCode kept track of player that scanned it
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(() -> qrCode.getPlayers().contains(mockPlayer.getDocumentId()));
  }

  @Test
  public void testDeleteQRCode() {
    onView(withId(R.id.addButton)).inRoot(isDialog()).perform(click());

    // Check if button switched to remove since player has code
    onView(withId(R.id.deleteButton)).inRoot(isDialog()).check(matches(isDisplayed()));
    onView(withId(R.id.addButton)).inRoot(isDialog()).check(matches(not(isDisplayed())));

    onView(withId(R.id.deleteButton)).inRoot(isDialog()).perform(click());

    // Check if QRCode is no longer in player's profile
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(() -> !mockPlayer.getQRCodeHashes().contains(qrCode.getHash()));
    // Check if QRCode removed previous player that scanned it
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(() -> !qrCode.getPlayers().contains(mockPlayer.getDocumentId()));
  }
}
