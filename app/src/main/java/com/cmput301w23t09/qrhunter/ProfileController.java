package com.cmput301w23t09.qrhunter;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.qrcode.DateComparator;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeAdapter;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeArray;
import com.cmput301w23t09.qrhunter.qrcode.ScoreComparator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Comparator;
import java.util.Objects;

public class ProfileController {
  private final ProfileFragment view;
  private Player player;
  private QRCodeArray qrCodes;
  private QRCodeAdapter qrCodeAdapter;
  private FirebaseFirestore db;

  public ProfileController(ProfileFragment fragment) {
    view = fragment;
  }

  public void setUpUsername(TextView usernameView) {}

  public void setUpQRList(
      GridView qrCodeList,
      TextView totalPoints,
      TextView totalCodes,
      TextView topCodeScore,
      Spinner typeSpinner,
      Spinner orderSpinner) {
    // set QR code data and list view adapter
    qrCodes = new QRCodeArray();
    qrCodeAdapter = new QRCodeAdapter(view.getContext(), qrCodes);
    qrCodeList.setAdapter(qrCodeAdapter);

    // access database
    db = FirebaseFirestore.getInstance();
    final CollectionReference collectionReference = db.collection("qrcodes");

    // add snapshot listener for updating data
    collectionReference.addSnapshotListener(
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
              String hash = doc.getId();
              Integer score = (int) (long) doc.getData().get("score");
              qrCodes.add(new QRCode(hash, null, null, score, null, null, null, null));
            }
            // update qr code statistics
            totalPoints.setText(view.getString(R.string.total_points_txt, qrCodes.getTotalScore()));
            totalCodes.setText(view.getString(R.string.total_codes_txt, qrCodes.size()));
            topCodeScore.setText(view.getString(R.string.top_code_txt, qrCodes.getTopScore()));
            // sort codes and update qr code list view
            updateQRListSort(typeSpinner, orderSpinner);
          }
        });
  }

  public void setUpSortSpinners(Spinner typeSpinner, Spinner orderSpinner) {
    // set array adapters for spinners
    ArrayAdapter<CharSequence> typeAdapter =
        ArrayAdapter.createFromResource(
            view.getContext(), R.array.sort_options, android.R.layout.simple_spinner_item);

    ArrayAdapter<CharSequence> orderAdapter =
        ArrayAdapter.createFromResource(
            view.getContext(), R.array.order_options, android.R.layout.simple_spinner_item);

    typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    orderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    typeSpinner.setAdapter(typeAdapter);
    orderSpinner.setAdapter(orderAdapter);

    // handle item selection for the spinners
    typeSpinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            updateQRListSort(typeSpinner, orderSpinner);
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {}
        });

    orderSpinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            updateQRListSort(typeSpinner, orderSpinner);
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {}
        });
  }

  public void updateQRListSort(Spinner typeSpinner, Spinner orderSpinner) {
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
}
