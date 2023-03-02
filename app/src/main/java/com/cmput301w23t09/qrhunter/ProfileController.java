package com.cmput301w23t09.qrhunter;

import android.view.View;
import android.widget.AdapterView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;

public class ProfileController {
  private final ProfileActivity activity;

  private FirebaseFirestore db;

  public ProfileController(ProfileActivity activity) {
    this.activity = activity;
  }

  public AdapterView.OnItemSelectedListener handleSortSpinnerEvent() {
    return new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String selected = (String) parent.getItemAtPosition(pos);
        if (Objects.equals(selected, "Points")) {

        } else if (Objects.equals(selected, "Date Taken")) {

        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {}
    };
  }

  public AdapterView.OnItemSelectedListener handleOrderSpinnerEvent() {
    return new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String selected = (String) parent.getItemAtPosition(pos);
        if (Objects.equals(selected, "Descending")) {

        } else if (Objects.equals(selected, "Ascending")) {

        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {}
    };
  }

  public void handleDataUpdate() {
    // access database
    db = FirebaseFirestore.getInstance();
    final CollectionReference collectionReference = db.collection("QRcodes");
    // add snapshot listener for updating data
    collectionReference.addSnapshotListener(activity.handleQRDataUpdate());
  }
}
