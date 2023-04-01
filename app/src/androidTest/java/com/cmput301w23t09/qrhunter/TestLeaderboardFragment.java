package com.cmput301w23t09.qrhunter;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.containsString;

import android.Manifest;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ListView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.DataInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.profile.MyProfileFragment;
import com.cmput301w23t09.qrhunter.profile.OtherProfileFragment;
import com.cmput301w23t09.qrhunter.qrcode.DeleteQRCodeFragment;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import com.google.android.material.tabs.TabLayout;
import com.robotium.solo.Solo;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class TestLeaderboardFragment extends BaseTest {

  private static Intent intent;
  protected static Player ourPlayer;

  {
    ourPlayer =
        new Player(
            getDeviceUUID(), "Our Player", "1234567890", "example@example.com", new ArrayList<>());
    intent = new Intent(ApplicationProvider.getApplicationContext(), GameActivity.class);
    intent.putExtra("activePlayer", ourPlayer);
  }

  private Solo solo;
  private QRCode qr;

  @Rule
  public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

  @Rule
  public ActivityScenarioRule<GameActivity> activityScenarioRule =
      new ActivityScenarioRule<>(intent);

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

    // Create our users.
    CountDownLatch playerDatabaseSetup = new CountDownLatch(2);
    PlayerDatabase.getInstance().add(ourPlayer, ignored -> playerDatabaseSetup.countDown());
    Player otherPlayer =
        new Player(
            UUID.randomUUID(),
            "Other Player",
            "1234567890",
            "example@example.com",
            new ArrayList<>());
    PlayerDatabase.getInstance().add(otherPlayer, ignored -> playerDatabaseSetup.countDown());
    Player otherPlayer2 =
        new Player(
            UUID.randomUUID(),
            "Other Player123",
            "1234567890",
            "example@example.com",
            new ArrayList<>());
    PlayerDatabase.getInstance().add(otherPlayer2, ignored -> playerDatabaseSetup.countDown());
    Player otherPlayer3 =
        new Player(
            UUID.randomUUID(),
            "123Other Player",
            "1234567890",
            "example@example.com",
            new ArrayList<>());
    PlayerDatabase.getInstance().add(otherPlayer3, ignored -> playerDatabaseSetup.countDown());
    Player otherPlayer4 =
        new Player(
            UUID.randomUUID(),
            "123Other Player123",
            "1234567890",
            "example@example.com",
            new ArrayList<>());
    PlayerDatabase.getInstance().add(otherPlayer4, ignored -> playerDatabaseSetup.countDown());
    playerDatabaseSetup.await();

    // Create our QRs
    CountDownLatch qrDatabaseSetup = new CountDownLatch(2);
    qr = new QRCode("b5a384ee0ec5a8b625de9b24a96627c9ea5d246b70eb34a3a4f9ee781e581731");
    QRCodeDatabase.getInstance()
        .addQRCode(
            qr,
            ignored -> {
              // This QR belongs to a player.
              QRCodeDatabase.getInstance()
                  .addPlayerToQR(ourPlayer, qr, ignored2 -> qrDatabaseSetup.countDown());
            });
    QRCode otherQR = new QRCode("0424974c68530290458c8d58674e2637f65abc127057957d7b3acbd24c208f93");
    QRCodeDatabase.getInstance().addQRCode(otherQR, ignored -> qrDatabaseSetup.countDown());
    qrDatabaseSetup.await();

    // Navigate to leaderboard screen
    onView(withId(R.id.navigation_social)).perform(click());
  }

  /** Tests the proper order of the top player scores tab */
  @Test
  public void testTopPlayerPoints() {
    selectTab(0);
    waitUntilListHasData();

    DataInteraction item =
        onData(anything()).inAdapterView(withId(R.id.leaderboard_list)).atPosition(0);

    item.onChildView(withId(R.id.leaderboard_entry_text))
        .check(matches(withText(ourPlayer.getUsername())));
    item.onChildView(withId(R.id.leaderboard_entry_score))
        .check(matches(withText(qr.getScore() + " points")));
  }

  /** Tests the proper order of the top player scans tab */
  @Test
  public void testTopPlayerScans() {
    selectTab(1);
    waitUntilListHasData();

    DataInteraction item =
        onData(anything()).inAdapterView(withId(R.id.leaderboard_list)).atPosition(0);

    item.onChildView(withId(R.id.leaderboard_entry_text))
        .check(matches(withText(ourPlayer.getUsername())));

    item.onChildView(withId(R.id.leaderboard_entry_score)).check(matches(withText("1 codes")));
  }

  /** Test the proper order of the top qr scans tab */
  @Test
  public void testTopQRScans() {
    selectTab(2);
    waitUntilListHasData();

    DataInteraction item =
        onData(anything()).inAdapterView(withId(R.id.leaderboard_list)).atPosition(0);

    item.onChildView(withId(R.id.leaderboard_entry_text)).check(matches(withText(qr.getName())));

    item.onChildView(withId(R.id.leaderboard_entry_score))
        .check(matches(withText(qr.getScore() + " points")));
  }

  /** Clicking on another player's profile should direct you to their profile. */
  @Test
  public void testClickOnOtherPlayerToProfile() {
    waitUntilListHasData();

    onData(anything()).inAdapterView(withId(R.id.leaderboard_list)).atPosition(1).perform(click());

    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              GameActivity gameActivity = (GameActivity) solo.getCurrentActivity();
              return gameActivity.getController().getBody() instanceof OtherProfileFragment;
            });
  }

  /** Clicking on your profile should direct you to your profile. */
  @Test
  public void testClickOnOurPlayerToProfile() {
    waitUntilListHasData();

    onData(anything()).inAdapterView(withId(R.id.leaderboard_list)).atPosition(0).perform(click());

    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              GameActivity gameActivity = (GameActivity) solo.getCurrentActivity();
              return gameActivity.getController().getBody() instanceof MyProfileFragment;
            });
  }

  /** Clicking on a QR you don't own should not show the DeleteQRFragment. */
  @Test
  public void testClickOnUnownedQRShouldNotShowDeleteFragment() {
    selectTab(2);
    waitUntilListHasData();

    onData(anything()).inAdapterView(withId(R.id.leaderboard_list)).atPosition(1).perform(click());

    // DeleteQRCodeFragment should NOT be shown.
    await()
        .atMost(15, TimeUnit.SECONDS)
        .until(
            () -> {
              GameActivity gameActivity = (GameActivity) solo.getCurrentActivity();
              return !(gameActivity.getController().getPopup() instanceof DeleteQRCodeFragment);
            });
  }

  /** Clicking on a QR you own should show the DeleteQRFragment. */
  @Test
  public void testClickOnOwnedQRShouldShowDeleteQRFragment() {
    selectTab(2);
    waitUntilListHasData();

    onData(anything()).inAdapterView(withId(R.id.leaderboard_list)).atPosition(0).perform(click());

    // DeleteQRCodeFragment should be shown.
    await()
        .atMost(15, TimeUnit.SECONDS)
        .until(
            () -> {
              GameActivity gameActivity = (GameActivity) solo.getCurrentActivity();
              return gameActivity.getController().getPopup() instanceof DeleteQRCodeFragment;
            });
  }

  /** Using the search bar to search for a player should transition to the player search fragment */
  @Test
  public void testSearchTransitionsToPlayerSearchFragment() {
    onView(withId(R.id.player_search)).perform(click());
    onView(isAssignableFrom(EditText.class))
        .perform(typeText("Joe"), pressKey(KeyEvent.KEYCODE_ENTER));

    onView(withId(R.id.search_linear_layout)).check(matches(isDisplayed()));
  }

  /** Searching for a player that does not exist should display a text view (Player Not Found) */
  @Test
  public void testSearchDisplaysNoPlayers() {
    onView(withId(R.id.player_search)).perform(click());
    onView(isAssignableFrom(EditText.class))
        .perform(typeText("Joe"), pressKey(KeyEvent.KEYCODE_ENTER));
    onView(withId(R.id.search_linear_layout)).check(matches(isDisplayed()));

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    onView(withText("Player Not Found.")).check(matches(isDisplayed()));
  }

  /** Presing the back button in the player search fragment should return to the leaderboards */
  @Test
  public void testPlayerSearchBackButton() {
    onView(withId(R.id.player_search)).perform(click());
    onView(isAssignableFrom(EditText.class))
        .perform(typeText("Joe"), pressKey(KeyEvent.KEYCODE_ENTER));
    onView(withId(R.id.search_linear_layout)).check(matches(isDisplayed()));

    onView(withId(R.id.back_button)).perform(click());
    onView(withId(R.id.leaderboard_linear_layout)).check(matches(isDisplayed()));
  }

  /**
   * Searching for a player should display related player names with the exact match first and names
   * with the exact match prefix in descending order based on closeness to the front
   */
  @Test
  public void testRelatedUsernameSearch() {
    onView(withId(R.id.player_search)).perform(click());
    onView(isAssignableFrom(EditText.class))
        .perform(typeText("Other Player"), pressKey(KeyEvent.KEYCODE_ENTER));
    onView(withId(R.id.search_linear_layout)).check(matches(isDisplayed()));
    waitUntilSearchListHasData();
    onData(anything())
        .inAdapterView(withId(R.id.search_query_list))
        .atPosition(0)
        .onChildView(withId(R.id.search_query_entry_text))
        .check(matches(withText("Other Player")));
    onData(anything())
        .inAdapterView(withId(R.id.search_query_list))
        .atPosition(1)
        .onChildView(withId(R.id.search_query_entry_text))
        .check(matches(withText("Other Player123")));
    onData(anything())
        .inAdapterView(withId(R.id.search_query_list))
        .atPosition(2)
        .onChildView(withId(R.id.search_query_entry_text))
        .check(matches(withText(containsString("123Other Player"))));
  }

  /**
   * Utility method to select a tab once on the leaderboard screen.
   *
   * @param position tab position
   */
  private void selectTab(int position) {
    TabLayout tabLayout = (TabLayout) solo.getView(R.id.leaderboard_navigation);

    solo.getCurrentActivity().runOnUiThread(() -> tabLayout.getTabAt(position).select());
    await().atMost(5, TimeUnit.SECONDS).until(() -> tabLayout.getSelectedTabPosition() == position);
  }

  /** Wait until the leaderboard list has entries. */
  private void waitUntilListHasData() {
    await()
        .atMost(5, TimeUnit.SECONDS)
        .until(
            () -> {
              ListView listView = (ListView) solo.getView(R.id.leaderboard_list);
              return listView.getChildCount() > 0;
            });
  }

  /** Wait until the search query list has entries. */
  private void waitUntilSearchListHasData() {
    await()
        .atMost(5, TimeUnit.SECONDS)
        .until(
            () -> {
              ListView listView = (ListView) solo.getView(R.id.search_query_list);
              return listView.getChildCount() == 4;
            });
  }
}
