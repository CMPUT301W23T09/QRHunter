package com.cmput301w23t09.qrhunter;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.anything;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import com.cmput301w23t09.qrhunter.comment.Comment;
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

  private Player commentingPlayer1;
  private Player commentingPlayer2;
  private Player commentingPlayer3;

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
    setupMultipleComments(); // Add comments to qrCode

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

  /** Test to see if we can show multiple comments from different players */
  @Test
  public void testShowingMultipleComments() {
    onView(withText(R.string.comments_tab_title)).perform(click());
    await() // Wait for comments to load
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              ListView qrList = (ListView) solo.getView(R.id.qr_nav_items);
              return qrList.getChildCount() > 0;
            });
    ListView qrList = (ListView) solo.getView(R.id.qr_nav_items);
    assertEquals(3, qrList.getChildCount()); // Check if we do have 3 comments displayed

    // Now, let's check if each comment field shows the correct information
    // Check thelegend27's comment
    onData(anything())
        .inAdapterView(withId(R.id.qr_nav_items))
        .atPosition(0)
        .onChildView(withId(R.id.comment_player_name))
        .check(matches(withText(commentingPlayer1.getUsername())));
    onData(anything())
        .inAdapterView(withId(R.id.qr_nav_items))
        .atPosition(0)
        .onChildView(withId(R.id.player_comment_input))
        .check(matches(withText("Meh... pretty underwhelming code :(")));

    // Check GabeN's comment
    onData(anything())
        .inAdapterView(withId(R.id.qr_nav_items))
        .atPosition(1)
        .onChildView(withId(R.id.comment_player_name))
        .check(matches(withText(commentingPlayer2.getUsername())));
    onData(anything())
        .inAdapterView(withId(R.id.qr_nav_items))
        .atPosition(1)
        .onChildView(withId(R.id.player_comment_input))
        // Original comment is too long, should be truncated
        .check(matches(withText("You can find this code on the telephone ...")));

    // Check jmmabanta's comment
    onData(anything())
        .inAdapterView(withId(R.id.qr_nav_items))
        .atPosition(2)
        .onChildView(withId(R.id.comment_player_name))
        .check(matches(withText(commentingPlayer3.getUsername())));
    onData(anything())
        .inAdapterView(withId(R.id.qr_nav_items))
        .atPosition(2)
        .onChildView(withId(R.id.player_comment_input))
        .check(matches(withText("Thanks GabeN for the tip!")));
  }

  /** Test to see if clicking on a comment views the full comment in a dialog box */
  @Test
  public void testCommentDialog() {
    onView(withText(R.string.comments_tab_title)).perform(click());
    await() // Wait for comments to load
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              ListView qrList = (ListView) solo.getView(R.id.qr_nav_items);
              return qrList.getChildCount() > 0;
            });
    onData(anything())
        .inAdapterView(withId(R.id.qr_nav_items))
        .atPosition(1) // GabeN's comment is truncated in list, dialog should show full comment
        .perform(click());
    solo.waitForView(R.id.comment_dialog_player_name);
    solo.waitForView(R.id.comment_dialog_comment_text);
    onView(withId(R.id.comment_dialog_player_name))
        .inRoot(isDialog())
        .check(matches(withText(commentingPlayer2.getUsername() + "'s Comment:")));
    onView(withId(R.id.comment_dialog_comment_text))
        .inRoot(isDialog())
        .check(
            matches(
                withText(
                    "You can find this code on the telephone pole right outside our offices")));
  }

  /** Adds comments from 3 different users to qrCode */
  private void setupMultipleComments() throws InterruptedException {
    // Add 3 different players
    commentingPlayer1 =
        new Player(
            UUID.randomUUID(),
            "thelegend27",
            "7802447788",
            "thelegend27@hotmail.com",
            new ArrayList<>());
    commentingPlayer2 =
        new Player(
            UUID.randomUUID(), "GabeN", "4258899642", "gaben@valvesoftware.com", new ArrayList<>());
    commentingPlayer3 =
        new Player(
            UUID.randomUUID(), "jmmabanta", "7806862481", "mabanta@ualberta.ca", new ArrayList<>());
    CountDownLatch addPlayersTasks = new CountDownLatch(3);
    PlayerDatabase.getInstance().add(commentingPlayer1, ignored -> addPlayersTasks.countDown());
    PlayerDatabase.getInstance().add(commentingPlayer2, ignored -> addPlayersTasks.countDown());
    PlayerDatabase.getInstance().add(commentingPlayer3, ignored -> addPlayersTasks.countDown());
    addPlayersTasks.await();

    // Have each player make a comment
    Comment comment1 =
        new Comment(
            commentingPlayer1.getDocumentId(),
            commentingPlayer1.getUsername(),
            "Meh... pretty underwhelming code :(");
    Comment comment2 =
        new Comment(
            commentingPlayer2.getDocumentId(),
            commentingPlayer2.getUsername(),
            "You can find this code on the telephone pole right outside our offices");
    Comment comment3 =
        new Comment(
            commentingPlayer3.getDocumentId(),
            commentingPlayer3.getUsername(),
            "Thanks GabeN for the tip!");
    qrCode.addComment(comment1);
    qrCode.addComment(comment2);
    qrCode.addComment(comment3);
    CountDownLatch updateQRCode = new CountDownLatch(1);
    QRCodeDatabase.getInstance().updateQRCode(qrCode, ignored -> updateQRCode.countDown());
    updateQRCode.await();
  }
}
