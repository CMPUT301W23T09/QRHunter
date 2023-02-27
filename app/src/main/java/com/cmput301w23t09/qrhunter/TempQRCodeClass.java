package com.cmput301w23t09.qrhunter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class TempQRCodeClass {
    String qrCodeHash = "696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6";
    String name;

    // Citation: How to convert hex to binary https://stackoverflow.com/questions/8640803/convert-hex-string-to-binary-string
    private String getFirstSixBits() {
        char hexidecimalNum = qrCodeHash.charAt(0);
        int decimal = Integer.parseInt(String.valueOf(hexidecimalNum), 16);
        String binary = Integer.toBinaryString(decimal);
        return binary;
    }

    // Create and store large and small image
    private void createName() {
        String binary = getFirstSixBits();
        String nameBitOne = (binary.charAt(0) == '0') ? "Quirky" : "Surreal";
        String nameBitTwo = (binary.charAt(1) == '0') ? "Quirky" : "Surreal";
        String nameBitThree = (binary.charAt(2) == '0') ? "Quirky" : "Surreal";
        String nameBitFour = (binary.charAt(3) == '0') ? "Quirky" : "Surreal";
        String nameBitFive = (binary.charAt(4) == '0') ? "Quirky" : "Surreal";
        String nameBitSix = (binary.charAt(5) == '0') ? "Quirky" : "Surreal";

        name = nameBitOne + " " + nameBitTwo + " " + " " + nameBitThree + " " + nameBitFour + " " + nameBitFive + " " + nameBitSix;
    }

    // Refactor: Remove hard-coded values
    // Citation on how canvas works: https://stackoverflow.com/questions/17954596/how-to-draw-circle-by-canvas-in-android#:~:text=If%20you%20are%20using%20your,canvas%20to%20draw%20a%20circle.
    // citation on how to save canvas as png: https://stackoverflow.com/questions/13533471/how-to-save-view-from-canvas-to-png-file
    private void createImage() {
        String binary = getFirstSixBits();
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        Paint paint2 = new Paint();
        Paint paint3 = new Paint();

        paint.setColor((binary.charAt(0) == '0') ? Color.RED : Color.BLUE);
        canvas.drawColor((binary.charAt(1) == '0') ? Color.YELLOW : Color.GREEN);

        if (binary.charAt(2) == '0') {
            canvas.drawCircle(50, 50, 20, paint);
        } else {
            canvas.drawRect(10, 10, 90, 90, paint);
        }

        paint2.setColor((binary.charAt(3) == '0') ? Color.rgb(238,130,238) : Color.rgb(64,224,208));

        if (binary.charAt(4) == '0') {
            canvas.drawCircle(50, 50, 10, paint2);
        } else {
            canvas.drawRect(20, 20, 80, 80, paint2);
        }

        paint3.setColor(Color.rgb(255,105,180));

        if (binary.charAt(5) == '0') {
            canvas.drawCircle(50, 50, 10, paint3);
        } else {
            canvas.drawRect(20, 20, 80, 80, paint3);
        }

        saveImage(bitmap);
    }

    private void saveImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageBytes = stream.toByteArray();

        // TODO: Save to firebase
    }
}
