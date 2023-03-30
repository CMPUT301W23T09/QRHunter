package com.cmput301w23t09.qrhunter.locationphoto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.camera.core.ImageProxy;
import com.cmput301w23t09.qrhunter.player.Player;
import java.nio.ByteBuffer;

/**
 * Stores the location photo that a player took of a QRCode
 *
 * @author Irene Sun
 * @author John Mabanta
 * @version 1.1
 */
public class LocationPhoto {
  /** Stores image */
  private Bitmap photo;
  /** This is the player that took the photo */
  private Player player;

  /**
   * This initializes a Photo with an image of a QRCode and the player that took it
   *
   * @param bitmap The location image of the QRCode, as a Bitmap type
   * @param player This is the player that took the photo
   */
  public LocationPhoto(Bitmap bitmap, Player player) {
    photo = bitmap;
    this.player = player;
  }

  /**
   * This initializes a Photo with an image of a QRCode and the player that took it
   *
   * @param image The location image of the QR Code, as an ImageProxy type
   * @param player This is the player that took the photo
   */
  public LocationPhoto(ImageProxy image, Player player) {
    setPhoto(image);
    this.player = player;
  }

  /**
   * This returns the image as a bitmap, ready to be displayed by an ImageView
   *
   * @return Return the photo
   */
  public Bitmap getPhoto() {
    return photo;
  }

  /**
   * This returns the player who took the photo
   *
   * @return Return the player who took the photo
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * This sets the image of the photo, given a Bitmap
   *
   * @param bitmap This is the bitmap to set photo to
   */
  public void setPhoto(Bitmap bitmap) {
    photo = bitmap;
  }

  /**
   * This sets the image of the photo, given an ImageProxy
   *
   * @param image This is the image to set photo to
   */
  public void setPhoto(ImageProxy image) {
    // Convert ImageProxy to Bitmap
    // Source: https://stackoverflow.com/a/41776098 (2017-01-21)
    // Author: Rod_Algonquin (https://stackoverflow.com/users/3444777/rod-algonquin)
    // License: CC BY-SA
    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
    byte[] bytes = new byte[buffer.capacity()];
    buffer.get(bytes);
    photo = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);

    // Rotate image so it's correct orientation
    // Source: https://stackoverflow.com/a/14645289 (2013-02-02)
    // Author: Aryan (https://stackoverflow.com/users/2032325/aryan)
    // License: CC BY-SA
    Matrix rotationMatrix = new Matrix();
    rotationMatrix.postRotate(90);
    photo =
        Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), rotationMatrix, true);
  }
}
