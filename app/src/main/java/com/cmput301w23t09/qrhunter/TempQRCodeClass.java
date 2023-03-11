package com.cmput301w23t09.qrhunter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
        String nameBitOne = (binary.charAt(0) == '0') ? "Hot" : "Cold";
        String nameBitTwo = (binary.charAt(1) == '0') ? "Spicy" : "Mild";
        String nameBitThree = (binary.charAt(2) == '0') ? "Light" : "Dark";
        String nameBitFour = (binary.charAt(3) == '0') ? "Yellow" : "Purple";
        String nameBitFive = (binary.charAt(4) == '0') ? "Quirky" : "Cheerful";
        String nameBitSix = (binary.charAt(5) == '0') ? "Panda" : "Aardvark";

        name = nameBitOne + " " + nameBitTwo + " " + " " + nameBitThree + " " + nameBitFour + " " + nameBitFive + " " + nameBitSix;
    }

    private void createImage() {
        ImageApiCallTask imageApiCallTask = new ImageApiCallTask();
        String url = "https://api.dicebear.com/5.x/pixel-art/svg?seed=" + qrCodeHash
        imageApiCallTask.execute(url);
    }

    private void saveImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageBytes = stream.toByteArray();

        // TODO: Save to firebase
    }
}
