package com.cmput301w23t09.qrhunter.qrcode;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.player.Player;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.concurrent.ExecutionException;

/**
 * Displays information about a specific QRCode. It also lets the user:
 *
 * <ul>
 *   <li>Remove selected QR code from profile
 * </ul>
 */
public class DeleteQRCodeFragment extends QRCodeFragment {
  private QRCode qrCode;
  private Player activePlayer;
  private FloatingActionButton addButton;
  private FloatingActionButton deleteButton;
  private QRCodeDatabase qrCodeDatabase;

  /**
   * Creates a new QRCodeFragment with deletion capabilities for a specific QR Code
   *
   * @param qrCode The QR code to view
   * @param activePlayer A player that has scanned the QR code
   * @return QRCodeFragment
   */
  public static DeleteQRCodeFragment newInstance(QRCode qrCode, Player activePlayer) {
    return (DeleteQRCodeFragment) QRCodeFragment.newInstance(qrCode, activePlayer);
  }

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_qrcode, null);
    qrCode = (QRCode) getArguments().getSerializable("qrcode");
    activePlayer = (Player) getArguments().getSerializable("activePlayer");
    qrCodeDatabase = QRCodeDatabase.getInstance();
    try {
      setupViews(view);
    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }
    return createAlertDialog(view);
  }

  /**
   * Binds the UI components with the attributes of the QRCode
   *
   * @param view The view that displays fragment_qrcode.xml
   */
  @Override
  protected void setupViews(View view) throws ExecutionException, InterruptedException {
    // disable unused views
    addButton = view.findViewById(R.id.addButton);
    addButton.setVisibility(View.GONE);
    view.findViewById(R.id.location_request_box).setVisibility(View.GONE);
    view.findViewById(R.id.take_location_photo_btn).setVisibility(View.GONE);

    // show qr code information
    setUpQRCodeView(view);

    // implement the delete button
    deleteButton = view.findViewById(R.id.deleteButton);
    updateDeleteButton();
    deleteButton.setOnClickListener(
        v -> {
          qrCodeDatabase.removeQRCodeFromPlayer(activePlayer, qrCode);
          deleteButton.setVisibility(View.GONE);
        });
  }

  /** Display the remove (x) QRCode button if the player has the QR code to their name */
  private void updateDeleteButton() {
    qrCodeDatabase.playerHasQRCode(
        activePlayer,
        qrCode,
        results -> {
          if (results.isSuccessful()) {
            if (results.getData()) {
              // QR code hash is already added to the player's account
              deleteButton.setVisibility(View.VISIBLE);
            } else {
              // QR code hash is not yet added to the player's account
              deleteButton.setVisibility(View.GONE);
            }
          } else {
            Log.w("QRCodeFragment", "Error getting player by device ID.", results.getException());
            Toast.makeText(getContext(), "Error getting player by device ID.", Toast.LENGTH_SHORT)
                .show();
          }
        });
  }
}
