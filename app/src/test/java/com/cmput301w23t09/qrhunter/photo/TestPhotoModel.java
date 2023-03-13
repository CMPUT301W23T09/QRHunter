package com.cmput301w23t09.qrhunter.photo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import android.graphics.Bitmap;

import androidx.camera.core.ImageProxy;

import com.cmput301w23t09.qrhunter.player.Player;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class TestPhotoModel {
  // create blank mock bitmap image
  private Bitmap mockBitmap() {
    int width = 10;
    int height = 10;
    Bitmap.Config config = Bitmap.Config.ARGB_8888;
    return Bitmap.createBitmap(width, height, config);
  }
  // create a mock player
  private Player mockPlayer() {
    UUID mockUUID = UUID.randomUUID();
    return new Player(mockUUID, "Username", "587-998-1206", "mock-email@gmail.com");
  }
  // create a mock photo
  private Photo mockPhoto() {
    return new Photo(mockBitmap(), mockPlayer());
  }

  // test getting the bitmap image of a photo
  @Test
  public void testGetBitmapImage() {
    assertTrue(mockPhoto().getPhoto().sameAs(mockBitmap()));
  }

  // test getting the player of a photo
  @Test
  public void testGetPlayer() {
    Player player =
        new Player(UUID.randomUUID(), "Username", "587-998-1206", "mock-email@gmail.com");
    Photo photo = new Photo(mockBitmap(), player);
    assertEquals(photo.getPlayer(), player);
  }

  // test setting the photo of a photo to a different bitmap
  @Test
  public void testSetBitmapImage() {
    Photo photo = mockPhoto();  // contains a bitmap equal to the mock bitmap
    // create a bitmap different from the mock bitmap
    int width = 50;
    int height = 10;
    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    // set the bitmap of the photo
    photo.setPhoto(bitmap);
    // check whether bitmap of photo was changed
    assertFalse(photo.getPhoto().sameAs(mockBitmap()));
    assertTrue(photo.getPhoto().sameAs(bitmap));
  }
}
