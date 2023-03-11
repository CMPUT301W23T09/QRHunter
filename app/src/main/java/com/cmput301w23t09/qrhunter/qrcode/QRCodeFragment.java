package com.cmput301w23t09.qrhunter.qrcode;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

    docRef.get().addOnCompleteListener(task -> {
      if (task.isSuccessful()) {
        DocumentSnapshot document = task.getResult();
        if (document.exists()) {
          addButton.setVisibility(View.GONE);
          deleteButton.setVisibility(View.VISIBLE);
        } else {
          addButton.setVisibility(View.VISIBLE);
          deleteButton.setVisibility(View.GONE);
        }
      } else {
        Log.d(TAG, "failed with ", task.getException());
      }
    });


    addButton.setOnClickListener(v -> {
      Map<String, Object> data = new HashMap<>();
      data.put("hash", hash);


      // ProfileController profileController = new ProfileController();
      // Profile currentUserProfile = profileController.getCurrentUser();

      //String uid = currentUser.getUid();
      // DocumentReference userDocRef = database.collection("users").document(uid);
      FirebaseFirestore.getInstance()
              //.collection("users")
              //.document(uid)
              .collection("qrcodes")
              .document(hash)
              .set(data)
              .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "QR code added to user's account.");
                addButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.VISIBLE);
              })
              .addOnFailureListener(e -> {
                Log.w(TAG, "Error writing document", e);
                Toast.makeText(getContext(), "Error adding QR code to account.", Toast.LENGTH_SHORT).show();
              });
    });


    deleteButton.setOnClickListener(v -> {
      FirebaseFirestore.getInstance()
              //.collection("users")

              .collection("qrcodes")
              .document(hash)
              .delete()
              .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "QR code deleted from user's account.");
                addButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.GONE);
              })
              .addOnFailureListener(e -> {
                Log.w(TAG, "Error deleting document", e);
                Toast.makeText(getContext(), "Error deleting QR code from account.", Toast.LENGTH_SHORT).show();
              });
    });
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
