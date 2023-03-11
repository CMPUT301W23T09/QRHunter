package com.cmput301w23t09.qrhunter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import android.graphics.Bitmap;

import org.junit.Test;

public class ImageApiCallTaskTest {
    @Test
    public void apiSuccessResponseTest() {
        ImageApiCallTask imageApiCallTask = new ImageApiCallTask();
        imageApiCallTask.execute("https://api.dicebear.com/5.x/pixel-art/svg?seed=Bob");
        try {
            Bitmap result = imageApiCallTask.get();
            int responseCode = imageApiCallTask.getResponseCode();
            assertNotNull(result);
            assertTrue(responseCode >= 200 && responseCode < 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
