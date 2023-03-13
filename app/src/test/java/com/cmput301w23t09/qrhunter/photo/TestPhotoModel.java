package com.cmput301w23t09.qrhunter.photo;

/* has issues with mocking bitmaps
public class TestPhotoModel {
  // create a mock bitmap
  @Mock
  private Bitmap mockBitmap;
  // create a mock player
  private Player mockPlayer() {
    UUID mockUUID = UUID.randomUUID();
    return new Player(mockUUID, "Username", "587-998-1206", "mock-email@gmail.com");
  }
  // create a mock photo
  private Photo mockPhoto() {
    return new Photo(mockBitmap, mockPlayer());
  }

  // test getting the bitmap image of a photo
  @Test
  public void testGetBitmapImage() {
    assertTrue(mockPhoto().getPhoto().sameAs(mockBitmap));
  }

  // test getting the player of a photo
  @Test
  public void testGetPlayer() {
    Player player =
        new Player(UUID.randomUUID(), "Username", "587-998-1206", "mock-email@gmail.com");
    Photo photo = new Photo(mockBitmap, player);
    assertEquals(photo.getPlayer(), player);
  }

  // test setting the photo of a photo to a different bitmap
  @Test
  public void testSetBitmapImage() {
    Photo photo = mockPhoto();
    // set the bitmap of the photo to a different bitmap
    photo.setPhoto((Bitmap) null);
    // check whether bitmap of photo was changed
    assertFalse(photo.getPhoto().sameAs(mockBitmap));
    assertTrue(photo.getPhoto().sameAs(null));
  }
}*/
