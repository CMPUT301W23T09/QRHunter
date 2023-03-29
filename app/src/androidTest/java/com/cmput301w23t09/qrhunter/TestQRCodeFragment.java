package com.cmput301w23t09.qrhunter;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeFragment;
import com.robotium.solo.Solo;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
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
public class TestQRCodeFragment extends BaseTest {
  private QRCode qrCode;
  private Solo solo;
  private QRCodeFragment qrCodeFragment;
  private Player player;
  private Player playerWhoScannedQR;

  @Rule
  public ActivityScenarioRule<GameActivity> activityScenarioRule =
      new ActivityScenarioRule<>(GameActivity.class);

  /** Opens the QRCodeFragment, assuming we've scanned a QR code with hash "test-hash123" */
  @Before
  public void setUp() throws InterruptedException {
    player =
        new Player(
            UUID.randomUUID(), "johndoe42", "7801234567", "doe@ualberta.ca", new ArrayList<>());
    playerWhoScannedQR =
        new Player(
            UUID.randomUUID(), "steve", "1234567890", "example@example.com", new ArrayList<>());

    // Mock QRCode Info
    // Actual Data: CMPUT301W23T09-QRHunter
    // Hash: 8926bb85b4e02cf2c877070dd8dc920acbf6c7e0153b735a3d9381ec5c2ac11d
    // Name: RobaqinectTiger✿
    // Score: 32 PTS
    qrCode = new QRCode("8926bb85b4e02cf2c877070dd8dc920acbf6c7e0153b735a3d9381ec5c2ac11d");

    CountDownLatch playerDBTasks = new CountDownLatch(2);
    PlayerDatabase.getInstance().add(player, ignored -> playerDBTasks.countDown());
    PlayerDatabase.getInstance().add(playerWhoScannedQR, ignored -> playerDBTasks.countDown());
    playerDBTasks.await();

    CountDownLatch qrDBTasks = new CountDownLatch(1);
    QRCodeDatabase.getInstance()
        .addQRCode(
            qrCode,
            ignored ->
                QRCodeDatabase.getInstance()
                    .addPlayerToQR(playerWhoScannedQR, qrCode, ignored2 -> qrDBTasks.countDown()));
    qrDBTasks.await();

    CountDownLatch fetchLatestQR = new CountDownLatch(1);
    QRCodeDatabase.getInstance()
        .getQRCodeByHash(
            qrCode.getHash(),
            task -> {
              qrCode = task.getData();
              fetchLatestQR.countDown();
            });
    fetchLatestQR.await();

    qrCodeFragment = QRCodeFragment.newInstance(qrCode, player);
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

  /** Checks that players who scanned the QR show up */
  @Test
  public void testScannedByShowingPlayers() {
    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              ListView qrList = (ListView) solo.getView(R.id.qr_nav_items);
              return qrList.getChildCount() > 0;
            });

    // First check that only 1 player is showing
    ListView qrList = (ListView) solo.getView(R.id.qr_nav_items);
    assertEquals(1, qrList.getChildCount());

    // Next, check to see that the username and points are correct.
    onView(withId(R.id.qrcode_player_scan_name))
        .inRoot(isDialog())
        .check(matches(withText(playerWhoScannedQR.getUsername())));
    onView(withId(R.id.player_comment_input)).inRoot(isDialog()).check(matches(withText("32 PTS")));
  }
}
