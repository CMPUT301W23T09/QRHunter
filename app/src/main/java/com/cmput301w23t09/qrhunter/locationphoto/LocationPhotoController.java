package com.cmput301w23t09.qrhunter.locationphoto;

import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.scanqr.camera.CameraLocationPhotoController;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;

/**
 * The LocationPhotoController manages the backend logic for letting a user take a location photo
 *
 * @see LocationPhotoFragment
 * @see CameraLocationPhotoController
 * @author John Mabanta
 * @version 1.0
 */
public class LocationPhotoController implements Serializable {

  private LocationPhotoFragment fragment;
  private ImageCapture imageCapture;
  private QRCode qrCode;
  private LocationPhoto locationPhoto;
  private ExecutorService cameraExecutor;
  private Player activePlayer;
  private StorageReference storageRef;
  private LocationPhotoDatabase locationPhotoDatabase;

  /**
   * Creates the LocationPhotoController
   *
   * @param fragment The LocationPhotoFragment that owns this LocationPhotoController
   * @param qrCode The QRCode that the user will add the location photo to
   * @param activePlayer The current logged in player
   */
  public LocationPhotoController(
      LocationPhotoFragment fragment, QRCode qrCode, Player activePlayer) {
    this.fragment = fragment;
    this.qrCode = qrCode;
    this.locationPhoto = null;
    this.activePlayer = activePlayer;
    this.imageCapture = null;
    this.storageRef = FirebaseStorage.getInstance().getReference();
    this.locationPhotoDatabase = LocationPhotoDatabase.getInstance();
  }

  /**
   * Once the camera use case is set, the LocationPhotoController needs to know the ImageCapture use
   * case and the CameraExecutor
   *
   * @see CameraLocationPhotoController
   * @param cameraExecutor What executes the camera service
   * @param imageCapture The ImageCapture use case that lets us take photos
   */
  public void setCameraFields(ExecutorService cameraExecutor, ImageCapture imageCapture) {
    this.cameraExecutor = cameraExecutor;
    this.imageCapture = imageCapture;
  }

  /** Takes a picture and adds it to the QRCode */
  public void takePhoto() {
    imageCapture.takePicture(
        cameraExecutor,
        new ImageCapture.OnImageCapturedCallback() {
          @Override
          public void onCaptureSuccess(@NonNull ImageProxy image) {
            super.onCaptureSuccess(image);

            locationPhoto = new LocationPhoto(image, activePlayer);
            locationPhotoDatabase.uploadPhoto(
                qrCode,
                locationPhoto,
                (isSuccessful) -> {
                  if (!isSuccessful)
                    Toast.makeText(
                        fragment.getContext(), "Image failed to upload!", Toast.LENGTH_LONG);
                  fragment.dismiss();
                });
            image.close();
          }

          @Override
          public void onError(@NonNull ImageCaptureException exception) {
            super.onError(exception);
            Log.e("ERROR", exception.getMessage());
          }
        });
  }
}
