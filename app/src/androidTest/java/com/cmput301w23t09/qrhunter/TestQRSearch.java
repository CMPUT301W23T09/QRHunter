package com.cmput301w23t09.qrhunter;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.anything;

import android.Manifest;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.view.KeyEvent;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class TestQRSearch extends BaseTest {
  private Solo solo;
  private Location userLocation;
  private String distantCity;
  private QRLocation distantCityLocation;
  private ArrayList<QRCode> qrCodes;

  @Rule
  public GrantPermissionRule permissionRule =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

  @Rule
  public ActivityScenarioRule<GameActivity> activityScenarioRule =
      new ActivityScenarioRule<>(GameActivity.class);

  /** Runs before all tests and creates solo instance */
  @Before
  public void setUp() throws InterruptedException, IOException {
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

    // generate qr codes
    getUserLocation();
    getDistantLocation();
    generateNearbyQRCodes();

    // add generated codes to database
    addCodesToDB();
  }

  /** Get and store location of the user */
  private void getUserLocation() throws InterruptedException {
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
  }

  /** Get and store a distant location and its corresponding address */
  private void getDistantLocation() throws IOException {
    // get city of user location
    Geocoder geocoder;
    List<Address> addresses;
    geocoder = new Geocoder(solo.getCurrentActivity().getApplicationContext(), Locale.getDefault());
    addresses =
        geocoder.getFromLocation(userLocation.getLatitude(), userLocation.getLongitude(), 1);
    Address userAddress = addresses.get(0);
    String userCity = userAddress.getLocality();

    // get a city that is not the city of the user
    distantCity = (Objects.equals(userCity, "Edmonton")) ? "Calgary" : "Edmonton";
    addresses = geocoder.getFromLocationName(distantCity, 1);
    Address distantAddress = addresses.get(0);
    distantCityLocation =
        new QRLocation(
            null, distantAddress.getLatitude() + 0.0001, distantAddress.getLongitude() + 0.0001);
  }

  /** Setup list of qr codes with locations near the user */
  private void generateNearbyQRCodes() {
    // get coordinates from user Location
    Double latitude = userLocation.getLatitude();
    Double longitude = userLocation.getLongitude();

    // generate a list of nearby locations
    ArrayList<QRLocation> nearbyLocations1 = new ArrayList<>();
    nearbyLocations1.add(new QRLocation(null, latitude, longitude));

    // generate another list of nearby locations
    ArrayList<QRLocation> nearbyLocations2 = new ArrayList<>();
    latitude = (latitude < 0) ? latitude + 0.0001 : latitude - 0.0001;
    longitude = (longitude < 0) ? longitude + 0.0001 : longitude - 0.0001;
    nearbyLocations2.add(new QRLocation(null, latitude, longitude));

    // generate a list of distant locations
    ArrayList<QRLocation> distantLocations = new ArrayList<>();
    latitude = (latitude < 0) ? latitude + 5 : latitude - 5;
    longitude = (longitude < 0) ? longitude + 5 : longitude - 5;
    distantLocations.add(new QRLocation(null, latitude, longitude));
    distantLocations.add(distantCityLocation);

    // create qr codes
    QRCode qr1 = new QRCode("06388d4ff367b3bfaecb890322f0f9c6b33f5a31ec3198606cd2199fb30f5fbe");
    QRCode qr2 = new QRCode("355944495d73dd01d42d7985b45949f066c543a2b5b1960fae23797786f9a18b");
    // qr code with the largest score of 440
    // should not show up in search results for codes near the user
    QRCode qr3 = new QRCode("cc30c003ed7f48534fe5a8e9310db24eb8d488b709825e06bf312000487605e5");

    // set qr codes' locations
    qr1.setLocations(nearbyLocations1);
    qr2.setLocations(nearbyLocations2);
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

  /** Check qr search query result when location coordinates are typed in */
  @Test
  public void testSearchCodesByCoordinateQueryResult() {
    // enter geolocation coordinates into search query
    solo.waitForView(R.id.qr_searchbar);
    onView(withId(R.id.qr_searchbar)).perform(click());
    onView(withId(androidx.appcompat.R.id.search_src_text))
        .perform(
            typeText(
                String.format("%f, %f", userLocation.getLatitude(), userLocation.getLongitude())),
            pressKey(KeyEvent.KEYCODE_ENTER));
    solo.waitForView(R.id.search_qr_result);
    // check first qr code of result
    onData(anything()).inAdapterView(withId(R.id.search_qr_result)).atPosition(0).perform(click());
    onView(withId(R.id.qr_name)).check(matches(withText(qrCodes.get(1).getName())));
    onView(withId(android.R.id.button1)).perform(click());
    // check second qr code of result
    onData(anything()).inAdapterView(withId(R.id.search_qr_result)).atPosition(1).perform(click());
    onView(withId(R.id.qr_name)).check(matches(withText(qrCodes.get(2).getName())));
  }

  /** Check qr search query result when address is typed in */
  @Test
  public void testSearchCodesByAddressQueryResult() {
    // enter geolocation coordinates into search query
    solo.waitForView(R.id.qr_searchbar);
    onView(withId(R.id.qr_searchbar)).perform(click());
    onView(withId(androidx.appcompat.R.id.search_src_text))
        .perform(typeText(distantCity), pressKey(KeyEvent.KEYCODE_ENTER));
    solo.waitForView(R.id.search_qr_result, 1, 6000);
    // check first qr code of result
    onData(anything()).inAdapterView(withId(R.id.search_qr_result)).atPosition(0).perform(click());
    onView(withId(R.id.qr_name)).check(matches(withText(qrCodes.get(0).getName())));
  }
}
