package com.cmput301w23t09.qrhunter.scanqr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;

/**
 * The LocationPhotoController manages the backend logic for letting a user take a location photo
 *
 * @see LocationPhotoFragment
 * @see CameraController
 * @author John Mabanta
 * @version 1.0
 */
public class LocationPhotoController {

  private LocationPhotoFragment fragment;
  private ImageCapture imageCapture;
  private ExecutorService cameraExecutor;
  private QRCode qrCode;

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

    CameraController cameraController = CameraController.getInstance();
    imageCapture = cameraController.getImageCapture();
    cameraExecutor = cameraController.getCameraExecutor();
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
            // fragment.dismiss();
          }

          @Override
          public void onError(@NonNull ImageCaptureException exception) {
            super.onError(exception);
            Log.e("ERROR", exception.getMessage());
          }
        });
  }
}
