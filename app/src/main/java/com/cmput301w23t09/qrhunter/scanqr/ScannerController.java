package com.cmput301w23t09.qrhunter.scanqr;

import android.annotation.SuppressLint;
import android.media.Image;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.camera.core.ImageProxy;
import com.cmput301w23t09.qrhunter.BaseFragment;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.hash.Hashing;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Controls QR Code scanning using Google's MLKit Library
 * (https://developers.google.com/ml-kit/vision/barcode-scanning/android)
 *
 * @author John Mabanta
 * @version 1.0
 */
public class ScannerController {

  private BarcodeScannerOptions options;
  private BarcodeScanner scanner;
  private BaseFragment fragment;
  private QRCodeFragment qrCodeFragment = null;
  private String pastHash = "";

  /**
   * Creates a ScannerController
   * @param fragment The fragment that the ScannerController is attached to
   */
  public ScannerController(BaseFragment fragment) {
    this.fragment = fragment;
    options =
        new BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE // Only want to scan QR codes
                )
            .build();
    scanner = BarcodeScanning.getClient(options);
  }

  /**
   * Scans for any QR Codes in the picture.
   *
   * @param imgProxy The image to scan QR codes in, most likely from the camera.
   * @see CameraController
   */
  public void scanCode(ImageProxy imgProxy) {
    @SuppressLint("UnsafeOptInUsageError")
    Image mediaImage = imgProxy.getImage();
    if (mediaImage != null) {
      InputImage img =
          InputImage.fromMediaImage(mediaImage, imgProxy.getImageInfo().getRotationDegrees());
      Task<List<Barcode>> result =
          scanner
              .process(img)
              .addOnSuccessListener(
                  new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                      if (barcodes.size() > 0) {
                        Barcode qrCode = barcodes.get(0);
                        // TODO: Draw qrCode.getBoundingBox()

                        // Only deal with the code's hash (US 08.01.01)
                        // From: https://stackoverflow.com/a/18340262 (2016-02-02)
                        // By: Jonathan (https://stackoverflow.com/users/69875/jonathan)
                        // License: CC BY-SA
                        String currentHash =
                            Hashing.sha256()
                                .hashString(qrCode.getRawValue(), StandardCharsets.UTF_8)
                                .toString();

                        if ((qrCodeFragment == null || !qrCodeFragment.isAdded())
                            && !pastHash.equals(currentHash)) {
                          pastHash = currentHash;
                          if (qrCodeFragment != null) qrCodeFragment.dismissNow();
                          qrCodeFragment = QRCodeFragment.newInstance(currentHash);
                          qrCodeFragment.show(
                              fragment.getParentFragmentManager(), "Scanned QR Code");
                        }
                        // TODO: Hash QR code and create QR Object from it!
                        // TODO: Decide how to handle multiple codes on screen at once
                      }
                    }
                  })
              .addOnFailureListener(
                  new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      Log.e("ERROR", e.toString());
                    }
                  })
              .addOnCompleteListener(
                  new OnCompleteListener<List<Barcode>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Barcode>> task) {
                      // Since we're using CameraX, we need to close the image proxy
                      imgProxy.close();
                    }
                  });
    }
  }
}
