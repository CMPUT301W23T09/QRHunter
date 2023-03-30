package com.cmput301w23t09.qrhunter.qrcode;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.comment.Comment;
import com.cmput301w23t09.qrhunter.player.Player;
import com.google.android.material.tabs.TabLayout;

import org.checkerframework.common.returnsreceiver.qual.This;

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
    showLocationPhoto();

    commentBox.setVisibility(View.GONE);
    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getPosition() == 1) {
          // Call playerHasQRCode to check if the active player has the QR code
          QRCodeDatabase.getInstance()
                  .playerHasQRCode(
                          activePlayer,
                          qrCode,
                          hasQRCodeResult -> {
                            if (hasQRCodeResult.getData() != null && hasQRCodeResult.getData()) {
                              // show comment box only when the active player has a QR code
                              commentBox.setVisibility(View.VISIBLE);
                            } else {
                              // hide comment box when the active player does not have a QR code
                              commentBox.setVisibility(View.GONE);
                            }
                          });

        } else {
          // Hide the comment box
          commentBox.setVisibility(View.GONE);
        }
      }


      @Override
      public void onTabUnselected(TabLayout.Tab tab) {}

      @Override
      public void onTabReselected(TabLayout.Tab tab) {}
    });

    // allows user enter a comment
    commentBox.setOnClickListener(this::onAddCommentInput);
    // allows user to click send and store comment in the database
    commentBox.setOnTouchListener(this::onSendComment);


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

  /** Show the location photo of the qr code */
  public void showLocationPhoto() {
    if (qrCode.getPhotos() != null && qrCode.getPhotos().size() > 0) {
      locationPhoto.setImageBitmap(qrCode.getPhotos().get(0).getPhoto());
    } else {
      locationPhoto.setImageResource(android.R.color.transparent);
    }
  }

  public void onAddCommentInput(View view) {
    commentBox.setHint("");
  } // Remove hint text when clicked

  private boolean onSendComment(View view, MotionEvent event) {
    final int DRAWABLE_RIGHT = 2;
    if (event.getAction() == MotionEvent.ACTION_UP) {
      if (event.getRawX()
              >= (commentBox.getRight()
              - commentBox.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
        // User clicked on the drawable icon
        String commentText = commentBox.getText().toString().trim();
        if (!commentText.isEmpty()) {
          saveCommentToDatabase(commentText);
          commentBox.setText("");
          commentBox.setHint(R.string.comment_box_hint_text);
        } else {
          // Comment text is empty, show an error message
          Toast.makeText(getContext(), "Comment text is empty", Toast.LENGTH_SHORT).show();
        }
        return true;
      }
    }
    return false;
  }

  private void saveCommentToDatabase(String commentText) {
    // Retrieve the active player object from the arguments bundle
    Player activePlayer = (Player) getArguments().getSerializable("player");

    Comment comment = new Comment(commentText, activePlayer, activePlayer.getDocumentId());
    QRCodeDatabase.getInstance()
            .getQRCodeByHash(
                    qrCode.getHash(),
                    qrCodeQueryResults -> {
                      if (qrCodeQueryResults.isSuccessful()) {
                        // Update the QRCode with the new comment
                        QRCode qrCodeToUpdate = qrCodeQueryResults.getData();
                        qrCodeToUpdate.addComment(comment);
                        QRCodeDatabase.getInstance()
                                .updateQRCode(
                                        qrCodeToUpdate,
                                        updateResults -> {
                                          if (updateResults.isSuccessful()) {
                                            Log.d(
                                                    TAG,
                                                    "Comment added to QRCode with hash: " + qrCodeToUpdate.getHash());
                                          } else {
                                            Log.w(
                                                    TAG,
                                                    "Error updating QRCode with new comment",
                                                    updateResults.getException());
                                          }
                                        });
                      } else {
                        Log.w(TAG, "Error getting QRCode from database", qrCodeQueryResults.getException());
                      }
                    });
    Log.d("AddQRCodeFragment", "Comment saved to database");
  }
}
