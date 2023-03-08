package com.cmput301w23t09.qrhunter.profile;

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeAdapter;
import com.cmput301w23t09.qrhunter.qrcode.ScoreComparator;
import com.cmput301w23t09.qrhunter.util.DeviceUtils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

/**
 * This is the controller for the profile fragment of the app
 */
public class ProfileController {
  /**
   * This is the profile fragment the controller handles
   */
  private final ProfileFragment fragment;
  /**
   * This is the array of QRCode objects that the fragment displays
   */
  private ArrayList<QRCode> qrCodes;
  /**
   * This is the adapter for displaying the QRCode objects
   */
  private QRCodeAdapter qrCodeAdapter;
  /**
   * This is the database the controller gets its data from
   */
  private FirebaseFirestore db;
  /**
   * This is the collection containing the qr code data
   */
  private CollectionReference qrcodeCollection;

  /**
   * This initializes the controller with its corresponding fragment
   * @param fragment
   * This is the fragment the controller manages
   */
  public ProfileController(ProfileFragment fragment) {
    this.fragment = fragment;

    // access database
    db = FirebaseFirestore.getInstance();
    qrcodeCollection = db.collection("qrcodes");
  }

  /**
   * This sets up the username view of the fragment
   * @param usernameView
   * This is the TextView that shows the username
   */
  public void setUpUsername(TextView usernameView) {
    PlayerDatabase.getInstance()
        .getPlayerByDeviceId(
            DeviceUtils.getDeviceUUID(fragment.getActivity()),
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
   * @param qrCodeList
   * This is the view that contains the list view of codes
   * @param totalPoints
   * This is the view showing the sum of code scores
   * @param totalCodes
   * This is the view showing the total number of codes
   * @param topCodeScore
   * This is the view showing the top score from the codes
   * @param orderSpinner
   * This is the spinner for selecting the sorting order of codes
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
            DeviceUtils.getDeviceUUID(fragment.getActivity()),
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
                        assert players != null;
                        if (players.contains(playerID)) {
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
   * @param orderSpinner
   * This is the spinner for selecting the sorting order of codes
   * @return
   * Return the OnItemSelectedListener for the spinner
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
   * This updates the order of qr codes shown
   * @param orderSpinner
   * This is the spinner indicating the sorting order of codes
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
   * This computes the sum of code scores
   * @return
   * Return the sum of code scores
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
   * @return
   * The top score
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
   * @param msg
   * The message to display
   */
  private void showMsg(String msg) {
    Toast.makeText(fragment.getActivity(), msg, Toast.LENGTH_SHORT).show();
  }
}
