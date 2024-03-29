package com.cmput301w23t09.qrhunter.scanqr;

import android.annotation.SuppressLint;
import android.media.Image;
import android.util.Log;
import androidx.camera.core.ImageProxy;
import com.cmput301w23t09.qrhunter.BaseFragment;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.qrcode.AddQRCodeFragment;
import com.cmput301w23t09.qrhunter.qrcode.DeleteQRCodeFragment;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeFragment;
import com.cmput301w23t09.qrhunter.scanqr.camera.CameraController;
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
  private final BarcodeScanner scanner;
  private final BaseFragment fragment;
  private QRCodeFragment qrCodeFragment = null;
  private final Player activePlayer;
  private boolean currentlyScanning = false;

  /**
   * Creates a ScannerController
   *
   * @param fragment The fragment that the ScannerController is attached to
   * @param activePlayer The current logged in player
   */
  public ScannerController(BaseFragment fragment, Player activePlayer) {
    this.fragment = fragment;
    this.activePlayer = activePlayer;
    options = new BarcodeScannerOptions.Builder().build();
    scanner = BarcodeScanning.getClient(options);
  }

  /**
   * Scans for any QR Codes in the picture
   *
   * @param imgProxy The image to scan QR codes in, most likely from the camera
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
                  barcodes -> {
                    if (barcodes.size() > 0) {
                      Barcode scannedCode = barcodes.get(0);
                      if (currentlyScanning) return;
                      currentlyScanning = true;
                      // TODO: Draw qrCode.getBoundingBox()

                      // Only deal with the code's hash (US 08.01.01)
                      // From: https://stackoverflow.com/a/18340262 (2016-02-02)
                      // By: Jonathan (https://stackoverflow.com/users/69875/jonathan)
                      // License: CC BY-SA
                      String currentHash =
                          Hashing.sha256()
                              .hashString(scannedCode.getRawValue(), StandardCharsets.UTF_8)
                              .toString();

                      if (fragment.getGameController().getPopup() == null) {
                        // Fetch existing QR data or create new QR
                        QRCodeDatabase.getInstance()
                            .getQRCodeByHash(
                                currentHash,
                                task -> {
                                  if (task.isSuccessful()) {
                                    QRCode qrCode;
                                    if (task.getData() != null) {
                                      qrCode = task.getData();
                                    } else {
                                      qrCode = new QRCode(currentHash);
                                    }

                                    if (qrCode
                                        .getPlayers()
                                        .contains(activePlayer.getDocumentId())) {
                                      qrCodeFragment =
                                          DeleteQRCodeFragment.newInstance(qrCode, activePlayer);
                                    } else {
                                      qrCodeFragment =
                                          AddQRCodeFragment.newInstance(qrCode, activePlayer);
                                    }
                                    showQRCodeFragment();
                                  }
                                });
                      }
                    } else currentlyScanning = false;
                  })
              .addOnFailureListener(e -> Log.e("ERROR", e.toString()))
              .addOnCompleteListener(
                  task -> {
                    // Since we're using CameraX, we need to close the image proxy
                    imgProxy.close();
                  });
    }
  }

  private void showQRCodeFragment() {
    if (fragment.getGameController().getPopup() == null)
      fragment.getGameController().setPopup(qrCodeFragment);
  }
}
