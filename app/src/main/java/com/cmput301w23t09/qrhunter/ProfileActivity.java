package com.cmput301w23t09.qrhunter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeAdapter;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeArray;
import com.cmput301w23t09.qrhunter.qrcode.ScoreComparator;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Collections;

public class ProfileActivity extends Fragment {
  private final GameController gameController;
  private ProfileController controller;

  private GridView qrCodeList;
  private QRCodeAdapter qrCodeAdapter;
  private QRCodeArray qrCodes;

  private TextView username;

  private TextView totalPoints;
  private TextView totalCodes;
  private TextView topCode;

  public ProfileActivity(GameController gameController) {
    this.gameController = gameController;
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.profile_activity, container, false);
    controller = new ProfileController(this);
    createSpinners(view);
    createProfileInfo(view);
    createQRCodeList(view);
    setupSettingsButton(view);
    return view;
  }

  private void createSpinners(View view) {
    // get spinners
    Spinner sortSpinner = view.findViewById(R.id.sort_spinner);
    Spinner orderSpinner = view.findViewById(R.id.order_spinner);

    // set array adapter for spinners
    ArrayAdapter<CharSequence> sortAdapter =
        ArrayAdapter.createFromResource(
            getContext(), R.array.sort_options, android.R.layout.simple_spinner_item);
    ArrayAdapter<CharSequence> orderAdapter =
        ArrayAdapter.createFromResource(
            getContext(), R.array.order_options, android.R.layout.simple_spinner_item);

    sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    orderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    sortSpinner.setAdapter(sortAdapter);
    orderSpinner.setAdapter(orderAdapter);

    // handle item selection for sort type spinner
    sortSpinner.setOnItemSelectedListener(controller.handleSortSpinnerEvent());

    // handle item selection for sort order spinner
    orderSpinner.setOnItemSelectedListener(controller.handleOrderSpinnerEvent());
  }

  private void createProfileInfo(View view) {
    // get profile info views
    username = view.findViewById(R.id.username);
    totalPoints = view.findViewById(R.id.total_points);
    totalCodes = view.findViewById(R.id.total_codes);
    topCode = view.findViewById(R.id.top_code_score);
  }

  private void createQRCodeList(View view) {
    // get QR code list view
    qrCodeList = view.findViewById(R.id.code_list);

    // set QR code data and list view adapter
    qrCodes = new QRCodeArray();
    qrCodeAdapter = new QRCodeAdapter(getContext(), qrCodes);
    qrCodeList.setAdapter(qrCodeAdapter);

    // add data from database to qrCodes
    controller.handleDataUpdate();
  }

  private void setupSettingsButton(View view) {
    view.findViewById(R.id.settings_btn)
        .setOnClickListener(
            ignored -> {
              gameController.setBody(new ProfileSettingsFragment(gameController));
            });
  }

  // This part hasn't been properly refactored to follow the MVC model
  public EventListener<QuerySnapshot> handleQRDataUpdate() {
    return new EventListener<QuerySnapshot>() {
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
        totalPoints.setText(getString(R.string.total_points_txt, qrCodes.getTotalScore()));
        totalCodes.setText(getString(R.string.total_codes_txt, qrCodes.size()));
        topCode.setText(getString(R.string.top_code_txt, qrCodes.getTopScore()));
        // sort codes and update qr code list view
        Collections.sort(qrCodes, new ScoreComparator().reversed());
        qrCodeAdapter.notifyDataSetChanged();
      }
    };
  }
}
