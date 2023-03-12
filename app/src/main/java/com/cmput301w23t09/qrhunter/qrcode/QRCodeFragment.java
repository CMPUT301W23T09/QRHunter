package com.cmput301w23t09.qrhunter.qrcode;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.map.LocationHandler;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.scanqr.LocationPhotoController;
import com.cmput301w23t09.qrhunter.scanqr.LocationPhotoFragment;
import com.cmput301w23t09.qrhunter.scanqr.camera.CameraLocationPhotoController;
import com.cmput301w23t09.qrhunter.util.DeviceUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QRCodeFragment extends DialogFragment implements Serializable {

  // TODO: Figure out how to know when to display Add (+) button or Delete (Trash) button
  private TextView qrName;
  private ImageView locationPhoto;
  private QRCode qrCode;
  private Button takeLocationPhotoBtn;
  private CheckBox locationCheckbox;
  private LocationHandler locationHandler;
  private LocationPhotoFragment locationPhotoFragment;
  private Player activePlayer;
  private FloatingActionButton addButton;
  private FloatingActionButton deleteButton;

  /**
   * Creates a new QRCodeFragment to display a specific QR Code
   * @param qrCode The QR code to view
   * @return
   */
  public static QRCodeFragment newInstance(QRCode qrCode, Player activePlayer) {
    Bundle args = new Bundle();
    args.putSerializable("qrcode", qrCode);
    args.putSerializable("activePlayer", activePlayer);
    QRCodeFragment fragment = new QRCodeFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_qrcode, null);
    qrCode = (QRCode) getArguments().getSerializable("qrcode");
    activePlayer = (Player) getArguments().getSerializable("activePlayer");
    locationHandler = new LocationHandler(this);
    setupViews(view);
    updateLocationPhoto();
    return createAlertDialog(view);
  }

  /**
   * Binds the UI components with the attributes of the QRCode
   *
   * @param view The view that displays fragment_qrcode.xml
   */
  private void setupViews(View view) {
    qrName = view.findViewById(R.id.qrName);
    locationPhoto = view.findViewById(R.id.location_photo);
    locationCheckbox = view.findViewById(R.id.location_request_box);
    qrName.setText(qrCode.getHash());
    takeLocationPhotoBtn = view.findViewById(R.id.take_location_photo_btn);
    takeLocationPhotoBtn.setOnClickListener(
        v -> {
          if (qrCode.getPhotos().size() > 0) {
            qrCode.deletePhoto(qrCode.getPhotos().get(0));
            updateLocationPhoto();
          } else {
            locationPhotoFragment = LocationPhotoFragment.newInstance(qrCode, this, activePlayer);
            locationPhotoFragment.show(getParentFragmentManager(), "Take Location Photo");
          }
        });
    locationCheckbox.setOnCheckedChangeListener(
        (buttonView, isChecked) -> {
          if (isChecked) {
            locationHandler.setQrToLastLocation(qrCode);
          } else {
            qrCode.setLoc(null);
          }
        });
  }

  /**
   * Updates the locationPhoto image view to show the newly-captured location photo
   *
   * @see CameraLocationPhotoController
   * @see LocationPhotoController
   */
  public void updateLocationPhoto() {
    if (qrCode.getPhotos() != null && qrCode.getPhotos().size() > 0) {
      takeLocationPhotoBtn.setText(R.string.remove_location_photo);
      locationPhoto.setImageBitmap(qrCode.getPhotos().get(0).getPhoto());
    } else {
      takeLocationPhotoBtn.setText(R.string.take_location_photo);
      locationPhoto.setImageResource(android.R.color.transparent);
    }
  }

  /**
   * Disables the "Record QR Location" box if the user has not granted location permissions
   *
   * @param requestCode The request code passed in {@link #requestPermissions(String[], int)}.
   * @param permissions The requested permissions. Never null.
   * @param grantResults The grant results for the corresponding permissions which is either {@link
   *     android.content.pm.PackageManager#PERMISSION_GRANTED} or {@link
   *     android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
   */
  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == LocationHandler.REQUEST_CODE_PERMISSIONS) {
      if (!locationHandler.locationPermissionsGranted()) {
        locationCheckbox.setEnabled(false);
      }
    }

    qrName = getView().findViewById(R.id.qrName);
    qrName.setText((String) getArguments().get("hash"));

    addButton = getView().findViewById(R.id.addButton);
    deleteButton = getView().findViewById(R.id.deleteButton);

    String hash = getArguments().getString("hash");

    // initial visibility of the add and delete button
    // check if the QR code hash is already added to the player's account
    PlayerDatabase.getInstance()
        .getPlayerByDeviceId(
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
                Toast.makeText(
                        getContext(), "Error getting player by device ID.", Toast.LENGTH_SHORT)
                    .show();
              }
            });

    // implementing the add button
    addButton.setOnClickListener(
        v -> {
          addQRCodeToCollection(hash);
          addQRCodeToPlayerAccount(hash);
        });

    // implementing the delete button
    deleteButton.setOnClickListener(
        v -> {
          deleteQRCodeFromCollection(hash);
          deleteQRCodeFromPlayerAccount(hash);
        });
  }

  /**
   * Adding scanned QR code to the database collection
   *
   * @param hash the Qrcode hash that is added to the the qrcode collection
   */
  private void addQRCodeToCollection(String hash) {
    Map<String, Object> data = new HashMap<>();
    data.put("hash", hash);

    FirebaseFirestore.getInstance()
        .collection("qrcodes")
        .document(hash)
        .set(data)
        .addOnSuccessListener(
            aVoid -> {
              Log.d(TAG, "QR code added to database.");
            })
        .addOnFailureListener(
            e -> {
              Log.w(TAG, "Error writing document", e);
              Toast.makeText(getContext(), "Error adding QR code to Database", Toast.LENGTH_SHORT)
                  .show();
            });
  }

  /**
   * adding scanned QR code to the player's account
   * @param hash qrcode hash that gets added to the player's account
   */
  private void addQRCodeToPlayerAccount(String hash) {
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

                  // update the player's account in the database with scanned qr code
                  PlayerDatabase.getInstance().update(currentPlayer, queryResult -> {
                    if (queryResult.isSuccessful()) {
                      Log.d(TAG, "QR code added to user's account.");
                      // update the visibility of the buttons
                      addButton.setVisibility(View.GONE);
                      deleteButton.setVisibility(View.VISIBLE);

                      //updates the qrcode document with players that have scanned a particular qr code
                      updateQRCodeDocument(hash,currentPlayer.getDocumentId());

                // add the QR code to the player's account
                List<String> scannedqrCodelist = currentPlayer.getQRCodeHashes();
                if (scannedqrCodelist == null) {
                  scannedqrCodelist = new ArrayList<>();
                }
                scannedqrCodelist.add(hash);
                currentPlayer.setQRCodeHashes(scannedqrCodelist);

                // update the player's account in the database
                PlayerDatabase.getInstance()
                    .update(
                        currentPlayer,
                        queryResult -> {
                          if (queryResult.isSuccessful()) {
                            Log.d(TAG, "QR code added to user's account.");
                            // update the visibility of the buttons
                            addButton.setVisibility(View.GONE);
                            deleteButton.setVisibility(View.VISIBLE);

                            // updates the qrcode document with players that have scanned a
                            // particular qr code
                            updateQRCodeDocument(hash, currentPlayer.getDocumentId());

                          } else {
                            Log.w(
                                TAG,
                                "Error adding QR code to account.",
                                queryResult.getException());
                            Toast.makeText(
                                    getContext(),
                                    "Error adding QR code to account.",
                                    Toast.LENGTH_SHORT)
                                .show();
                          }
                        });
              } else {
                Log.w(TAG, "Error getting player by device ID.", results.getException());
                Toast.makeText(
                        getContext(), "Error getting player by device ID.", Toast.LENGTH_SHORT)
                    .show();
              }
            });
  }

  /**
   * Deletes QR code from QR code collection
   * @param hash qrcode hash that gets deleted from the QR code collection
   */
  private void deleteQRCodeFromCollection(String hash) {
    FirebaseFirestore.getInstance()
        .collection("qrcodes")
        .document(hash)
        .delete()
        .addOnSuccessListener(
            aVoid -> {
              Log.d(TAG, "QR code deleted from database.");
            })
        .addOnFailureListener(
            e -> {
              Log.w(TAG, "Error deleting QR code from collection", e);
            });
  }

  /**
   * Deletes scanned QR code From the player's account
   * @param hash qrcode hash that gets deleted from player's account
   */
  private void deleteQRCodeFromPlayerAccount(String hash) {
    PlayerDatabase.getInstance()
        .getPlayerByDeviceId(
            DeviceUtils.getDeviceUUID(getActivity()),
            results -> {
              // check if database query was successful
              if (results.isSuccessful()) {
                Player currentPlayer = results.getData();

                // remove the QR code from the player's account
                List<String> scannedqrCodelist = currentPlayer.getQRCodeHashes();
                if (scannedqrCodelist == null) {
                  scannedqrCodelist = new ArrayList<>();
                }
                scannedqrCodelist.remove(hash);
                currentPlayer.setQRCodeHashes(scannedqrCodelist);

                // update the player's account in the database
                PlayerDatabase.getInstance()
                    .update(
                        currentPlayer,
                        queryResult -> {
                          if (queryResult.isSuccessful()) {
                            Log.d(TAG, "QR code deleted from user's account.");
                            addButton.setVisibility(View.VISIBLE);
                            deleteButton.setVisibility(View.GONE);

                            // updates the qrcode document by removing players that have deleted a
                            // particular qrcode from account
                            updateQRCodeDocument(hash, currentPlayer.getDocumentId());

                          } else {
                            Log.w(
                                TAG,
                                "Error deleting QR code from account.",
                                queryResult.getException());
                            Toast.makeText(
                                    getContext(),
                                    "Error deleting QR code from account.",
                                    Toast.LENGTH_SHORT)
                                .show();
                          }
                        });
              } else {
                Log.w(TAG, "Error getting player by device ID.", results.getException());
                Toast.makeText(
                        getContext(), "Error getting player by device ID.", Toast.LENGTH_SHORT)
                    .show();
              }
            });
  }

  /**
   * Updates the QR code document in database with players who have scanned or removed a particular qr code
   * gets the qr code document if it exits and updates its players, logs a message if document doesn't exist
   * @param hash qrcode hash that has been scanned
   * @param playerId playerID of a player that has scanned/removed a particular qr code
   */
  private void updateQRCodeDocument(String hash, String playerId){
    FirebaseFirestore.getInstance()
            .collection("qrcodes")
            .document(hash)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
              if (documentSnapshot.exists()) {
                // Update the document
                FirebaseFirestore.getInstance()
                        .collection("qrcodes")
                        .document(hash)
                        .update("players", FieldValue.arrayUnion(playerId))
                        .addOnSuccessListener(aVoid -> {
                          Log.d(TAG, "Player updated in QR code document.");
                        })
                        .addOnFailureListener(e -> {
                          Log.w(TAG, "Error updating QR code document", e);
                          Toast.makeText(getContext(), "Error updating QR code document", Toast.LENGTH_SHORT).show();
                        });
              } else {
                Log.d(TAG, "QR code document does not exist.");
              }
            })
            .addOnFailureListener(e -> {
              Log.w(TAG, "Error getting QR code document", e);
              Toast.makeText(getContext(), "Error getting QR code document", Toast.LENGTH_SHORT).show();
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

  public LocationPhotoFragment getLocationPhotoFragment() {
    return locationPhotoFragment;
  }
}