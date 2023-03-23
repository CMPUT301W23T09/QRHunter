package com.cmput301w23t09.qrhunter;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import android.Manifest;
import android.content.Intent;
import androidx.test.espresso.DataInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import com.cmput301w23t09.qrhunter.database.DatabaseConsumer;
import com.cmput301w23t09.qrhunter.database.DatabaseQueryResults;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.profile.ProfileFragment;
import com.cmput301w23t09.qrhunter.profile.ProfileSettingsFragment;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import com.robotium.solo.Solo;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/** Test classes for profile activity */
public class TestProfileFragment {
  private Solo solo;
  private String mockPlayerID;
  private UUID mockUUID;
  private Player mockPlayer;
  private QRCode mockQR1;
  private QRCode mockQR2;
  private ArrayList<String> mockHashes;
  private ArrayList<QRCode> mockQRCodes;

  @Rule
  public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

  @Rule
  public ActivityScenarioRule<GameActivity> activityScenarioRule =
      new ActivityScenarioRule<>(GameActivity.class);

  /**
   * Runs before all tests and creates solo instance
   *
   * @throws Exception
   */
  @Before
  public void setUp() throws Exception {
    // create mock qr codes
    mockQRCodes = new ArrayList<>();
    mockHashes = new ArrayList<>();
    mockQR1 = new QRCode("0424974c68530290458c8d58674e2637f65abc127057957d7b3acbd24c208f93");
    mockQR2 = new QRCode("b5a384ee0ec5a8b625de9b24a96627c9ea5d246b70eb34a3a4f9ee781e581731");
    mockQRCodes.add(mockQR1);
    mockQRCodes.add(mockQR2);
    mockHashes.add(mockQR1.getHash());
    mockHashes.add(mockQR2.getHash());

    // create a mock player
    mockPlayerID = "001";
    mockUUID = UUID.randomUUID();
    mockPlayer =
        new Player(mockPlayerID, mockUUID, "Irene", "5873571506", "isun@ualberta.ca", mockHashes);

    // mock PlayerDatabase
    PlayerDatabase mockPlayerDatabase = mock(PlayerDatabase.class);
    // mock getting the player based on the device
    doAnswer(
            invocation -> {
              DatabaseConsumer<Player> callback = invocation.getArgument(1);
              callback.accept(new DatabaseQueryResults<>(mockPlayer));
              return null;
            })
        .when(mockPlayerDatabase)
        .getPlayerByDeviceId(any(UUID.class), any(DatabaseConsumer.class));
    // mock updating a player's info in the database
    doAnswer(
            invocation -> {
              DatabaseConsumer<Void> callback = invocation.getArgument(1);
              callback.accept(new DatabaseQueryResults<>(null, null));
              return null;
            })
        .when(mockPlayerDatabase)
        .update(any(Player.class), any(DatabaseConsumer.class));
    PlayerDatabase.mockInstance(mockPlayerDatabase);

    // mock QRCodeDatabase
    QRCodeDatabase mockQRCodeDatabase = mock(QRCodeDatabase.class);
    // mock ignoring requests for a snapshot listener
    doNothing().when(mockQRCodeDatabase).addListener(any(DatabaseChangeListener.class));
    // mock getting the qr codes for a list of hashes
    doAnswer(
            invocation -> {
              DatabaseConsumer<List<QRCode>> callback = invocation.getArgument(1);
              callback.accept(new DatabaseQueryResults<>(mockQRCodes));
              return null;
            })
        .when(mockQRCodeDatabase)
        .getQRCodeHashes(any(List.class), any(DatabaseConsumer.class));
    QRCodeDatabase.mockInstance(mockQRCodeDatabase);

    // get solo
    activityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              activity.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
              solo = new Solo(InstrumentationRegistry.getInstrumentation(), activity);
            });

    // navigate to profile fragment
    onView(withId(R.id.navigation_my_profile)).perform(click());
    await()
        .until(
            () ->
                ((GameActivity) solo.getCurrentActivity()).getController().getBody()
                    instanceof ProfileFragment);
  }

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
    onView(withId(R.id.username)).check(matches(withText(mockPlayer.getUsername())));
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
    // get the highest and lowest score
    Integer highestScore = Math.max(mockQR1.getScore(), mockQR2.getScore());
    Integer lowestScore = Math.min(mockQR1.getScore(), mockQR2.getScore());
    // get the qr code list
    DataInteraction codeList = onData(anything()).inAdapterView(withId(R.id.code_list));
    // check that the highest scoring code is displayed first (since default sort is descending)
    codeList
        .atPosition(0)
        .onChildView(withId(R.id.score))
        .check(matches(withText(String.valueOf(highestScore))));
    // change the sort order
    onView(withId(R.id.order_spinner)).perform(click());
    onData(allOf(is(instanceOf(String.class)), is("Ascending"))).perform(click());
    // check that the lowest scoring code is displayed first
    codeList
        .atPosition(0)
        .onChildView(withId(R.id.score))
        .check(matches(withText(String.valueOf(lowestScore))));
  }

  /** Checks if the total points of codes scanned is displayed correctly */
  @Test
  public void testTotalPoints() {
    // compute the total score
    Integer totalScore = mockQR1.getScore() + mockQR2.getScore();
    // check the displayed text for total code score
    onView(withId(R.id.total_points))
        .check(matches(withText(String.format("%d\nTotal Points", totalScore))));
  }

  /** Checks if the number of codes scanned is displayed correctly */
  @Test
  public void testCodesScanned() {
    // check the displayed text for total codes scanned
    onView(withId(R.id.total_codes))
        .check(matches(withText(String.format("%d\nCodes Scanned", mockQRCodes.size()))));
  }

  /** Checks if the top code score is displayed correctly */
  @Test
  public void testTopScore() {
    // compute the top score
    Integer highestScore = Math.max(mockQR1.getScore(), mockQR2.getScore());
    // get the displayed text for top code score
    onView(withId(R.id.top_code_score))
        .check(matches(withText(String.format("%d\nTop Code", highestScore))));
  }

  /** Checks if the fragment is properly changed when the settings button is clicked */
  @Test
  public void testSettingsButton() {
    // click the settings button
    onView(withId(R.id.contact_info_button)).perform(click());
    // check the current fragment
    await()
        .until(
            () ->
                ((GameActivity) solo.getCurrentActivity()).getController().getBody()
                    instanceof ProfileSettingsFragment);
  }

  @Test
  public void testSettingsBackBtn() {
    // navigate to settings
    onView(withId(R.id.contact_info_button)).perform(click());
    // click on the back button from settings
    onView(withId(R.id.settings_back_button)).perform(click());
    // check the current fragment
    await()
        .until(
            () ->
                ((GameActivity) solo.getCurrentActivity()).getController().getBody()
                    instanceof ProfileFragment);
  }

  /** Checks if the player info is properly displayed in the settings */
  @Test
  public void testSettingsInfo() {
    // click the settings button to navigate to the settings fragment
    onView(withId(R.id.contact_info_button)).perform(click());
    // search for the player's phone and email
    onView(withId(R.id.settings_screen_phoneTextField))
        .check(matches(withText(mockPlayer.getPhoneNo())));
    onView(withId(R.id.settings_screen_emailTextField))
        .check(matches(withText(mockPlayer.getEmail())));
  }

  /* Checks the change of the user's phone number */
  @Test
  public void testPhoneNumChange() {
    // click the settings button to navigate to the settings
    onView(withId(R.id.contact_info_button)).perform(click());
    // clear the current phone number and enter a new one
    String newPhoneNo = "5872571509";
    onView(withId(R.id.settings_screen_phoneTextField)).perform(click(), clearText());
    onView(withId(R.id.settings_screen_phoneTextField))
        .perform(click(), typeText(newPhoneNo), closeSoftKeyboard());
    // check the phone number input
    onView(withId(R.id.settings_screen_phoneTextField)).check(matches(withText(newPhoneNo)));
    // press the save button
    onView(withId(R.id.settings_save_button)).perform(scrollTo(), click());
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(() -> (Objects.equals(mockPlayer.getPhoneNo(), newPhoneNo)));
  }

  /** Checks the change of the user's email */
  @Test
  public void testEmailChange() {
    // click the settings button to navigate to the settings
    onView(withId(R.id.contact_info_button)).perform(click());
    // clear the current email and enter a new one
    String newEmail = "irenerose.sun@gmail.com";
    onView(withId(R.id.settings_screen_emailTextField)).perform(click(), clearText());
    onView(withId(R.id.settings_screen_emailTextField))
        .perform(click(), typeText(newEmail), closeSoftKeyboard());
    // check email input
    onView(withId(R.id.settings_screen_emailTextField)).check(matches(withText(newEmail)));
    // press the save button
    onView(withId(R.id.settings_save_button)).perform(scrollTo(), click());
    // check if player email was updated
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(() -> (Objects.equals(mockPlayer.getEmail(), newEmail)));
  }

  /** Navigates back to profile fragment */
  @After
  public void goToProfile() {
    onView(withId(R.id.navigation_my_profile)).perform(click());
    await()
        .until(
            () ->
                ((GameActivity) solo.getCurrentActivity()).getController().getBody()
                    instanceof ProfileFragment);
  }
}
