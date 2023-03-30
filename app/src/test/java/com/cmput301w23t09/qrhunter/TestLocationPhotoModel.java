package com.cmput301w23t09.qrhunter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import android.graphics.Bitmap;
import com.cmput301w23t09.qrhunter.locationphoto.LocationPhoto;
import com.cmput301w23t09.qrhunter.player.Player;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestLocationPhotoModel {
  // create a mock bitmap
  @Mock private Bitmap mockBitmap = mock(Bitmap.class);

  // test getting the bitmap image of a photo
  @Test
  public void testGetBitmapImage() {
    Bitmap photoImage = new LocationPhoto(mockBitmap, null).getPhoto();
    assertEquals(photoImage.getClass().toString(), "class android.graphics.Bitmap");
    assertEquals(System.identityHashCode(photoImage), System.identityHashCode(mockBitmap));
  }

  // test getting the player of a photo
  @Test
  public void testGetPlayer() {
    UUID mockUUID = UUID.randomUUID();
    Player player =
        new Player(mockUUID, "Username", "587-998-1206", "mock-email@gmail.com", new ArrayList<>());
    LocationPhoto photo = new LocationPhoto((Bitmap) null, player);
    assertEquals(photo.getPlayer(), player);
  }

  // test setting the photo of a photo to a different bitmap
  @Test
  public void testSetBitmapImage() {
    LocationPhoto photo = new LocationPhoto((Bitmap) null, null);
    assertNull(photo.getPhoto());
    photo.setPhoto(mockBitmap);
    assertEquals(photo.getPhoto().getClass().toString(), "class android.graphics.Bitmap");
    assertEquals(System.identityHashCode(photo.getPhoto()), System.identityHashCode(mockBitmap));
  }
}
