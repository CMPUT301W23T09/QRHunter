package com.cmput301w23t09.qrhunter.profile;

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.qrcode.DateComparator;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeAdapter;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeArray;
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

public class ProfileController {
  private final GameController gameController;
  private final ProfileFragment fragment;
  private QRCodeArray qrCodes;
  private QRCodeAdapter qrCodeAdapter;
  private FirebaseFirestore db;
  private final CollectionReference playerCollection;
  private final CollectionReference qrcodeCollection;

  public ProfileController(ProfileFragment fragment, GameController gameController) {
    this.fragment = fragment;
    this.gameController = gameController;

    // access database
    db = FirebaseFirestore.getInstance();
    playerCollection = db.collection("players");
    qrcodeCollection = db.collection("qrcodes");
  }

  public void setUpUsername(TextView usernameView) {
    // add snapshot listener for keeping username updated
    playerCollection.addSnapshotListener(
        new EventListener<QuerySnapshot>() {
          @Override
          public void onEvent(
              @Nullable QuerySnapshot queryDocumentSnapshots,
              @Nullable FirebaseFirestoreException error) {
            updateUsername(usernameView);
          }
        });
  }

  private void updateUsername(TextView usernameView) {
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

  public void setUpQRList(
      GridView qrCodeList,
      TextView totalPoints,
      TextView totalCodes,
      TextView topCodeScore,
      Spinner typeSpinner,
      Spinner orderSpinner) {
    // set QR code data and list view adapter
    qrCodes = new QRCodeArray();
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
                          fragment.getString(R.string.total_points_txt, qrCodes.getTotalScore()));
                      totalCodes.setText(
                          fragment.getString(R.string.total_codes_txt, qrCodes.size()));
                      topCodeScore.setText(
                          fragment.getString(R.string.top_code_txt, qrCodes.getTopScore()));
                      // sort codes and update qr code list view
                      updateQRListSort(typeSpinner, orderSpinner);
                    }
                  });
            });
  }

  public AdapterView.OnItemSelectedListener handleSpinnerSelect(
      Spinner typeSpinner, Spinner orderSpinner) {
    return new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        updateQRListSort(typeSpinner, orderSpinner);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {}
    };
  }

  public void handeSettingsClick() {
    ProfileSettingsFragment settingsFragment = new ProfileSettingsFragment(gameController);
    gameController.setBody(settingsFragment);
  }

  private void updateQRListSort(Spinner typeSpinner, Spinner orderSpinner) {
    // get selected spinner options
    String selectedType = typeSpinner.getSelectedItem().toString();
    String selectedOrder = orderSpinner.getSelectedItem().toString();

    // get comparator
    Comparator<QRCode> comparator =
        new ScoreComparator(); // default comparator, sorts by score in ascending order

    if (Objects.equals(selectedType, "Date Taken")) {
      comparator = new DateComparator();
    }

    if (Objects.equals(selectedOrder, "Descending")) {
      comparator = comparator.reversed();
    }

    // sort and update qr codes
    qrCodes.sort(comparator);
    qrCodeAdapter.notifyDataSetChanged();
  }

  private void showMsg(String msg) {
    Toast.makeText(fragment.getActivity(), msg, Toast.LENGTH_SHORT).show();
  }
}
