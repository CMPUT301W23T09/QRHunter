package com.cmput301w23t09.qrhunter.profile;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeAdapter;
import com.cmput301w23t09.qrhunter.qrcode.ScoreComparator;
import com.cmput301w23t09.qrhunter.util.DeviceUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Comparator;
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
  /** This is the database the controller gets its data from */
  private final FirebaseFirestore db;
  /** This is the collection containing the qr code data */
  private final CollectionReference qrcodeCollection;

  private final UUID deviceUUID;

  /**
   * This initializes the controller with its corresponding fragment
   *
   * @param fragment This is the fragment the controller manages
   * @param gameController The game controller that controls the global view
   */
  public ProfileController(
      ProfileFragment fragment, GameController gameController, UUID deviceUUID) {
    this.fragment = fragment;
    this.gameController = gameController;
    this.deviceUUID = deviceUUID;

    // access database
    db = FirebaseFirestore.getInstance();
    qrcodeCollection = db.collection("qrcodes");
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
   * @param totalPoints This is the view showing the sum of code scores
   * @param totalCodes This is the view showing the total number of codes
   * @param topCodeScore This is the view showing the top score from the codes
   * @param orderSpinner This is the spinner for selecting the sorting order of codes
   */
  public void setUpQRList(
      GridView qrCodeList,
      TextView totalPoints,
      TextView totalCodes,
      TextView topCodeScore,
      Spinner orderSpinner) {
    // set QR code data and list view adapter
    qrCodes = new ArrayList<>();
    qrCodeAdapter = new QRCodeAdapter(fragment.getContext(), qrCodes);
    qrCodeList.setAdapter(qrCodeAdapter);

    // get current player
    PlayerDatabase.getInstance()
        .getPlayerByDeviceId(
            deviceUUID,
            results -> {
              // check if database query was successful
              if (!results.isSuccessful()) {
                showMsg("An error occurred while loading in your player data.");
                return;
              }
              // otherwise get the qr codes for the current player
              String playerID = results.getData().getDocumentId();
              // add snapshot listener for updating data
              qrcodeCollection.addSnapshotListener(
                  new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(
                        @Nullable QuerySnapshot queryDocumentSnapshots,
                        @Nullable FirebaseFirestoreException error) {
                      // clear QR code data
                      qrCodes.clear();
                      // add data from database
                      assert queryDocumentSnapshots != null;
                      for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        // get list of players that have scanned the qr code
                        ArrayList<String> players = (ArrayList<String>) doc.get("players");
                        // add the qr code if the current player has scanned it
                        if (players != null && players.contains(playerID)) {
                          String hash = doc.getId();
                          Integer score = (int) (long) doc.get("score");
                          qrCodes.add(new QRCode(hash, null, null, score, null, null, null, null));
                        }
                      }
                      // update qr code statistics
                      totalPoints.setText(
                          fragment.getString(R.string.total_points_txt, getTotalScore()));
                      totalCodes.setText(
                          fragment.getString(R.string.total_codes_txt, qrCodes.size()));
                      topCodeScore.setText(
                          fragment.getString(R.string.top_code_txt, getTopScore()));
                      // sort codes and update qr code list view
                      updateQRListSort(orderSpinner);
                    }
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
        /** implement showing a qrcode dialog fragment * */
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
  public int getTopScore() {
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
    Toast.makeText(fragment.getActivity(), msg, Toast.LENGTH_SHORT).show();
  }

  /**
   * Finds the position of the user's top QR code relative to all QR codes
   * @param queryDocumentSnapshots Documents for all QR codes
   * @param topQR The user's highest scoring QR code
   * @return -1 if the user's top QR code was not found in the collection
   * @return The user's top QR position relative to all the other QR code positions
   */
  private int getTopQRPosition(QuerySnapshot queryDocumentSnapshots, QRCode topQR) {
    int position = 1;

    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
      String qrHash = documentSnapshot.getString("hash");
//      int score = documentSnapshot.getLong("score").intValue();

      if (topQR != null && qrHash.equals(topQR.getHash())) {
        return position;
      }

      position++;
    }

    return -1;
  }

  /**
   * Calculates the percentile rank of the user's top QR code by score relative to all QR codes
   */
  public void calculateRankOfHighestQRScore() {
    if (qrCodes.size() <= 0) {
      return;
    }

    qrCodes.sort(new ScoreComparator().reversed());
    QRCode topQR = qrCodes.get(0);
    Query query = qrcodeCollection.orderBy("score", Query.Direction.ASCENDING);
    query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
   * @param percentile Percentile value for the user's top QR code
   */
  private void displayHighestQRScoreToast(float percentile) {
    int duration = Toast.LENGTH_SHORT;
    Context context = fragment.getActivity();
    String formattedPercentile = String.format("%.2f", 100.0 - percentile);
    String message = String.format("Your highest scoring unique QR code is in the top %s%% in terms of points.", formattedPercentile);
    Toast toast = Toast.makeText(context, message, duration);
    toast.show();
  }
}
