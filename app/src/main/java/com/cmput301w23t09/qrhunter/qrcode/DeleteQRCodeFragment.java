package com.cmput301w23t09.qrhunter.qrcode;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.comment.Comment;
import com.cmput301w23t09.qrhunter.player.Player;
import com.google.android.material.tabs.TabLayout;

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

    commentBox.setVisibility(View.GONE);
    tabLayout.addOnTabSelectedListener(
        new TabLayout.OnTabSelectedListener() {
          @Override
          public void onTabSelected(TabLayout.Tab tab) {
            if (tab.getPosition() == 1) {
              commentBox.setVisibility(View.VISIBLE);
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

    // allows user to enter a comment
    commentBox.setOnClickListener(this::onAddCommentInput);

    // allows user to click on send icon directly to store comment in the database
    commentBox.setOnTouchListener((v, event) -> onSendComment(v, event, null));
    updateLocationPhoto();

    //handles Enter key press to store comment in the database
    commentBox.setOnKeyListener((v, keyCode, event) -> {
      if (keyCode == KeyEvent.KEYCODE_ENTER) {
          return onSendComment(v, null, event);
      }
      return false;
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

  /**
   * Removes hint text when clicked*
   *
   * @param view view
   */
  public void onAddCommentInput(View view) {
    commentBox.setHint("");
  }

  /**
   * Handles sending a comment when the send icon is clicked. Changes the color of the send icon
   * depending on the state.
   *
   * @param view View
   * @param event The motion event
   * @return True if the event is handled. otherwise, false.
   */
  private boolean onSendComment(View view, MotionEvent event, KeyEvent keyEvent) {
      final int DRAWABLE_RIGHT = 2;

      // changes color of send icon depending on the state
      int default_color = ContextCompat.getColor(requireContext(), R.color.purple_500);
      int on_send_color = ContextCompat.getColor(requireContext(), R.color.purple_200);

      if (event != null && event.getAction() == MotionEvent.ACTION_DOWN) {
          if (event.getRawX() >= (commentBox.getRight() - commentBox.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
              commentBox.getCompoundDrawables()[DRAWABLE_RIGHT].setTint(on_send_color);
              return true;
          }
      } else if (event != null && event.getAction() == MotionEvent.ACTION_UP) {
          if (event.getRawX() >= (commentBox.getRight() - commentBox.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
              commentBox.getCompoundDrawables()[DRAWABLE_RIGHT].setTint(default_color);

              // stores comment message in a variable and calls the addComment method
              String commentText = commentBox.getText().toString().trim();
              if (!commentText.isEmpty()) {
                  addComment(commentText);
                  commentBox.setText("");
                  commentBox.setHint(R.string.comment_box_hint_text);
              } else {
                  // Comment text is empty, show an error message
                  Toast.makeText(getContext(), "Comment text is empty", Toast.LENGTH_SHORT).show();
              }
              return true;
          }
      } else if (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
          // handles the enter key press
          String commentText = commentBox.getText().toString().trim();
          if (!commentText.isEmpty()) {
              addComment(commentText);
              commentBox.setText("");
              commentBox.setHint(R.string.comment_box_hint_text);
          } else {
              // Comment text is empty, show an error message
              Toast.makeText(getContext(), "Comment text is empty", Toast.LENGTH_SHORT).show();
          }
          return true;
      }
      return false;
  }


    /**
   * Adds a comment to the comments list updates the comments adapter saves the comment to the
   * database.
   *
   * @param commentText The comment text to be added to database
   */
  private void addComment(String commentText) {
    Comment comment =
        new Comment(activePlayer.getDocumentId(), activePlayer.getUsername(), commentText);
    comments.add(comment);
    commentsAdapter.notifyDataSetChanged();
    listElement.setSelection(commentsAdapter.getCount() - 1);

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
