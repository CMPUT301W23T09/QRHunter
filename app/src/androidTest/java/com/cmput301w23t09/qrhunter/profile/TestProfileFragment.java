package com.cmput301w23t09.qrhunter.profile;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import android.Manifest;
import android.content.Intent;
import android.widget.EditText;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import com.cmput301w23t09.qrhunter.GameActivity;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.database.DatabaseConsumer;
import com.cmput301w23t09.qrhunter.database.DatabaseQueryResults;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import com.robotium.solo.Solo;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
    QRCode qr1 = new QRCode("0424974c68530290458c8d58674e2637f65abc127057957d7b3acbd24c208f93");
    QRCode qr2 = new QRCode("0424974c68530290458c8d58674e2637f65abc127057957d7b3acbd24c208f93");
    mockQRCodes.add(qr1);
    mockQRCodes.add(qr2);
    mockHashes.add(qr1.getHash());
    mockHashes.add(qr2.getHash());

    // create a mock player
    mockPlayerID = "001";
    mockUUID = UUID.randomUUID();
    mockPlayer =
        new Player(mockPlayerID, mockUUID, "Irene", "5873571506", "isun@ualberta.ca", mockHashes);

    // Mock PlayerDatabase
    PlayerDatabase mockPlayerDatabase = mock(PlayerDatabase.class);
    doAnswer(
            invocation -> {
              DatabaseConsumer<Player> callback = invocation.getArgument(1);
              callback.accept(new DatabaseQueryResults<>(mockPlayer));
              return null;
            })
        .when(mockPlayerDatabase)
        .getPlayerByDeviceId(any(UUID.class), any(DatabaseConsumer.class));
    doAnswer(
            invocation -> {
              DatabaseConsumer<Void> callback = invocation.getArgument(1);
              callback.accept(new DatabaseQueryResults<>(null, null));
              return null;
            })
        .when(mockPlayerDatabase)
        .update(any(Player.class), any(DatabaseConsumer.class));
    PlayerDatabase.mockInstance(mockPlayerDatabase);

    // Mock QRCodeDatabase
    QRCodeDatabase mockQRCodeDatabase = mock(QRCodeDatabase.class);
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
    solo.clickOnView(solo.getView(R.id.navigation_my_profile));
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
    assertTrue(solo.searchText(mockPlayer.getUsername()));
  }

  /**
   * Test if spinner displays spinner options correctly Assumes that the profile only has one
   * spinner
   */
  @Test
  public void testSpinnerView() {
    // checks the current selected value of spinner
    solo.isSpinnerTextSelected(0, "Descending");
    // click on spinner and select the next option
    solo.pressSpinnerItem(0, 1);
    // check if the selected option is correct
    solo.isSpinnerTextSelected(0, "Ascending");
  }

  /** Checks if the fragment is properly changed when the settings button is clicked */
  @Test
  public void testSettingsButton() {
    // click the settings button
    solo.clickOnView(solo.getView(R.id.contact_info_button));
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
    solo.clickOnView(solo.getView(R.id.contact_info_button));
    // click on the back button from settings
    solo.clickOnView(solo.getView(R.id.settings_back_button));
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
    solo.clickOnView(solo.getView(R.id.contact_info_button));
    // search for the player's phone and email
    assertTrue(solo.searchEditText(mockPlayer.getPhoneNo()));
    assertTrue(solo.searchEditText(mockPlayer.getEmail()));
  }

  /** Checks the change of the user's phone number */
  @Test
  public void testPhoneNumChange() {
    // click the settings button to navigate to the settings
    solo.clickOnView(solo.getView(R.id.contact_info_button));
    // clear the current phone number
    solo.clickOnView(solo.getView(R.id.settings_screen_phoneTextField));
    // remove current phone number
    solo.clearEditText((EditText) solo.getView(R.id.settings_screen_phoneTextField));
    // enter a new phone number
    String newPhoneNo = "5872571509";
    solo.enterText((EditText) solo.getView(R.id.settings_screen_phoneTextField), newPhoneNo);
    // check the phone number input
    assertTrue(solo.searchText(newPhoneNo));
    // press the save button
    solo.clickOnView(solo.getView(R.id.settings_save_button));
    // check if player phone number was changed
    solo.sleep(1000); // wait for update callback to finish
    assertEquals(mockPlayer.getPhoneNo(), newPhoneNo);
  }

  /** Checks the change of the user's email */
  @Test
  public void testEmailChange() {
    // click the settings button to navigate to the settings
    solo.clickOnView(solo.getView(R.id.contact_info_button));
    // clear the current email
    solo.clickOnView(solo.getView(R.id.settings_screen_emailTextField));
    solo.clearEditText((EditText) solo.getView(R.id.settings_screen_emailTextField));
    // enter a new email
    String newEmail = "irenerose.sun@gmail.com";
    solo.enterText((EditText) solo.getView(R.id.settings_screen_emailTextField), newEmail);
    // check the email input
    assertTrue(solo.searchText(newEmail));
    // press the save button
    solo.clickOnView(solo.getView(R.id.settings_save_button));
    // check if player email was changed
    solo.sleep(1000); // wait for update callback to finish
    assertEquals(mockPlayer.getEmail(), newEmail);
  }

  /** Navigate back to profile fragment */
  @After
  public void goToProfile() {
    solo.clickOnView(solo.getView(R.id.navigation_my_profile));
    await()
        .until(
            () ->
                ((GameActivity) solo.getCurrentActivity()).getController().getBody()
                    instanceof ProfileFragment);
  }
}
