package com.cmput301w23t09.qrhunter.qrcode;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.comment.Comment;
import com.cmput301w23t09.qrhunter.map.LocationHandler;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.scanqr.LocationPhotoController;
import com.cmput301w23t09.qrhunter.scanqr.LocationPhotoFragment;
import com.cmput301w23t09.qrhunter.scanqr.camera.CameraLocationPhotoController;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * Displays information about a specific QRCode. It also lets the user:
 *
 * <ul>
 *   <li>Add scanned QR code to profile
 *   <li>Record geolocation of scanned QR code
 *   <li>Take location photo of scanned qr code
 * </ul>
 */
public class AddQRCodeFragment extends QRCodeFragment {

  /**
   * Creates a new AddQRCodeFragment to display a specific QR Code
   *
   * @param qrCode The QR code to view
   * @param activePlayer The player that scanned the given QR code
   * @return QRCodeFragment
   */

  private int selectedTabIndex = 0;

  public static AddQRCodeFragment newInstance(QRCode qrCode, Player activePlayer) {
    Bundle args = new Bundle();
    args.putSerializable("qrcode", qrCode);
    args.putSerializable("player", activePlayer);
    AddQRCodeFragment fragment = new AddQRCodeFragment();
    fragment.setArguments(args);
    return fragment;
  }

  /**
   * Creates a new QRCodeFragment to display a specific QR Code with adding capabilities
   *
   * @param view
   * @return QRCodeFragment
   */
  @Override
  protected void setUpButtons(View view) {
    addButton.setVisibility(View.VISIBLE);
    deleteButton.setVisibility(View.GONE);
    loadingButton.setVisibility(View.GONE);
    locationHandler = new LocationHandler(this);
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
    updateAddButton();
    addButton.setOnClickListener(this::onAddQRClicked);
    updateLocationPhoto();

      // implementing tabs for scanned by and comments
      tabLayout.addTab(tabLayout.newTab().setText("Tab 1"));
      tabLayout.addTab(tabLayout.newTab().setText("Comments"));


      commentBox.setVisibility(View.GONE);

      if (tabLayout.getSelectedTabPosition()==1){
          listView.setVisibility(View.VISIBLE);
          //Call playerHasQRCode to check if the active player has the QR code
          qrCodeDatabase.playerHasQRCode(activePlayer, qrCode, hasQRCodeResult -> {
              if (hasQRCodeResult.getData() != null && hasQRCodeResult.getData()) {
                  commentBox.setVisibility(View.VISIBLE);}
              else{
                  commentBox.setVisibility(View.GONE);
              }
          });
      }



      commentBox.setOnClickListener(this::onAddCommentInput);
      commentBox.setOnTouchListener(this::onSendComment);


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
                                    loadingButton.setVisibility(View.GONE);
                                    this.dismiss();
                                  });
                        });

              } else {
                // QRCode already exists, add player to the QR
                QRCodeDatabase.getInstance()
                    .addPlayerToQR(
                        activePlayer,
                        qrCode,
                        ignored -> {
                          loadingButton.setVisibility(View.GONE);
                            loadingButton.setVisibility(View.GONE);
                            if (selectedTabIndex == 1) {
                                commentBox.setVisibility(View.VISIBLE);
                            } else {
                                commentBox.setVisibility(View.GONE);
                            }
                          this.dismiss();
                        });
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
  }

  /** Display the add (+) QRCode button if the player does not have the QRCode to their name */
  private void updateAddButton() {
    QRCodeDatabase.getInstance()
        .playerHasQRCode(
            activePlayer,
            qrCode,
            results -> {
              if (results.isSuccessful()) {
                if (results.getData()) {
                  // QR code hash is already added to the player's account
                  addButton.setVisibility(View.GONE);
                  DeleteQRCodeFragment.newInstance(qrCode, activePlayer)
                      .show(getParentFragmentManager(), "Switch to delete QR code fragment");
                  this.dismiss();
                } else {
                  // QR code hash is not yet added to the player's account
                  // Thus, display add button
                  addButton.setVisibility(View.VISIBLE);
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

    public void onAddCommentInput(View view){
        commentBox.setHint(""); // Remove hint text when clicked
        //commentBox.setHeight(getResources().getDimensionPixelSize(R.dimen.comment_box_expanded_height));
    }

    private boolean onSendComment(View view, MotionEvent event) {
        final int DRAWABLE_RIGHT = 2;
        if(event.getAction() == MotionEvent.ACTION_UP) {
            if(event.getRawX() >= (commentBox.getRight() - commentBox.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
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

        Comment comment = new Comment(commentText, activePlayer);
        qrCodeDatabase.getQRCodeByHash(qrCode.getHash(), qrCodeQueryResults -> {
            if (qrCodeQueryResults.isSuccessful()) {
                // Update the QRCode with the new comment
                QRCode qrCodeToUpdate = qrCodeQueryResults.getData();
                qrCodeToUpdate.addComment(comment);

                qrCodeDatabase.updateQRCode(qrCodeToUpdate, updateResults -> {
                    if (updateResults.isSuccessful()) {
                        Log.d(TAG, "Comment added to QRCode with hash: " + qrCodeToUpdate.getHash());
                    } else {
                        Log.w(TAG, "Error updating QRCode with new comment", updateResults.getException());
                    }
                });
            } else {
                Log.w(TAG, "Error getting QRCode from database", qrCodeQueryResults.getException());
            }
        });

    }


}
