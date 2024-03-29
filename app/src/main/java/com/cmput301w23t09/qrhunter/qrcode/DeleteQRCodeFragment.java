package com.cmput301w23t09.qrhunter.qrcode;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.cmput301w23t09.qrhunter.player.Player;

/**
 * Displays information about a specific QRCode. It also lets the user:
 *
 * <ul>
 *   <li>Remove selected QR code from profile
 * </ul>
 */
public class DeleteQRCodeFragment extends QRCodeFragment {
  /**
   * Creates a new DeleteQRCodeFragment to display a specific QR Code
   *
   * @param qrCode The QR code to view
   * @param activePlayer The player that scanned the given QR code
   * @return QRCodeFragment
   */
  public static DeleteQRCodeFragment newInstance(QRCode qrCode, Player activePlayer) {
    Bundle args = new Bundle();
    args.putSerializable("qrcode", qrCode);
    args.putSerializable("player", activePlayer);
    DeleteQRCodeFragment fragment = new DeleteQRCodeFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  protected void setUpButtons(View view) {
    deleteButton.setVisibility(View.VISIBLE);
    addButton.setVisibility(View.GONE);
    takeLocationPhotoBtn.setVisibility(View.GONE);
    locationCheckbox.setVisibility(View.GONE);
    loadingButton.setVisibility(View.GONE);
    updateDeleteButton();
    deleteButton.setOnClickListener(this::onRemoveQRClicked);
  }

  @Override
  protected void toggleCommentBox(boolean isShown) {
    if (isShown) commentBox.setVisibility(View.VISIBLE);
    else commentBox.setVisibility(View.GONE);
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
              loadingButton.setVisibility(View.GONE);
              this.dismiss();
            });
  }

  /** Display the remove (x) QRCode button if the player has the QR code to their name. */
  private void updateDeleteButton() {
    QRCodeDatabase.getInstance()
        .playerHasQRCode(
            activePlayer,
            qrCode,
            results -> {
              if (results.isSuccessful()) {
                if (results.getData()) {
                  // QR code hash is already added to the player's account
                  // Thus, display delete button
                  deleteButton.setVisibility(View.VISIBLE);
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
}
