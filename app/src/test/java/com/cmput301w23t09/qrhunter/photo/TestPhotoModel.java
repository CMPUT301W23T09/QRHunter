package com.cmput301w23t09.qrhunter.photo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import android.graphics.Bitmap;

import com.cmput301w23t09.qrhunter.player.Player;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.UUID;

/* ... requires mockito? ...
public class TestPhotoModel {
  // create 2 different blank mock bitmap images
  @Mock
  private Bitmap mockBitmap1 = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
  @Mock
  private Bitmap mockBitmap2 = Bitmap.createBitmap(50, 10, Bitmap.Config.ARGB_8888);
  // create a mock player
  private Player mockPlayer() {
    UUID mockUUID = UUID.randomUUID();
    return new Player(mockUUID, "Username", "587-998-1206", "mock-email@gmail.com");
  }
  // create a mock photo
  private Photo mockPhoto() {
    return new Photo(mockBitmap1, mockPlayer());
  }

  // test getting the bitmap image of a photo
  @Test
  public void testGetBitmapImage() {
    assertTrue(mockPhoto().getPhoto().sameAs(mockBitmap1));
  }

  // test getting the player of a photo
  @Test
  public void testGetPlayer() {
    Player player =
        new Player(UUID.randomUUID(), "Username", "587-998-1206", "mock-email@gmail.com");
    Photo photo = new Photo(mockBitmap1, player);
    assertEquals(photo.getPlayer(), player);
  }

  // test setting the photo of a photo to a different bitmap
  @Test
  public void testSetBitmapImage() {
    Photo photo = mockPhoto();
    // set the bitmap of the photo to a different bitmap
    photo.setPhoto(mockBitmap2);
    // check whether bitmap of photo was changed
    assertFalse(photo.getPhoto().sameAs(mockBitmap1));
    assertTrue(photo.getPhoto().sameAs(mockBitmap2));
  }
}
*/