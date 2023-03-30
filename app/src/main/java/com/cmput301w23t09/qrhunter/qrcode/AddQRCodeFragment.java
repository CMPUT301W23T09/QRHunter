package com.cmput301w23t09.qrhunter.qrcode;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.map.LocationHandler;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.scanqr.LocationPhotoController;
import com.cmput301w23t09.qrhunter.scanqr.LocationPhotoFragment;
import com.cmput301w23t09.qrhunter.scanqr.camera.CameraLocationPhotoController;

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

    //hides comment box
    commentBox.setVisibility(View.GONE);

    updateAddButton();
    addButton.setOnClickListener(this::onAddQRClicked);
    updateLocationPhoto();
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
                        });

              } else {
                // QRCode already exists, add player to the QR
                QRCodeDatabase.getInstance()
                    .addPlayerToQR(
                        activePlayer,
                        qrCode,
                        ignored -> {
                          loadingButton.setVisibility(View.GONE);

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
}
