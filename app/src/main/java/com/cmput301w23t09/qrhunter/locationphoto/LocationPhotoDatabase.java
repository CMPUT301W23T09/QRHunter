package com.cmput301w23t09.qrhunter.locationphoto;

import android.graphics.Bitmap;
import android.util.Log;
import androidx.annotation.NonNull;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.function.Consumer;

public class LocationPhotoDatabase {
  private final String PREFIX;
  private static final String DEBUG_TAG = "LocationPhotoDatabase";

  private static LocationPhotoDatabase INSTANCE;

  /**
   * Creates a LocationPhotoDatabase without a prefix to the QRCode's folder Should be used in
   * production
   */
  protected LocationPhotoDatabase() {
    PREFIX = "";
  }

  public static LocationPhotoDatabase getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new LocationPhotoDatabase();
    }
    return INSTANCE;
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
            new OnSuccessListener<ListResult>() {
              @Override
              public void onSuccess(ListResult listResult) {
                for (StorageReference photo : listResult.getItems()) locationPhotoRefs.add(photo);
                callback.accept(locationPhotoRefs);
              }
            })
        .addOnFailureListener(
            new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                callback.accept(null);
              }
            });
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
   */
  public void uploadPhoto(QRCode qrCode, LocationPhoto locationPhoto) {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    // Use 50 JPEG quality to keep image sizes low (US 09.01.01)
    locationPhoto.getPhoto().compress(Bitmap.CompressFormat.JPEG, 50, output);
    byte[] data = output.toByteArray();
    StorageReference locationPhotoRef =
        getQRCodeRef(qrCode).child(locationPhoto.getPlayer().getDocumentId() + ".jpg");
    UploadTask uploadTask = locationPhotoRef.putBytes(data);
    uploadTask.addOnFailureListener(
        e -> {
          Log.e(DEBUG_TAG, e.getMessage());
        });
  }

  /**
   * Deletes a player's location photo from the QRCode
   *
   * @param qrCode The QRCode to delete location photo from
   * @param player The Player who'd like to delete their location photo
   */
  public void deletePhoto(QRCode qrCode, Player player) {
    StorageReference locationPhotoRef = getQRCodeRef(qrCode).child(player.getDocumentId() + ".jpg");
    locationPhotoRef
        .delete()
        .addOnFailureListener(
            e -> {
              Log.e(DEBUG_TAG, e.getMessage());
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
  private StorageReference getQRCodeRef(QRCode qrCode) {
    return FirebaseStorage.getInstance().getReference().child(PREFIX + qrCode.getHash() + "/");
  }
}
