package com.cmput301w23t09.qrhunter.locationphoto;

import android.graphics.Bitmap;
import android.util.Log;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.function.Consumer;

public class LocationPhotoStorage {
  private static final String DEBUG_TAG = "LocationPhotoStorage";

  private static LocationPhotoStorage INSTANCE;

  public LocationPhotoStorage() {}

  public String getPrefix() {
    return "";
  }

  /**
   * @return Retrieves the LocationPhotoStorage
   */
  public static LocationPhotoStorage getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new LocationPhotoStorage();
    }
    return INSTANCE;
  }

  public static void mockInstance(LocationPhotoStorage mockInstance) {
    INSTANCE = mockInstance;
  }

  /**
   * Retrieves all the location photos (references) for a given QRCode
   *
   * @param qrCode The QRCode to fetch location photos for
   * @param callback The callback function that handles the returned location photos
   */
  public void getLocationPhotos(QRCode qrCode, Consumer<ArrayList<StorageReference>> callback) {
    ArrayList<StorageReference> locationPhotoRefs = new ArrayList<>();
    getQRCodeRef(qrCode)
        .listAll()
        .addOnSuccessListener(
            listResult -> {
              for (StorageReference photo : listResult.getItems()) locationPhotoRefs.add(photo);
              callback.accept(locationPhotoRefs);
            })
        .addOnFailureListener(e -> callback.accept(null));
  }

  /**
   * Uploads a location photo
   *
   * <p>The format of the photo's URL is `[QRCodeHash]/[PlayerDocId]`
   *
   * <p>Adapted from https://firebase.google.com/docs/storage/android/upload-files
   *
   * <p>License: Apache 2.0
   *
   * @param qrCode The QRCode associated with the location photo
   * @param locationPhoto The location photo to upload
   * @param callback The callback function that handles result of upload
   */
  public void uploadPhoto(QRCode qrCode, LocationPhoto locationPhoto, Consumer<Boolean> callback) {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    // Resize to a maximum of 600x800, preserving aspect ratio
    Bitmap resizedPhoto = resizeBitmap(locationPhoto.getPhoto(), 600, 800);
    // Use 25 JPEG quality to keep image sizes low (US 09.01.01)
    resizedPhoto.compress(Bitmap.CompressFormat.JPEG, 25, output);
    byte[] data = output.toByteArray();
    StorageReference locationPhotoRef =
        getQRCodeRef(qrCode).child(locationPhoto.getPlayer().getDocumentId() + ".jpg");
    UploadTask uploadTask = locationPhotoRef.putBytes(data);
    uploadTask
        .addOnSuccessListener(unused -> callback.accept(true))
        .addOnFailureListener(
            e -> {
              Log.e(DEBUG_TAG, e.getMessage());
              callback.accept(false);
            });
  }

  /**
   * Deletes a player's location photo from the QRCode
   *
   * @param qrCode The QRCode to delete location photo from
   * @param player The Player who'd like to delete their location photo
   * @param callback The callback function that handles result of deletion
   */
  public void deletePhoto(QRCode qrCode, Player player, Consumer<Boolean> callback) {
    StorageReference locationPhotoRef = getQRCodeRef(qrCode).child(player.getDocumentId() + ".jpg");
    locationPhotoRef
        .delete()
        .addOnSuccessListener(unused -> callback.accept(true))
        .addOnFailureListener(
            e -> {
              Log.e(DEBUG_TAG, e.getMessage());
              callback.accept(false);
            });
  }
  ;

  /**
   * Checks if a player has a location photo uploadedt
   *
   * @param qrCode The QRCode we check if player has a location photo of
   * @param player The player of interest
   * @param callback The callback function to handle the result of the query
   */
  public void playerHasLocationPhoto(QRCode qrCode, Player player, Consumer<Boolean> callback) {
    StorageReference locationPhotoRef = getQRCodeRef(qrCode).child(player.getDocumentId() + ".jpg");
    locationPhotoRef
        .getDownloadUrl() // Checks if file actually exists in cloud storage
        .addOnSuccessListener(unused -> callback.accept(true))
        .addOnFailureListener(unused -> callback.accept(false));
  }

  /**
   * Gets the reference for the folder containing all the location photos of the QRCode
   *
   * @param qrCode The QRCode to fetch location photos of
   * @return A StorageReference pointing to the folder of the QRCode's location photos
   */
  public StorageReference getQRCodeRef(QRCode qrCode) {
    if (qrCode != null) // Sometimes qrCode is null when run in android test
    return FirebaseStorage.getInstance().getReference().child(getPrefix() + qrCode.getHash() + "/");
    return null;
  }

  /**
   * Resizes the image bitmap, preserving aspect ratio.
   *
   * <p>Source: https://stackoverflow.com/a/28367226 By: joaomgcd
   * (https://stackoverflow.com/users/1002963/joaomgcd) (2015-02-06) Edited By: Abel Hamilton
   * (https://stackoverflow.com/users/7396999/abel-hamilton) (2017-10-22) License: CC BY-SA
   *
   * @param image The bitmap to resize
   * @param maxWidth The maximum width it can be resized to.
   * @param maxHeight The maximum height it can be resized to.
   * @return The resized bitmap
   */
  private Bitmap resizeBitmap(Bitmap image, int maxWidth, int maxHeight) {
    if (maxHeight > 0 && maxWidth > 0) {
      int width = image.getWidth();
      int height = image.getHeight();
      float ratioBitmap = (float) width / (float) height;
      float ratioMax = (float) maxWidth / (float) maxHeight;

      int finalWidth = maxWidth;
      int finalHeight = maxHeight;
      if (ratioMax > ratioBitmap) {
        finalWidth = (int) ((float) maxHeight * ratioBitmap);
      } else {
        finalHeight = (int) ((float) maxWidth / ratioBitmap);
      }
      image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
      return image;
    } else {
      return image;
    }
  }
}
