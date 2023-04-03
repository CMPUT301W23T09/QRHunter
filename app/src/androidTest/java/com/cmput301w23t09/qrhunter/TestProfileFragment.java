package com.cmput301w23t09.qrhunter;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;

import android.Manifest;
import android.content.Intent;
import android.widget.GridView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.DataInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.profile.ProfileFragment;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import com.cmput301w23t09.qrhunter.qrcode.ScoreComparator;
import com.robotium.solo.Solo;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/** Test classes for profile activity */
public abstract class TestProfileFragment extends BaseTest {

  private static final Intent intent;
  private static final Player ourPlayer;

  static {
    ourPlayer =
        new Player(
            getDeviceUUID(),
            "Irene",
            "5873571506",
            "isun@ualberta.ca",
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>());
    intent = new Intent(ApplicationProvider.getApplicationContext(), GameActivity.class);
    intent.putExtra("activePlayer", ourPlayer);
  }

  protected Solo solo;
  protected Player profilePlayer;
  protected Player localPlayer;

  @Rule
  public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

  @Rule
  public ActivityScenarioRule<GameActivity> activityScenarioRule =
      new ActivityScenarioRule<>(intent);

  /**
   * Runs before all tests and creates solo instance
   *
   * @throws Exception
   */
  @Before
  public void setUp() throws Exception {
    // get solo
    activityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              activity.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
              solo = new Solo(InstrumentationRegistry.getInstrumentation(), activity);
            });

    // Create our user and the profile player.
    CountDownLatch playerDatabaseSetup = new CountDownLatch(1);
    profilePlayer = getProfilePlayer();

    PlayerDatabase.getInstance()
        .add(
            ourPlayer,
            task -> {
              // Update GameController activePlayer document id to match the record of the player
              // being added

              // Update the GameActivity activePlayer
              Player storedActivePlayer =
                  ((GameActivity) solo.getCurrentActivity()).getController().getActivePlayer();
              storedActivePlayer.setDocumentId(task.getData().getDocumentId());
              localPlayer = storedActivePlayer;
              ourPlayer.setDocumentId(null); // reset the document id assigned to our template

              // Now that we added the main player, do we need to add the profile player too?
              if (profilePlayer.getDeviceId().equals(ourPlayer.getDeviceId())) {
                // No we don't.
                playerDatabaseSetup.countDown();
              } else {
                // Yes we do.
                PlayerDatabase.getInstance()
                    .add(profilePlayer, ignored2 -> playerDatabaseSetup.countDown());
              }
            });

    // Wait for the JUnit thread to be told that the players were added to the database.
    playerDatabaseSetup.await();

    // Add the QRs to the profile.
    List<QRCode> qrsToAdd = getProfileQRCodesToAdd();
    CountDownLatch qrDBTasks = new CountDownLatch(qrsToAdd.size());
    for (QRCode qrCode : qrsToAdd) {
      QRCodeDatabase.getInstance()
          .addQRCode(
              qrCode,
              ignored -> {
                // Add the player to this QR.
                QRCodeDatabase.getInstance()
                    .addPlayerToQR(profilePlayer, qrCode, ignoredQr -> qrDBTasks.countDown());
              });
    }
    qrDBTasks.await();

    // Open the profile.
    openProfile();
  }

  /**
   * Retrieve the reference to use for the player of the profile we are viewing.
   *
   * @return the player
   */
  protected abstract Player getProfilePlayer();

  /**
   * Retrieve the QRs to add to the profile we are viewing.
   *
   * @return qrs to add
   */
  protected abstract List<QRCode> getProfileQRCodesToAdd();

  /** Code that opens the profile before every test. */
  protected abstract void openProfile();

  /** Checks if the current fragment is correct */
  @Test
  public void testStartProfile() {
    // get current fragment and check if it is a profile fragment
    GameController gc = ((GameActivity) solo.getCurrentActivity()).getController();
    assertTrue(gc.getBody() instanceof ProfileFragment);
  }

  /** Checks if the username is correctly displayed */
  @Test
  public void testUsernameView() {
    // check if mockPlayer's username is displayed
    onView(withId(R.id.username)).check(matches(withText(profilePlayer.getUsername())));
  }

  /**
   * Test if spinner displays spinner options correctly Assumes that the profile only has one
   * spinner
   */
  @Test
  public void testSpinnerView() {
    // checks the current selected value of spinner
    onView(withId(R.id.order_spinner)).check(matches(withSpinnerText("Descending")));
    // click on spinner and select the next option
    onView(withId(R.id.order_spinner)).perform(click());
    onData(allOf(is(instanceOf(String.class)), is("Ascending"))).perform(click());
    // check if the selected option is correct
    onView(withId(R.id.order_spinner)).check(matches(withSpinnerText("Ascending")));
  }

  /** Checks if qr codes are properly sorted and displayed */
  @Test
  public void testQRListView() {
    waitForProfileQRsToAppear();

    // get the highest and lowest score
    Integer highestScore =
        getProfileQRCodesToAdd().stream().mapToInt(QRCode::getScore).reduce(0, Integer::max);
    Integer lowestScore =
        getProfileQRCodesToAdd().stream()
            .mapToInt(QRCode::getScore)
            .reduce(Integer.MAX_VALUE, Integer::min);
    // get the qr code list
    DataInteraction codeList = onData(anything()).inAdapterView(withId(R.id.code_list));
    // check that the highest scoring code is displayed first (since default sort is descending)
    codeList
        .atPosition(0)
        .onChildView(withId(R.id.score))
        .check(matches(withText(String.valueOf(highestScore) + " PTS")));
    // change the sort order
    onView(withId(R.id.order_spinner)).perform(click());
    onData(allOf(is(instanceOf(String.class)), is("Ascending"))).perform(click());
    // check that the lowest scoring code is displayed first
    codeList
        .atPosition(0)
        .onChildView(withId(R.id.score))
        .check(matches(withText(String.valueOf(lowestScore) + " PTS")));
  }

  /** Checks if the total points of codes scanned is displayed correctly */
  @Test
  public void testTotalPoints() {
    // compute the total score
    Integer totalScore =
        getProfileQRCodesToAdd().stream().mapToInt(QRCode::getScore).reduce(0, Integer::sum);
    // check the displayed text for total code score
    onView(withId(R.id.total_points))
        .check(matches(withText(String.format("%d\nTotal Points", totalScore))));
  }

  /** Checks if the number of codes scanned is displayed correctly */
  @Test
  public void testCodesScanned() {
    // check the displayed text for total codes scanned
    onView(withId(R.id.total_codes))
        .check(matches(withText(String.format("%d\nCodes Scanned", 2))));
  }

  /** Checks if the top code score is displayed correctly */
  @Test
  public void testTopScore() {
    // compute the top score
    Integer highestScore =
        getProfileQRCodesToAdd().stream().mapToInt(QRCode::getScore).reduce(0, Integer::max);
    // get the displayed text for top code score
    onView(withId(R.id.top_code_score))
        .check(matches(withText(String.format("%d\nTop Code", highestScore))));
  }

  /** Checks if the correct QRCodeFragment pops up when a qr code is selected */
  @Test
  public void testQRClick() {
    waitForProfileQRsToAppear();

    // get the list of qr codes sorted in descending order (default order of profile's qr code list)
    List<QRCode> sortedCodes = getProfileQRCodesToAdd();
    sortedCodes.sort(new ScoreComparator().reversed());
    // get the qr code list
    DataInteraction codeList = onData(anything()).inAdapterView(withId(R.id.code_list));
    // click on an item in the code list
    codeList.atPosition(0).perform(click());
    // check that the correct QRCodeFragment is displayed
    onView(withId(R.id.qr_name)).check(matches(withText(sortedCodes.get(0).getName())));
  }

  protected void waitForProfileQRsToAppear() {
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(
            () -> {
              GridView qrs = (GridView) solo.getView(R.id.code_list);
              return qrs.getChildCount() > 0;
            });
  }
}
