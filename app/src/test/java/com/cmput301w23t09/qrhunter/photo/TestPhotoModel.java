package com.cmput301w23t09.qrhunter.photo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import android.graphics.Bitmap;
import com.cmput301w23t09.qrhunter.player.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

// needs work with mock bitmap generation
public class TestPhotoModel {
  // create a mock bitmap
  @Mock private Bitmap mockBitmap;
  // create a mock player
  private Player mockPlayer() {
    UUID mockUUID = UUID.randomUUID();
    return new Player(mockUUID, "Username", "587-998-1206", "mock-email@gmail.com", new ArrayList());
  }

  // test getting the bitmap image of a photo
  @Test
  public void testGetBitmapImage() {
    assertNull(new Photo((Bitmap) null, mockPlayer()).getPhoto());
  }

  // test getting the player of a photo
  @Test
  public void testGetPlayer() {
    Player player =
        new Player(UUID.randomUUID(), "Username", "587-998-1206", "mock-email@gmail.com", new ArrayList<>());
    Photo photo = new Photo(mockBitmap, player);
    assertEquals(photo.getPlayer(), player);
  }

  // test setting the photo of a photo to a different bitmap
  @Test
  public void testSetBitmapImage() {
    Photo photo = new Photo(mockBitmap, mockPlayer());
    // change the bitmap of the photo
    photo.setPhoto((Bitmap) null);
    // check whether bitmap of photo was changed
    assertNull(photo.getPhoto());
  }
}
