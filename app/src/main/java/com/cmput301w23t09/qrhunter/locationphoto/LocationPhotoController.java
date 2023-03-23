package com.cmput301w23t09.qrhunter.locationphoto;

import android.graphics.Bitmap;
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
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
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
            uploadPhoto();
            image.close();
            fragment.dismiss();
          }

          @Override
          public void onError(@NonNull ImageCaptureException exception) {
            super.onError(exception);
            Log.e("ERROR", exception.getMessage());
          }
        });
  }

  /**
   * Uploads locationPhoto to Firebase Cloud Storage The format of the URL is
   * `QRCodeHash/PlayerDocId`
   *
   * <p>Adapted from https://firebase.google.com/docs/storage/android/upload-files License: Apache
   * 2.0
   */
  public void uploadPhoto() {
    ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
    // Use 25 JPEG quality to keep image sizes low (US 09.01.01)
    locationPhoto.getPhoto().compress(Bitmap.CompressFormat.JPEG, 50, byteOutput);
    byte[] data = byteOutput.toByteArray();
    StorageReference locationPhotoRef =
        storageRef.child(qrCode.getHash() + "/" + activePlayer.getDocumentId() + ".jpg");
    UploadTask uploadTask = locationPhotoRef.putBytes(data);
    uploadTask.addOnFailureListener(
        e -> {
          Toast.makeText(
              fragment.getContext(), "Location image failed to upload!", Toast.LENGTH_LONG);
          Log.e("LocationPhotoController", e.getMessage());
        });
  }

  public void deletePhoto() {
    StorageReference locationPhotoRef =
        storageRef.child(qrCode.getHash() + "/" + activePlayer.getDocumentId() + ".jpg");
    locationPhotoRef
        .delete()
        .addOnFailureListener(
            e -> {
              if (((StorageException) e).getErrorCode()
                  != StorageException.ERROR_OBJECT_NOT_FOUND) {
                Toast.makeText(
                    fragment.getContext(), "Location image failed to delete!", Toast.LENGTH_LONG);
                Log.e("LocationPhotoController", e.getMessage());
              }
            })
        .addOnSuccessListener(unused -> locationPhoto = null);
  }
}
