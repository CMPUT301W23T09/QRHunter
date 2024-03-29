package com.cmput301w23t09.qrhunter;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.Manifest;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.ListView;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import com.cmput301w23t09.qrhunter.comment.Comment;
import com.cmput301w23t09.qrhunter.locationphoto.LocationPhotoStorage;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.qrcode.AddQRCodeFragment;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import com.robotium.solo.Solo;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class TestAddQRCodeFragment extends BaseTest {
  private QRCode qrCode;
  private Solo solo;
  private AddQRCodeFragment qrCodeFragment;

  private Player player;

  @Rule
  public ActivityScenarioRule<GameActivity> activityScenarioRule =
      new ActivityScenarioRule<>(GameActivity.class);

  @Rule
  public GrantPermissionRule permissionRule =
      GrantPermissionRule.grant(
          android.Manifest.permission.ACCESS_FINE_LOCATION,
          android.Manifest.permission.ACCESS_COARSE_LOCATION,
          Manifest.permission.CAMERA);

  /** Opens the AddQRCodeFragment, assuming we've scanned a QR code with hash "test-hash123" */
  @Before
  public void setUp() throws InterruptedException {
    player =
        new Player(
            UUID.randomUUID(),
            "johndoe42",
            "7801234567",
            "doe@ualberta.ca",
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>());

    CountDownLatch dbTasks = new CountDownLatch(1);
    PlayerDatabase.getInstance()
        .add(
            player,
            ignored -> {
              dbTasks.countDown();
            });
    dbTasks.await();

    // Mock QRCode Info
    // Actual Data: CMPUT301W23T09-QRHunter
    // Hash: 8926bb85b4e02cf2c877070dd8dc920acbf6c7e0153b735a3d9381ec5c2ac11d
    // Name: Robbel Spicy Tiger
    // Score: 32 PTS
    qrCode = new QRCode("8926bb85b4e02cf2c877070dd8dc920acbf6c7e0153b735a3d9381ec5c2ac11d");

    qrCodeFragment = AddQRCodeFragment.newInstance(qrCode, player);
    activityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              activity.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
              solo = new Solo(InstrumentationRegistry.getInstrumentation(), activity);
              qrCodeFragment.show(activity.getSupportFragmentManager(), "AddQRCodeFragment");
            });
    await().until(() -> qrCodeFragment.getDialog() != null);
    await().until(() -> qrCodeFragment.getDialog().isShowing());
  }

  /** Checks if the QRCodeFragment displays the QRCode's name correctly */
  @Test
  public void testCorrectDisplayInfo() {
    onView(withId(R.id.qr_name)).inRoot(isDialog()).check(matches(withText("Robbel Spicy Tiger")));
    onView(withId(R.id.qr_points)).inRoot(isDialog()).check(matches(withText("32 PTS")));
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
    await().atMost(30, TimeUnit.SECONDS).until(() -> qrCode.getLoc() == null);
  }

  /**
   * Test if we can take a location photo and if the player that took it is correctly logged and
   * stored in the database
   */
  @Test
  public void testSnapLocationPhoto() {
    onView(withId(R.id.take_location_photo_btn)).inRoot(isDialog()).perform(click());
    await()
        .until(
            () ->
                qrCodeFragment.getLocationPhotoFragment().getDialog() != null
                    && qrCodeFragment.getLocationPhotoFragment().getDialog().isShowing());
    onView(withId(R.id.location_photo_shutter)).inRoot(isDialog()).perform(click());
    await().until(() -> qrCodeFragment.getLocationPhotoFragment().getDialog() == null);
    await().until(() -> qrCodeFragment.getLocationPhotoAdapter().getCount() == 1);
    AtomicReference<Boolean> updatedHasPhoto = new AtomicReference<>();
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(
            () -> {
              Boolean hasPhoto = updatedHasPhoto.get();
              if (hasPhoto != null && hasPhoto) return true;

              LocationPhotoStorage.getInstance()
                  .playerHasLocationPhoto(
                      qrCode,
                      player,
                      result -> {
                        updatedHasPhoto.set(result);
                      });
              return false;
            });
  }

  /**
   * Test if after we take a location photo, we can remove it using the same button and if the
   * location photo is no longer stored in the database
   */
  @Test
  public void testRemoveLocationPhoto() {
    testSnapLocationPhoto();
    onView(withId(R.id.take_location_photo_btn)).inRoot(isDialog()).perform(click());
    await().until(() -> qrCodeFragment.getLocationPhotoAdapter().getCount() == 0);
    AtomicReference<Boolean> updatedHasPhoto = new AtomicReference<>();
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(
            () -> {
              Boolean hasPhoto = updatedHasPhoto.get();
              if (hasPhoto != null && !hasPhoto) return true;

              LocationPhotoStorage.getInstance()
                  .playerHasLocationPhoto(
                      qrCode,
                      player,
                      result -> {
                        updatedHasPhoto.set(result);
                      });
              return false;
            });
  }

  /** Test to see that QRCodes are successfully added to the player's account */
  @Test
  public void testAddQRCode() throws Exception {
    // Click the add QR button and add the QR
    onView(withId(R.id.addButton)).inRoot(isDialog()).perform(click());
    await().atMost(30, TimeUnit.SECONDS).until(() -> qrCodeFragment.getDialog() == null);

    // Check that the database details are correct in that the player exists in the QR's scanned
    // player fields
    // and that the qr exists in the player's scanned qr field.

    AtomicReference<Player> updatedPlayer = new AtomicReference<>();
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(
            () -> {
              // If we have already fetched the player, check that the QRCode is not within the
              // Player.
              Player databasePlayer = updatedPlayer.get();
              if (databasePlayer != null
                  && databasePlayer.getQRCodeHashes().contains(qrCode.getHash())) {
                return true; // Player was correctly updated!
              }

              // If the phone no was not updated yet or if we have not fetched the newest copy of
              // the player
              // then fetch the latest database saved entry.
              PlayerDatabase.getInstance()
                  .getPlayerByUsername(
                      player.getUsername(),
                      fetchedPlayer -> updatedPlayer.set(fetchedPlayer.getData()));
              return false; // Try again.
            });

    AtomicReference<QRCode> updatedQR = new AtomicReference<>();
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(
            () -> {
              // If we have already fetched the QRCode, check that the Player is not within the
              // QRCode.
              QRCode databaseQR = updatedQR.get();
              if (databaseQR != null && databaseQR.getPlayers().contains(player.getDocumentId())) {
                return true; // Player was correctly updated!
              }

              // If the QRCode was not updated yet or if we have not fetched the newest copy of the
              // QRCode
              // then fetch the latest database saved entry.
              QRCodeDatabase.getInstance()
                  .getQRCodeByHash(
                      qrCode.getHash(), fetchedQR -> updatedQR.set(fetchedQR.getData()));
              return false; // Try again.
            });
  }

  /** Test to see that player can comment on a QR code and have it stored on Firestore */
  @Test
  public void testAddComment() {
    String commentString = "This QR code is pretty awesome!";
    onView(withText(R.string.comments_tab_title)).perform(click());
    solo.waitForView(R.id.comment_box);
    onView(withId(R.id.comment_box))
        .perform(
            click(),
            replaceText(commentString),
            pressKey(KeyEvent.KEYCODE_ENTER),
            closeSoftKeyboard());
    // First check that only 1 comment is showing (the only comment)
    ListView qrList = (ListView) solo.getView(R.id.qr_nav_items);
    assertEquals(1, qrList.getChildCount());

    // Then, check if the comment user and comment text is accurate
    onView(withId(R.id.comment_player_name))
        .inRoot(isDialog())
        .check(matches(withText(player.getUsername())));
    onView(withId(R.id.player_comment_input))
        .inRoot(isDialog())
        .check(matches(withText(commentString)));

    // Check if database has comment
    AtomicReference<QRCode> updatedQR = new AtomicReference<>();
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(
            () -> {
              QRCode databaseQR = updatedQR.get();
              if (databaseQR != null && databaseQR.getComments().size() == 1) {
                Comment comment = databaseQR.getComments().get(0);
                if (comment.getComment().equals(commentString)
                    && comment.getUsername().equals(player.getUsername())
                    && comment.getPlayerId().equals(player.getDocumentId())) return true;
                return false;
              }

              QRCodeDatabase.getInstance()
                  .getQRCodeByHash(
                      qrCode.getHash(), fetchedQR -> updatedQR.set(fetchedQR.getData()));
              return false;
            });
  }
}
