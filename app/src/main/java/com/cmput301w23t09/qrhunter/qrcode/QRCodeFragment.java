package com.cmput301w23t09.qrhunter.qrcode;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.cmput301w23t09.qrhunter.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class QRCodeFragment extends DialogFragment {

  // TODO: Figure out how to know when to display Add (+) button or Delete (Trash) button
  private TextView qrName;
  private FloatingActionButton addButton;
  private FloatingActionButton deleteButton;

  /**
   * Creates a new QRCodeFragment to display a specific QR Code
   *
   * <p>TODO: Replace hash with QRCode object
   *
   * @param hash Hash of the QR code to view
   * @return
   */
  public static QRCodeFragment newInstance(String hash) {
    Bundle args = new Bundle();
    args.putString("hash", hash);
    QRCodeFragment fragment = new QRCodeFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_qrcode, null);
    setupViews(view);
    return createAlertDialog(view);
  }

  /**
   * Binds the UI components with the attributes of the QRCode
   *
   * @param view The view that displays fragment_qrcode.xml
   */
  private void setupViews(View view) {
    qrName = view.findViewById(R.id.qrName);

    qrName.setText((String) getArguments().get("hash"));

    /**----------------------------------------------------------------**/
    String hash = getArguments().getString("hash");


    FirebaseFirestore database = FirebaseFirestore.getInstance();
    DocumentReference docRef = database.collection("qrcodes").document(hash);


    addButton = view.findViewById(R.id.addButton);
    deleteButton = view.findViewById(R.id.deleteButton);

    Task<DocumentSnapshot> task = docRef.get();

    if (task.isSuccessful()) {
      DocumentSnapshot document = task.getResult();
      if (document.exists()) {
        addButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.VISIBLE);
        deleteButton.setOnClickListener(v -> {
          // TODO: Delete the QR Code from Firestore
          database.collection("qrcodes").document(hash)
                  .delete()
                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                      Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                  })
                  .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      Log.w(TAG, "Error deleting document", e);
                    }
                  });

          dismiss();
        });
      } else {
        addButton.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.GONE);
        addButton.setOnClickListener(v -> {
          // TODO: Add the QR Code to Firestore

          Map<String, Object> data = new HashMap<>();
          data.put("hash", hash);
          database.collection("qrcodes").document(hash)
                  .set(data)
                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                      Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                  })
                  .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      Log.w(TAG, "Error writing document", e);
                    }
                  });

          dismiss();
        });
      }
    } else {
      Log.d(TAG, "get failed with ", task.getException());
    }
  }



  /**
   * Creates a dialog box to display QRCode information in
   *
   * @param view The view that displays fragment_qrcode.xml
   * @return An AlertDialog that displays QRCode information
   */
  private AlertDialog createAlertDialog(View view) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    return builder.setView(view).setPositiveButton("Close", null).create();
  }
}
