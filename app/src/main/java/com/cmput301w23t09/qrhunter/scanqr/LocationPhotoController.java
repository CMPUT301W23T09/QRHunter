package com.cmput301w23t09.qrhunter.scanqr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.annotation.NonNull;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.scanqr.camera.CameraLocationPhotoController;
import java.nio.ByteBuffer;
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

  /**
   * Creates the LocationPhotoController
   *
   * @param fragment The LocationPhotoFragment that owns this LocationPhotoController
   * @param qrCode The QRCode that the user will add the location photo to
   */
  public LocationPhotoController(LocationPhotoFragment fragment, QRCode qrCode) {
    this.fragment = fragment;
    this.qrCode = qrCode;
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

            // Convert ImageProxy to Bitmap
            // Source: https://stackoverflow.com/a/41776098 (2017-01-21)
            // Author: Rod_Algonquin (https://stackoverflow.com/users/3444777/rod-algonquin)
            // License: CC BY-SA
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);

            // Rotate image so it's correct orientation
            // Source: https://stackoverflow.com/a/14645289 (2013-02-02)
            // Author: Aryan (https://stackoverflow.com/users/2032325/aryan)
            // License: CC BY-SA
            Matrix rotationMatrix = new Matrix();
            rotationMatrix.postRotate(90);
            bitmapImage =
                Bitmap.createBitmap(
                    bitmapImage,
                    0,
                    0,
                    bitmapImage.getWidth(),
                    bitmapImage.getHeight(),
                    rotationMatrix,
                    true);

            qrCode.addPhoto(bitmapImage);
            image.close();
            fragment.dismiss();
          }
        });
  }
}
