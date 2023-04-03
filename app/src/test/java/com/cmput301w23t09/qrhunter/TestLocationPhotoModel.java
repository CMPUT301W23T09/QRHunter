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

/** This tests the methods of the LocationPhoto class */
@RunWith(MockitoJUnitRunner.class)
public class TestLocationPhotoModel {
  /** This is a mock bitmap */
  @Mock private Bitmap mockBitmap = mock(Bitmap.class);

  /** This tests getting the bitmap image from a LocationPhoto */
  @Test
  public void testGetBitmapImage() {
    Bitmap photoImage = new LocationPhoto(mockBitmap, null).getPhoto();
    assertEquals(photoImage.getClass().toString(), "class android.graphics.Bitmap");
    assertEquals(System.identityHashCode(photoImage), System.identityHashCode(mockBitmap));
  }

  /** This tests getting the player from a LocationPhoto */
  @Test
  public void testGetPlayer() {
    UUID mockUUID = UUID.randomUUID();
    Player player =
        new Player(
            mockUUID,
            "Username",
            "587-998-1206",
            "mock-email@gmail.com",
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>());
    LocationPhoto photo = new LocationPhoto((Bitmap) null, player);
    assertEquals(photo.getPlayer(), player);
  }

  /** This tests setting the bitmap image from a LocationPhoto */
  @Test
  public void testSetBitmapImage() {
    LocationPhoto photo = new LocationPhoto((Bitmap) null, null);
    assertNull(photo.getPhoto());
    photo.setPhoto(mockBitmap);
    assertEquals(photo.getPhoto().getClass().toString(), "class android.graphics.Bitmap");
    assertEquals(System.identityHashCode(photo.getPhoto()), System.identityHashCode(mockBitmap));
  }
}
