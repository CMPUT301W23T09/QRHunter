package com.cmput301w23t09.qrhunter.profile;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeAdapter;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeFragment;
import com.cmput301w23t09.qrhunter.qrcode.ScoreComparator;
import com.cmput301w23t09.qrhunter.util.DeviceUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** This is the controller for the profile fragment of the app */
public class ProfileController {
  /** This is the game controller that controls the content on screen. */
  private final GameController gameController;
  /** This is the profile fragment the controller handles */
  private final ProfileFragment fragment;
  /** This is the array of QRCode objects that the fragment displays */
  private ArrayList<QRCode> qrCodes;
  /** This is the adapter for displaying the QRCode objects */
  private QRCodeAdapter qrCodeAdapter;
  /** Device UUID of the profile */
  private final UUID deviceUUID;

  /**
   * This initializes the controller with its corresponding fragment
   *
   * @param fragment This is the fragment the controller manages
   * @param gameController The game controller that controls the global view
   * @param deviceUUID Device UUID of the profile.
   */
  public ProfileController(
      ProfileFragment fragment, GameController gameController, UUID deviceUUID) {
    this.fragment = fragment;
    this.gameController = gameController;
    this.deviceUUID = deviceUUID;
  }

  /**
   * This sets up the username view of the fragment
   *
   * @param usernameView This is the TextView that shows the username
   */
  public void setUpUsername(TextView usernameView) {
    PlayerDatabase.getInstance()
        .getPlayerByDeviceId(
            deviceUUID,
            results -> {
              // check if database query was successful
              if (!results.isSuccessful()) {
                showMsg("An error occurred while loading in your player data.");
              }
              // otherwise get username
              usernameView.setText(results.getData().getUsername());
            });
  }

  /**
   * Sets up the list view of qr codes
   *
   * @param qrCodeList This is the view that contains the list view of codes
   * @param totalPoints This is the view that displays the total points
   * @param totalCodes This is the view that displays the total number of codes
   * @param topScore This is the view that displays the top score
   * @param orderSpinner This is the spinner that indicates the sort order
   */
  public void setUpQRList(
      GridView qrCodeList,
      TextView totalPoints,
      TextView totalCodes,
      TextView topScore,
      Spinner orderSpinner) {
    // set QR code data and list view adapter
    qrCodes = new ArrayList<>();
    qrCodeAdapter = new QRCodeAdapter(gameController.getActivity(), qrCodes);
    qrCodeList.setAdapter(qrCodeAdapter);

    // get current player
    PlayerDatabase.getInstance()
        .getPlayerByDeviceId(
            deviceUUID,
            playerCollectionResults -> {
              // check if database query was successful
              if (!playerCollectionResults.isSuccessful()) {
                showMsg("An error occurred while loading in your player data.");
                return;
              }
              // otherwise get the qr code hashes of the current player
              List<String> codeHashes = playerCollectionResults.getData().getQRCodeHashes();
              // get the qr codes from the hashes
              QRCodeDatabase.getInstance()
                  .getQRCodeHashes(
                      codeHashes,
                      QrCodeResults -> {
                        // add qr codes from result to qrCodes
                        qrCodes.clear();
                        qrCodes.addAll(QrCodeResults.getData());

                        // update qr code statistics
                        totalPoints.setText(
                            gameController
                                .getActivity()
                                .getString(R.string.total_points_txt, getTotalScore()));
                        totalCodes.setText(
                            gameController
                                .getActivity()
                                .getString(R.string.total_codes_txt, qrCodes.size()));
                        topScore.setText(
                            gameController
                                .getActivity()
                                .getString(R.string.top_code_txt, getTopScore()));

                        // sort and display qr codes
                        updateQRListSort(orderSpinner);
                      });
            });
  }

  /**
   * This creates a custom OnItemSelectedListener for the given spinner
   *
   * @param orderSpinner This is the spinner for selecting the sorting order of codes
   * @return Return the OnItemSelectedListener for the spinner
   */
  public AdapterView.OnItemSelectedListener handleSpinnerSelect(Spinner orderSpinner) {
    return new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        updateQRListSort(orderSpinner);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {}
    };
  }

  /**
   * This handles the action to take when the contact info button is clicked. Either displaying the
   * contact information or rendering the edit details fragment.
   */
  public void handleContactButtonClick() {
    if (deviceUUID.equals(DeviceUtils.getDeviceUUID(gameController.getActivity()))) {
      // Display edit settings fragment
      ProfileSettingsFragment settingsFragment =
          new ProfileSettingsFragment(gameController, deviceUUID);
      gameController.setBody(settingsFragment);
    } else {
      // Display contact info popup
      PlayerDatabase.getInstance()
          .getPlayerByDeviceId(
              deviceUUID,
              task -> {
                if (task.getException() != null) {
                  showMsg(
                      "An exception occurred while trying to load this player's contact details.");
                  return;
                }

                Player player = task.getData();
                fragment.displayContactInfo(player.getEmail(), player.getPhoneNo());
              });
    }
  }

  /**
   * This updates the order of qr codes shown
   *
   * @param orderSpinner This is the spinner indicating the sorting order of codes
   */
  private void updateQRListSort(Spinner orderSpinner) {
    // get selected spinner options
    String selectedOrder = orderSpinner.getSelectedItem().toString();

    // get comparator
    Comparator<QRCode> comparator =
        new ScoreComparator(); // default comparator, sorts by score in ascending order

    if (Objects.equals(selectedOrder, "Descending")) {
      comparator = comparator.reversed();
    }

    // sort and update qr codes
    qrCodes.sort(comparator);
    qrCodeAdapter.notifyDataSetChanged();
  }

  /**
   * This is the onclicklistener for qr codes displayed in the profile
   *
   * @return Return the onclicklistener
   */
  public AdapterView.OnItemClickListener handleQRSelect() {
    return new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        QRCode qrCode = qrCodes.get(position);
        QRCodeFragment.newInstance(qrCode, gameController.getActivePlayer())
            .show(fragment.getParentFragmentManager(), "");
      }
    };
  }

  /**
   * This computes the sum of code scores
   *
   * @return Return the sum of code scores
   */
  public int getTotalScore() {
    int total = 0;
    for (QRCode qrCode : qrCodes) {
      total += qrCode.getScore();
    }
    return total;
  }

  /**
   * This computes the top score of the qr codes
   *
   * @return The top score
   */
  public long getTopScore() {
    qrCodes.sort(new ScoreComparator().reversed());
    if (qrCodes.size() > 0) {
      QRCode topQR = qrCodes.get(0);
      return topQR.getScore();
    } else {
      return 0;
    }
  }

  /**
   * This displays a Toast message
   *
   * @param msg The message to display
   */
  private void showMsg(String msg) {
    Toast.makeText(gameController.getActivity(), msg, Toast.LENGTH_SHORT).show();
  }

  /**
   * Finds the position of the user's top QR code relative to all QR codes
   *
   * @param queryDocumentSnapshots Documents for all QR codes
   * @param topQR The user's highest scoring QR code
   * @return -1 if the user's top QR code was not found in the collection
   * @return The user's top QR position relative to all the other QR code positions
   */
  private int getTopQRPosition(QuerySnapshot queryDocumentSnapshots, QRCode topQR) {
    int position = 1;

    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
      String qrHash = documentSnapshot.getString("hash");

      if (topQR != null && qrHash.equals(topQR.getHash())) {
        return position;
      }

      position++;
    }

    return -1;
  }

  /** Calculates the percentile rank of the user's top QR code by score relative to all QR codes */
  public void calculateRankOfHighestQRScore() {
    if (qrCodes.size() <= 0) {
      return;
    }

    qrCodes.sort(new ScoreComparator().reversed());
    QRCode topQR = qrCodes.get(0);
    CollectionReference qrcodeCollection = FirebaseFirestore.getInstance().collection("qrcodes");
    Query query = qrcodeCollection.orderBy("score", Query.Direction.ASCENDING);
    query
        .get()
        .addOnSuccessListener(
            new OnSuccessListener<QuerySnapshot>() {
              @Override
              public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int topQRPosition = getTopQRPosition(queryDocumentSnapshots, topQR);
                int totalNumQRCodes = queryDocumentSnapshots.size();

                if (topQRPosition == -1) {
                  return;
                }

                float percentileRank = ((topQRPosition - 1) / (float) totalNumQRCodes) * 100;
                displayHighestQRScoreToast(percentileRank);
              }
            });
  }

  /**
   * Displays the percentile rank of the user's top QR code by score relative to all QR codes
   *
   * @param percentile Percentile value for the user's top QR code
   */
  private void displayHighestQRScoreToast(float percentile) {
    int duration = Toast.LENGTH_SHORT;
    Context context = gameController.getActivity();
    String formattedPercentile = String.format("%.2f", 100.0 - percentile);
    String message =
        String.format(
            "Your highest scoring unique QR code is in the top %s%% in terms of points.",
            formattedPercentile);
    Toast toast = Toast.makeText(context, message, duration);
    toast.show();
  }
}
