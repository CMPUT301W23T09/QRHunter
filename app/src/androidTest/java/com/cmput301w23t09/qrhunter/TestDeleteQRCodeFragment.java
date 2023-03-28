package com.cmput301w23t09.qrhunter;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.qrcode.DeleteQRCodeFragment;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import com.robotium.solo.Solo;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class TestDeleteQRCodeFragment extends BaseTest {
  private QRCode qrCode;
  private Solo solo;
  private DeleteQRCodeFragment qrCodeFragment;
  private Player player;

  @Rule
  public ActivityScenarioRule<GameActivity> activityScenarioRule =
      new ActivityScenarioRule<>(GameActivity.class);

  /** Opens the QRCodeFragment, assuming we've scanned a QR code with hash "test-hash123" */
  @Before
  public void setUp() throws InterruptedException {
    player =
        new Player(
            UUID.randomUUID(), "johndoe42", "7801234567", "doe@ualberta.ca", new ArrayList<>());

    CountDownLatch dbTasks = new CountDownLatch(1);
    PlayerDatabase.getInstance()
        .add(
            player,
            ignored -> {
              // Add QR to player

              // Mock QRCode Info
              // Actual Data: CMPUT301W23T09-QRHunter
              // Hash: 8926bb85b4e02cf2c877070dd8dc920acbf6c7e0153b735a3d9381ec5c2ac11d
              // Name: RobaqinectTiger✿
              // Score: 32 PTS
              qrCode =
                  new QRCode("8926bb85b4e02cf2c877070dd8dc920acbf6c7e0153b735a3d9381ec5c2ac11d");
              QRCodeDatabase.getInstance()
                  .addPlayerToQR(
                      player,
                      qrCode,
                      ignored2 -> {
                        dbTasks.countDown();
                      });
            });
    dbTasks.await();

    qrCodeFragment = DeleteQRCodeFragment.newInstance(qrCode, player);
    activityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              activity.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
              solo = new Solo(InstrumentationRegistry.getInstrumentation(), activity);
              qrCodeFragment.show(activity.getSupportFragmentManager(), "DeleteQRCodeFragment");
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

  /** Test to see that QRCodes are successfully removed from the player account */
  @Test
  public void testDeleteQRCode() {
    // Delete the QRCode from the player's
    await().until(() -> solo.getView(R.id.deleteButton).getVisibility() == View.VISIBLE);
    onView(withId(R.id.deleteButton)).inRoot(isDialog()).perform(click());
    await().atMost(30, TimeUnit.SECONDS).until(() -> qrCodeFragment.getDialog() == null);

    // Check that the database details are correct in that the player does not exist in the qr's
    // scanned player fields
    // and that the qr does not exist in the player's scanned qr field.

    AtomicReference<Player> updatedPlayer = new AtomicReference<>();
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(
            () -> {
              // If we have already fetched the player, check that the QRCode is not within the
              // Player.
              Player databasePlayer = updatedPlayer.get();
              if (databasePlayer != null
                  && !databasePlayer.getQRCodeHashes().contains(qrCode.getHash())) {
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
              if (databaseQR != null && !databaseQR.getPlayers().contains(player.getDocumentId())) {
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
}
