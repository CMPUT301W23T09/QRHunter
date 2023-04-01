package com.cmput301w23t09.qrhunter;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.anything;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import com.cmput301w23t09.qrhunter.map.MapFragment;
import com.cmput301w23t09.qrhunter.map.QRLocation;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import com.cmput301w23t09.qrhunter.qrcode.ScoreComparator;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.robotium.solo.Solo;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class TestQRSearch extends BaseTest {
  private Solo solo;
  private Location userLocation;
  private ArrayList<QRCode> qrCodes;

  @Rule
  public GrantPermissionRule permissionRule =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

  @Rule
  public ActivityScenarioRule<GameActivity> activityScenarioRule =
      new ActivityScenarioRule<>(GameActivity.class);

  /** Runs before all tests and creates solo instance */
  @Before
  public void setUp() throws InterruptedException {
    // get solo
    activityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              activity.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
              solo = new Solo(InstrumentationRegistry.getInstrumentation(), activity);
            });

    // Open the map fragment
    onView(withId(R.id.navigation_qr_finder)).perform(click());
    await()
        .until(
            () ->
                ((GameActivity) solo.getCurrentActivity()).getController().getBody()
                    instanceof MapFragment);

    // generate qr codes near location
    generateNearbyQRCodes(getNearbyLocations());

    // add generated codes to database
    addCodesToDB();
  }

  /**
   * Get a list of locations near the user
   *
   * @return Return a list of locations near the user
   */
  private ArrayList<QRLocation> getNearbyLocations() throws InterruptedException {
    // get location
    AtomicReference<Location> retrievedLocation = new AtomicReference<>();
    CountDownLatch locationFetch = new CountDownLatch(1);

    FusedLocationProviderClient fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(
            solo.getCurrentActivity().getApplicationContext());
    fusedLocationClient
        .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
        .addOnSuccessListener(
            solo.getCurrentActivity(),
            location -> {
              if (location != null) {
                retrievedLocation.set(location);
              }

              locationFetch.countDown();
            });

    locationFetch.await();
    userLocation = retrievedLocation.get();

    // create array of nearby locations
    Location nearbyLoc = userLocation;
    nearbyLoc.setLatitude(userLocation.getLatitude() + 0.0001);
    nearbyLoc.setLongitude(userLocation.getLongitude() + 0.0001);
    ArrayList<QRLocation> nearbyLocations = new ArrayList<>();
    nearbyLocations.add(new QRLocation(userLocation));
    nearbyLocations.add(new QRLocation(nearbyLoc));
    return nearbyLocations;
  }

  /**
   * Setup list of qr codes with locations near the user
   *
   * @param qrLocations This is a list of location near the user
   */
  private void generateNearbyQRCodes(ArrayList<QRLocation> qrLocations) {
    // generate location lists for qr codes
    ArrayList<QRLocation> subList = new ArrayList<>();
    QRLocation firstLoc = qrLocations.get(0);
    subList.add(firstLoc);

    Location distantLocation = userLocation;
    if (userLocation.getLatitude() < 0) {
      distantLocation.setLatitude(userLocation.getLatitude() + 5);
    } else {
      distantLocation.setLatitude(userLocation.getLatitude() - 5);
    }
    if (userLocation.getLongitude() < 0) {
      distantLocation.setLongitude(userLocation.getLongitude() + 5);
    } else {
      distantLocation.setLatitude(userLocation.getLongitude() - 5);
    }
    ArrayList<QRLocation> distantLocations = new ArrayList<>();
    distantLocations.add(new QRLocation(distantLocation));

    // create qr codes
    QRCode qr1 = new QRCode("06388d4ff367b3bfaecb890322f0f9c6b33f5a31ec3198606cd2199fb30f5fbe");
    QRCode qr2 = new QRCode("355944495d73dd01d42d7985b45949f066c543a2b5b1960fae23797786f9a18b");
    // qr code with the largest score of 440, should not show up in search results
    QRCode qr3 = new QRCode("cc30c003ed7f48534fe5a8e9310db24eb8d488b709825e06bf312000487605e5");

    // set qr codes' locations
    qr1.setLocations(qrLocations);
    qr2.setLocations(subList);
    qr3.setLocations(distantLocations);

    // save qr codes to a list
    qrCodes = new ArrayList<>();
    qrCodes.add(qr1);
    qrCodes.add(qr2);
    qrCodes.add(qr3);
    qrCodes.sort(new ScoreComparator().reversed());
  }

  /** Add qr codes to live mock database */
  private void addCodesToDB() throws InterruptedException {
    // add qr codes to database
    CountDownLatch qrDBAddTask = new CountDownLatch(qrCodes.size());
    for (QRCode qrCode : qrCodes) {
      QRCodeDatabase.getInstance()
          .addQRCode(
              qrCode,
              ignored -> {
                qrDBAddTask.countDown();
              });
    }
    qrDBAddTask.await();
    // update qr codes with the location data
    CountDownLatch qrDBDataTask = new CountDownLatch(qrCodes.size());
    for (QRCode qrCode : qrCodes) {
      QRCodeDatabase.getInstance()
          .updateQRCode(
              qrCode,
              ignored -> {
                qrDBDataTask.countDown();
              });
    }
    qrDBDataTask.await();
  }

  /** Check qr search button click result */
  @Test
  public void testClickSearchButtonResult() {
    // click on search button
    solo.waitForView(R.id.qr_searcher);
    onView(withId(R.id.qr_searcher)).perform(click());
    // check first qr code of result
    solo.waitForView(R.id.search_qr_result);
    onData(anything()).inAdapterView(withId(R.id.search_qr_result)).atPosition(0).perform(click());
    onView(withId(R.id.qr_name)).check(matches(withText(qrCodes.get(1).getName())));
    onView(withId(android.R.id.button1)).perform(click());
    // check second qr code of result
    onData(anything()).inAdapterView(withId(R.id.search_qr_result)).atPosition(1).perform(click());
    onView(withId(R.id.qr_name)).check(matches(withText(qrCodes.get(2).getName())));
  }

  /** Check qr search query result */
  @Test
  public void testNearbyCodesQueryResult() {
    // enter geolocation coordinates into search query
    solo.waitForView(R.id.qr_searcher);
    onView(withId(R.id.qr_searcher)).perform(click());
    onView(withId(androidx.appcompat.R.id.search_src_text))
        .perform(
            typeText(
                String.format("%f, %f", userLocation.getLatitude(), userLocation.getLongitude())));
    solo.waitForView(R.id.search_qr_result);
    // check first qr code of result
    onData(anything()).inAdapterView(withId(R.id.search_qr_result)).atPosition(0).perform(click());
    onView(withId(R.id.qr_name)).check(matches(withText(qrCodes.get(1).getName())));
    onView(withId(android.R.id.button1)).perform(click());
    // check second qr code of result
    onData(anything()).inAdapterView(withId(R.id.search_qr_result)).atPosition(1).perform(click());
    onView(withId(R.id.qr_name)).check(matches(withText(qrCodes.get(2).getName())));
  }
}
