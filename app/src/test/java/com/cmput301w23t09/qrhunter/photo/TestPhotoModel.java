package com.cmput301w23t09.qrhunter.photo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import android.graphics.Bitmap;

import com.cmput301w23t09.qrhunter.player.Player;

import org.junit.jupiter.api.Test;

import java.util.UUID;

public class TestPhotoModel {
    // create mock bitmap image
    private Bitmap mockBitmap() {
        int width = 10;
        int height = 10;
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        return Bitmap.createBitmap(10, 10, config);
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
    public void testGetImage() {
        assert(mockPhoto().getPhoto(), Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888));
    }

    // test getting the player of a photo
    @Test
    public void testGetPlayer() {
        Player player = new Player(UUID.randomUUID(), "Username", "587-998-1206", "mock-email@gmail.com");
        Photo photo = new Photo(mockBitmap(), player);
        assertEquals(photo.getPlayer(), player);
    }
}
