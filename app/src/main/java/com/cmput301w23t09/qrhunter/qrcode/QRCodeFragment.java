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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FieldValue;
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

    addButton = view.findViewById(R.id.addButton);
    deleteButton = view.findViewById(R.id.deleteButton);

    String hash = getArguments().getString("hash");

    //initial visibility of the add and delete button
    // check if the QR code hash is already added to the player's account
    PlayerDatabase.getInstance().getPlayerByDeviceId(
            DeviceUtils.getDeviceUUID(getActivity()),
            results -> {
              if (results.isSuccessful()) {
                Player currentPlayer = results.getData();
                List<String> scannedQRCodeList = currentPlayer.getQRCodeHashes();
                if (scannedQRCodeList != null && scannedQRCodeList.contains(hash)) {
                  // QR code hash is already added to the player's account
                  addButton.setVisibility(View.GONE);
                  deleteButton.setVisibility(View.VISIBLE);
                } else {
                  // QR code hash is not yet added to the player's account
                  addButton.setVisibility(View.VISIBLE);
                  deleteButton.setVisibility(View.GONE);
                }
              } else {
                Log.w(TAG, "Error getting player by device ID.", results.getException());
                Toast.makeText(getContext(), "Error getting player by device ID.", Toast.LENGTH_SHORT).show();
              }
            });


    //implementing the add button
    addButton.setOnClickListener(v -> {
      addQRCodeToCollection(hash);
      addQRCodeToPlayerAccount(hash);
    });

    //implementing the delete button
    deleteButton.setOnClickListener(v ->{
      deleteQRCodeFromCollection(hash);
      deleteQRCodeFromPlayerAccount(hash);
  });

  }

  /**
   * Adding scanned QR code to the database collection
   * @param hash the Qrcode hash that is added to the the qrcode collection
   */
    private void addQRCodeToCollection(String hash ){
      Map<String, Object> data = new HashMap<>();
      data.put("hash", hash);

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
    }

  /**
   * adding scanned QRcode to the player's account
   * @param hash the qrcode hash that gets added to the player's account
   */
  private void addQRCodeToPlayerAccount(String hash){
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

                      //updates the qrcode document with players that have scanned a particular qr code
                      updateQRCodeDocument(hash,currentPlayer.getDocumentId());

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

    }

  /**
   * Deletes QR code from qr code collection
   * @param hash qrcode hash that gets deleted from the QRcode collection
   */
  private void deleteQRCodeFromCollection( String hash) {
    FirebaseFirestore.getInstance()
            .collection("qrcodes")
            .document(hash)
            .delete()
            .addOnSuccessListener(aVoid -> {
              Log.d(TAG, "QR code deleted from database.");
            })
            .addOnFailureListener(e -> {
              Log.w(TAG, "Error deleting QR code from collection", e);
            });
  }

  /**
   * Deletes scanned QRcode From the player's account
   * @param hash qrcode hash that gets deleted from player's account
   */
  private void deleteQRCodeFromPlayerAccount( String hash) {
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

                    //updates the qrcode document by removing players that have deleted a particular qrcode from account
                    updateQRCodeDocument(hash,currentPlayer.getDocumentId());

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
  }


  /**
   * Updates the QR code document in database with players who have scanned or removed a particular qr code
   * @param hash qrcode hash that has been scanned
   * @param playerId playerID of a player that has scanned/removed a particular qr code
   */
  private void updateQRCodeDocument(String hash, String playerId){
    // update the "players" field in the QR code document
    FirebaseFirestore.getInstance()
            .collection("qrcodes")
            .document(hash)
            .update("players", FieldValue.arrayUnion(playerId))
            .addOnSuccessListener(aVoid -> {
              Log.d(TAG, "Player updated in  QR code document.");
            })
            .addOnFailureListener(e -> {
              Log.w(TAG, "Error updating QR code document", e);
              Toast.makeText(getContext(), "Error updating QR code document", Toast.LENGTH_SHORT).show();
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
