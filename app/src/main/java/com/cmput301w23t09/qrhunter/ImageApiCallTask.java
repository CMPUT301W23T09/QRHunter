package com.cmput301w23t09.qrhunter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// Citation: API Call information - https://www.java67.com/2019/03/7-examples-of-httpurlconnection-in-java.html
public class ImageApiCallTask extends AsyncTask<String, Void, Bitmap> {
    int responseCode;

    @Override
    protected Bitmap doInBackground(String... urls) {
        try {
            // Open a connection to the image API endpoint
            URL url = new URL(urls[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Set any required headers or parameters
//            conn.setRequestProperty("Authorization", "Bearer my-auth-token");

            responseCode = conn.getResponseCode();
            InputStream inputStream;
            if (responseCode < HttpURLConnection.HTTP_BAD_REQUEST) {
                inputStream = conn.getInputStream();
            } else {
                inputStream = conn.getErrorStream();
            }

            // Convert the InputStream to a Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            // run the save image method on the QR code
        } else {
            Log.d("imageApiCall", "API call for DiceBear failed.");
        }
    }

    public int getResponseCode() {
        return responseCode;
    }
}