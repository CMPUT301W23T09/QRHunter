package com.cmput301w23t09.qrhunter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ImageApiCallTaskTest {
    @Test
    public void apiSuccessResponseTest() {
        ImageApiCallTask imageApiCallTask = new ImageApiCallTask("https://api.dicebear.com/5.x/pixel-art/svg?seed=Bob");
        imageApiCallTask.execute();
        int responseCode = imageApiCallTask.getResponseCode();
        assertEquals(200, responseCode);
    }

}
