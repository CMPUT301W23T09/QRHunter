package com.cmput301w23t09.qrhunter.scanqr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;

public class LocationPhotoController {

  private LocationPhotoFragment fragment;
  private ImageCapture imageCapture;
  private QRCode qrCode;
  private ExecutorService cameraExecutor;

  public LocationPhotoController(LocationPhotoFragment fragment) {
    this.fragment = fragment;
    // this.qrCode = qrCode;
    this.imageCapture = null;
  }

  public void setImageCapture(ImageCapture imageCapture) {
    this.imageCapture = imageCapture;
  }

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

            qrCode.addPhoto(bitmapImage);
          }
        });
  }
}
