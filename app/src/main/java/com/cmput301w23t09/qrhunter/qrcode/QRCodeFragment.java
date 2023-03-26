package com.cmput301w23t09.qrhunter.qrcode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Displays information about a specific QRCode. It also lets the user:
 *
 * <ul>
 *   <li>Add scanned QR code to profile
 *   <li>Remove selected QR code from profile
 *   <li>Record geolocation of scanned QR code
 *   <li>Take location photo of scanned qr code
 * </ul>
 *
 * @author John Mabanta
 * @version 1.0
 */
public class QRCodeFragment extends DialogFragment implements Serializable {

  private ImageView locationPhoto;
  private QRCode qrCode;
  private Button takeLocationPhotoBtn;
  private CheckBox locationCheckbox;
  private LocationHandler locationHandler;
  private LocationPhotoFragment locationPhotoFragment;
  private Player activePlayer;
  private FloatingActionButton addButton;
  private FloatingActionButton deleteButton;

  private ListView listElement;
  private QRCodePlayerScansAdapter scansAdapter;
  private List<QRCodePlayerScansAdapter.Entry> playersWhoScanned;

  private FloatingActionButton loadingButton;

  /**
   * Creates a new QRCodeFragment to display a specific QR Code
   *
   * @param qrCode The QR code to view
   * @param activePlayer The currently active/logged-in player
   * @return QRCodeFragment
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
    try {
      setupViews(view);
    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }
    updateLocationPhoto();
    return createAlertDialog(view);
  }

  @Override
  public void onResume() {
    super.onResume();

    // Adjust QRFragment window size to match content.
    Window window = getDialog().getWindow();
    window.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    window.setGravity(Gravity.CENTER);
  }

  /**
   * Binds the UI components with the attributes of the QRCode
   *
   * @param view The view that displays fragment_qrcode.xml
   */
  private void setupViews(View view) throws ExecutionException, InterruptedException {
    setupTab(view);

    locationPhoto = view.findViewById(R.id.location_photo);
    locationCheckbox = view.findViewById(R.id.location_request_box);

    TextView qrName = view.findViewById(R.id.qr_name);
    qrName.setText(qrCode.getName());

    TextView qrScore = view.findViewById(R.id.qr_points);
    qrScore.setText(qrCode.getScore().toString() + " PTS");

    ImageView qrCodeVisual = view.findViewById(R.id.qr_code_visual);
    qrCodeVisual.setImageBitmap(qrCode.getVisualRepresentation());

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

    addButton = view.findViewById(R.id.addButton);
    deleteButton = view.findViewById(R.id.deleteButton);
    loadingButton = view.findViewById(R.id.loadingButton);

    updateAddDeleteButton();

    // implementing the add/delete button listeners
    addButton.setOnClickListener(this::onAddQRClicked);
    deleteButton.setOnClickListener(this::onRemoveQRClicked);
  }

  /**
   * Called when the add QR button is clicked
   *
   * @param view view
   */
  private void onAddQRClicked(View view) {
    addButton.setVisibility(View.GONE);
    loadingButton.setVisibility(View.VISIBLE);

    // Add QR to database, when the QR has been added, allow the deletion of the QRCode.
    // First check if the qr code exists.
    QRCodeDatabase.getInstance()
        .getQRCodeByHash(
            qrCode.getHash(),
            qrCodeHash -> {
              if (qrCodeHash.getException() != null) {
                addButton.setVisibility(View.VISIBLE);
                loadingButton.setVisibility(View.GONE);
                return;
              }

              // If it doesn't exist, add the QR
              if (qrCodeHash.getData() == null) {
                QRCodeDatabase.getInstance()
                    .addQRCode(
                        qrCode,
                        task -> {
                          if (!task.isSuccessful()) {
                            addButton.setVisibility(View.VISIBLE);
                            loadingButton.setVisibility(View.GONE);
                            return;
                          }

                          // Add the player to the QR
                          QRCodeDatabase.getInstance()
                              .addPlayerToQR(
                                  activePlayer,
                                  qrCode,
                                  ignored -> {
                                    deleteButton.setVisibility(View.VISIBLE);
                                    loadingButton.setVisibility(View.GONE);
                                  });
                        });

              } else {
                // QRCode already exists, add player to the QR
                QRCodeDatabase.getInstance()
                    .addPlayerToQR(
                        activePlayer,
                        qrCode,
                        ignored -> {
                          deleteButton.setVisibility(View.VISIBLE);
                          loadingButton.setVisibility(View.GONE);
                        });
              }
            });
  }

  /**
   * Called when the remove QR button is clicked
   *
   * @param view view
   */
  private void onRemoveQRClicked(View view) {
    deleteButton.setVisibility(View.GONE);
    loadingButton.setVisibility(View.VISIBLE);

    // Remove QR from player
    QRCodeDatabase.getInstance()
        .removeQRCodeFromPlayer(
            activePlayer,
            qrCode,
            ignored2 -> {
              addButton.setVisibility(View.VISIBLE);
              loadingButton.setVisibility(View.GONE);
            });
  }

  /**
   * Sets up the tab related items and listeners for the qr fragment.
   *
   * @param view dialog
   */
  private void setupTab(View view) {
    TabLayout layout = view.findViewById(R.id.qr_nav);
    layout.addTab(layout.newTab().setText(getText(R.string.players_who_scanned_tab_title)));
    layout.addTab(layout.newTab().setText(getText(R.string.comments_tab_title)));

    listElement = view.findViewById(R.id.qr_nav_items);
    setupPlayerScans();
    layout.addOnTabSelectedListener(
        new TabLayout.OnTabSelectedListener() {
          @Override
          public void onTabSelected(TabLayout.Tab tab) {
            if (tab.getText().equals(getText(R.string.players_who_scanned_tab_title))) {
              // Who scanned the QR
              listElement.setAdapter(scansAdapter);
            } else {
              // Comments
            }
          }

          @Override
          public void onTabUnselected(TabLayout.Tab tab) {}

          @Override
          public void onTabReselected(TabLayout.Tab tab) {}
        });
  }

  private void setupPlayerScans() {
    playersWhoScanned = new ArrayList<>();
    scansAdapter = new QRCodePlayerScansAdapter(getContext(), playersWhoScanned);

    for (String documentId : qrCode.getPlayers()) {
      PlayerDatabase.getInstance()
          .getPlayerByDocumentId(
              documentId,
              task -> {
                if (!task.isSuccessful()) {
                  Toast.makeText(
                          getContext(),
                          "An exception occurred while fetching the players who scanned this QR...",
                          Toast.LENGTH_LONG)
                      .show();
                  return;
                }
                Player player = task.getData();

                QRCodeDatabase.getInstance()
                    .getQRCodeHashes(
                        player.getQRCodeHashes(),
                        qrsTask -> {
                          if (!qrsTask.isSuccessful()) {
                            Toast.makeText(
                                    getContext(),
                                    "An exception occurred while fetching the QRs of a player who scanned this QR...",
                                    Toast.LENGTH_LONG)
                                .show();
                            return;
                          }

                          int score =
                              qrsTask.getData().stream()
                                  .mapToInt(QRCode::getScore)
                                  .reduce(0, Integer::sum);

                          playersWhoScanned.add(new QRCodePlayerScansAdapter.Entry(player, score));
                        });
              });
    }
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

  /**
   * Display the add (+) QRCode button if the player does not have the QRCode to their name, else
   * display the remove (x) QRCode button if the player has it.
   */
  private void updateAddDeleteButton() {
    QRCodeDatabase.getInstance()
        .playerHasQRCode(
            activePlayer,
            qrCode,
            results -> {
              if (results.isSuccessful()) {
                if (results.getData()) {
                  // QR code hash is already added to the player's account
                  // Thus, display delete button
                  addButton.setVisibility(View.GONE);
                  deleteButton.setVisibility(View.VISIBLE);
                } else {
                  // QR code hash is not yet added to the player's account
                  // Thus, display add button
                  addButton.setVisibility(View.VISIBLE);
                  deleteButton.setVisibility(View.GONE);
                }
              } else {
                Log.w(
                    "QRCodeFragment", "Error getting player by device ID.", results.getException());
                Toast.makeText(
                        getContext(), "Error getting player by device ID.", Toast.LENGTH_SHORT)
                    .show();
              }
            });
  }

  /**
   * @return The location photo fragment
   */
  public LocationPhotoFragment getLocationPhotoFragment() {
    return locationPhotoFragment;
  }
}
