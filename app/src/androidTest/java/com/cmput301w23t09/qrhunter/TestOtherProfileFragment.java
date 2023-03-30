package com.cmput301w23t09.qrhunter;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.anything;

import android.widget.TextView;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.profile.OtherProfileFragment;
import com.cmput301w23t09.qrhunter.profile.ProfileFragment;
import com.cmput301w23t09.qrhunter.qrcode.DeleteQRCodeFragment;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class TestOtherProfileFragment extends TestProfileFragment {

  private QRCode higherScoreQR;
  private QRCode lowerScoreQR;

  @Override
  public void setUp() throws Exception {
    higherScoreQR = new QRCode("b5a384ee0ec5a8b625de9b24a96627c9ea5d246b70eb34a3a4f9ee781e581731");
    lowerScoreQR = new QRCode("0424974c68530290458c8d58674e2637f65abc127057957d7b3acbd24c208f93");

    super.setUp();
  }

  @Override
  protected Player getProfilePlayer() {
    return new Player(
        UUID.randomUUID(), "DaPerson", "123-456-7890", "example@example.com", new ArrayList<>());
  }

  @Override
  protected List<QRCode> getProfileQRCodesToAdd() {
    return new ArrayList<QRCode>() {
      {
        this.add(higherScoreQR);
        this.add(lowerScoreQR);
      }
    };
  }

  @Override
  protected void openProfile() {
    GameActivity gameActivity = (GameActivity) solo.getCurrentActivity();
    OtherProfileFragment otherProfileFragment =
        new OtherProfileFragment(gameActivity.getController(), profilePlayer.getDeviceId());
    gameActivity.getController().setBody(otherProfileFragment);

    await().until(() -> gameActivity.getController().getBody() instanceof ProfileFragment);

    // Wait for the default profile to no longer exist.
    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              TextView usernameView = (TextView) solo.getView(R.id.username);
              TextView totalPoints = (TextView) solo.getView(R.id.total_points);
              return !usernameView.getText().toString().equals("")
                  && !totalPoints.getText().toString().equals("");
            });
  }

  @Test
  public void testShouldNotShowDeleteButtonOnQRCodeIfNotOwned() {
    // Click the first QR
    onData(anything()).inAdapterView(withId(R.id.code_list)).atPosition(0).perform(click());

    // DeleteQRCodeFragment should NOT be shown.
    await()
        .atMost(15, TimeUnit.SECONDS)
        .until(
            () -> {
              GameActivity gameActivity = (GameActivity) solo.getCurrentActivity();
              return !(gameActivity.getController().getPopup() instanceof DeleteQRCodeFragment);
            });
  }

  @Test
  public void testShouldShowDeleteButtonOnQRCodeIfOwned() throws InterruptedException {
    // Assign the first QR as owned.
    CountDownLatch addQRToPlayerDBTask = new CountDownLatch(1);
    QRCodeDatabase.getInstance()
        .addPlayerToQR(ourPlayer, higherScoreQR, task -> addQRToPlayerDBTask.countDown());
    addQRToPlayerDBTask.await();

    // Click the first QR
    onData(anything()).inAdapterView(withId(R.id.code_list)).atPosition(0).perform(click());

    // DeleteQRCodeFragment should be shown.
    await()
        .atMost(15, TimeUnit.SECONDS)
        .until(
            () -> {
              GameActivity gameActivity = (GameActivity) solo.getCurrentActivity();
              return gameActivity.getController().getPopup() instanceof DeleteQRCodeFragment;
            });
  }

  /** Tests that clicking the contact info button displays the contact information for the user. */
  @Test
  public void testContactInfoButton() {
    onView(withId(R.id.contact_info_button)).perform(click());

    solo.waitForView(R.id.contact_information_title);

    onView(withId(R.id.contact_email))
        .check(matches(withText(String.format("Email: %s", getProfilePlayer().getEmail()))));
    onView(withId(R.id.contact_phoneNo))
        .check(
            matches(withText(String.format("Phone Number: %s", getProfilePlayer().getPhoneNo()))));
  }
}
