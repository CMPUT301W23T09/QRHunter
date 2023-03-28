package com.cmput301w23t09.qrhunter.qrcode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
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
import com.cmput301w23t09.qrhunter.scanqr.LocationPhotoFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

/** Displays information about a specific QRCode. It also lets the user: */
public class QRCodeFragment extends DialogFragment implements Serializable {
  protected QRCode qrCode;
  protected ImageView locationPhoto;
  protected Button takeLocationPhotoBtn;
  protected CheckBox locationCheckbox;
  protected LocationHandler locationHandler;
  protected LocationPhotoFragment locationPhotoFragment;
  protected Player activePlayer;
  protected FloatingActionButton addButton;
  protected FloatingActionButton deleteButton;
  protected FloatingActionButton loadingButton;

  protected ListView listElement;
  protected QRCodePlayerScansAdapter scansAdapter;

  /**
   * Creates a new QRCodeFragment to display a specific QR Code
   *
   * @param qrCode The QR code to view
   * @param player The player that scanned the given QR code
   * @return QRCodeFragment
   */
  public static QRCodeFragment newInstance(QRCode qrCode, Player player) {
    Bundle args = new Bundle();
    args.putSerializable("qrcode", qrCode);
    args.putSerializable("player", player);
    QRCodeFragment fragment = new QRCodeFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_qrcode, null);
    qrCode = (QRCode) getArguments().getSerializable("qrcode");
    activePlayer = (Player) getArguments().getSerializable("player");

    try {
      setupViews(view);
    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }
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

    // get widgets in QRCodeFragment
    locationPhoto = view.findViewById(R.id.location_photo);
    locationCheckbox = view.findViewById(R.id.location_request_box);
    takeLocationPhotoBtn = view.findViewById(R.id.take_location_photo_btn);
    TextView qrName = view.findViewById(R.id.qr_name);
    TextView qrScore = view.findViewById(R.id.qr_points);
    ImageView qrCodeVisual = view.findViewById(R.id.qr_code_visual);
    addButton = view.findViewById(R.id.addButton);
    deleteButton = view.findViewById(R.id.deleteButton);
    loadingButton = view.findViewById(R.id.loadingButton);

    // fill views with qr code information
    qrName.setText(qrCode.getName());
    qrScore.setText(qrCode.getScore().toString() + " PTS");
    qrCodeVisual.setImageBitmap(qrCode.getVisualRepresentation());

    // set up buttons
    setUpButtons(view);
  }

  /** Enable and disable buttons of QRCodeFragment */
  protected void setUpButtons(View view) {
    locationPhoto.setVisibility(View.GONE);
    takeLocationPhotoBtn.setVisibility(View.GONE);
    locationCheckbox.setVisibility(View.GONE);
    addButton.setVisibility(View.GONE);
    loadingButton.setVisibility(View.VISIBLE);
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

    scansAdapter = new QRCodePlayerScansAdapter(getContext());
    setupPlayerScans();

    listElement.setAdapter(
        scansAdapter); // by default, the adapter should display the scanned players.

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

  /** Fetch all players who scanned this QR and add it to the adapter. */
  private void setupPlayerScans() {
    // For each player who scanned the QR, fetch them and the score they have.
    // Upon fetching them, add them to our adapter.
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

                // Retrieve all of the QRs the player has to get their total score.
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

                          // Calculate the player's total score
                          int score =
                              qrsTask.getData().stream()
                                  .mapToInt(QRCode::getScore)
                                  .reduce(0, Integer::sum);

                          // Add an entry into our adapter with their score and player.
                          scansAdapter.add(new QRPlayerScanEntry(player, score));
                        });
              });
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
