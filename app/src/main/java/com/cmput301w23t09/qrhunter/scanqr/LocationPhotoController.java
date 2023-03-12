package com.cmput301w23t09.qrhunter.scanqr;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import com.cmput301w23t09.qrhunter.photo.Photo;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.scanqr.camera.CameraLocationPhotoController;
import java.util.concurrent.ExecutorService;

/**
 * The LocationPhotoController manages the backend logic for letting a user take a location photo
 *
 * @see LocationPhotoFragment
 * @see CameraLocationPhotoController
 * @author John Mabanta
 * @version 1.0
 */
public class LocationPhotoController {

  private LocationPhotoFragment fragment;
  private ImageCapture imageCapture;
  private QRCode qrCode;
  private ExecutorService cameraExecutor;
  private Player activePlayer;

  /**
   * Creates the LocationPhotoController
   *
   * @param fragment The LocationPhotoFragment that owns this LocationPhotoController
   * @param qrCode The QRCode that the user will add the location photo to
   */
  public LocationPhotoController(
      LocationPhotoFragment fragment, QRCode qrCode, Player activePlayer) {
    this.fragment = fragment;
    this.qrCode = qrCode;
    this.activePlayer = activePlayer;
    this.imageCapture = null;
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

            qrCode.addPhoto(new Photo(image, activePlayer));
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
}
