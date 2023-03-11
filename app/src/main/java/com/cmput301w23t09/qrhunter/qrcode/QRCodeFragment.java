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
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.util.DeviceUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    String hash = getArguments().getString("hash");

    addButton = view.findViewById(R.id.addButton);
    deleteButton = view.findViewById(R.id.deleteButton);

    addButton.setVisibility(View.VISIBLE);
    deleteButton.setVisibility(View.GONE);


    //implementing the add button
    addButton.setOnClickListener(v -> {
      Map<String, Object> data = new HashMap<>();
      data.put("hash", hash);

      //adding QR code to the qrcode collection
      FirebaseFirestore.getInstance()
              .collection("qrcodes")
              .document(hash)
              .set(data)
              .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "QR code added to database.");
              })
              .addOnFailureListener(e -> {
                Log.w(TAG, "Error writing document", e);
                Toast.makeText(getContext(), "Error adding QR code to Database", Toast.LENGTH_SHORT).show();
              });

      //adding QRCode to player account
      PlayerDatabase.getInstance().getPlayerByDeviceId(
              DeviceUtils.getDeviceUUID(getActivity()),
              results -> {
                // check if database query was successful
                if (results.isSuccessful()) {
                  Player currentPlayer = results.getData();

                  // add the QR code to the player's account
                  List<String> scannedqrCodelist = currentPlayer.getQRCodeHashes();
                  if (scannedqrCodelist == null) {
                    scannedqrCodelist= new ArrayList<>();
                  }
                  scannedqrCodelist.add(hash);
                  currentPlayer.setQRCodeHashes(scannedqrCodelist);

                  // update the player's account in the database
                  PlayerDatabase.getInstance().update(currentPlayer, queryResult -> {
                    if (queryResult.isSuccessful()) {
                      Log.d(TAG, "QR code added to user's account.");
                      // update the visibility of the buttons
                      addButton.setVisibility(View.GONE);
                      deleteButton.setVisibility(View.VISIBLE);

                    } else {
                      Log.w(TAG, "Error adding QR code to account.", queryResult.getException());
                      Toast.makeText(getContext(), "Error adding QR code to account.", Toast.LENGTH_SHORT).show();
                    }
                  });
                } else {
                  Log.w(TAG, "Error getting player by device ID.", results.getException());
                  Toast.makeText(getContext(), "Error getting player by device ID.", Toast.LENGTH_SHORT).show();
                }
              });

    });

    //implementing the delete button
    deleteButton.setOnClickListener(v -> {
      FirebaseFirestore.getInstance()
              .collection("qrcodes")
              .document(hash)
              .delete()
              .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "QR code deleted from database.");

                // remove the QR code from the player's account
                PlayerDatabase.getInstance().getPlayerByDeviceId(
                        DeviceUtils.getDeviceUUID(getActivity()),
                        results -> {
                          // check if database query was successful
                          if (results.isSuccessful()) {
                            Player currentPlayer = results.getData();

                            // remove the QR code from the player's account
                            List<String> scannedqrCodelist = currentPlayer.getQRCodeHashes();
                            if (scannedqrCodelist == null) {
                              scannedqrCodelist= new ArrayList<>();
                            }
                            scannedqrCodelist.remove(hash);
                            currentPlayer.setQRCodeHashes(scannedqrCodelist);

                            // update the player's account in the database
                            PlayerDatabase.getInstance().update(currentPlayer, queryResult -> {
                              if (queryResult.isSuccessful()) {
                                Log.d(TAG, "QR code deleted from user's account.");
                                addButton.setVisibility(View.VISIBLE);
                                deleteButton.setVisibility(View.GONE);

                              } else {
                                Log.w(TAG, "Error deleting QR code from account.", queryResult.getException());
                                Toast.makeText(getContext(), "Error deleting QR code from account.", Toast.LENGTH_SHORT).show();
                              }
                            });
                          } else {
                            Log.w(TAG, "Error getting player by device ID.", results.getException());
                            Toast.makeText(getContext(), "Error getting player by device ID.", Toast.LENGTH_SHORT).show();
                          }
                        });
              })
              .addOnFailureListener(e -> {
                Log.w(TAG, "Error deleting document", e);
                Toast.makeText(getContext(), "Error deleting QR code from database.", Toast.LENGTH_SHORT).show();
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
