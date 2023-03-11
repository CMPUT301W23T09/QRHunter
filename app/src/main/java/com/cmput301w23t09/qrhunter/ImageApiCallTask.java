package com.cmput301w23t09.qrhunter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// Citation: API Call information - https://www.java67.com/2019/03/7-examples-of-httpurlconnection-in-java.html
public class ImageApiCallTask extends AsyncTask<String, Void, Bitmap> {
    int responseCode;

    @Override
    protected Bitmap doInBackground(String apiUrl) {
        try {
            // Open a connection to the image API endpoint
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Set any required headers or parameters
//            conn.setRequestProperty("Authorization", "Bearer my-auth-token");

            // Get the response code and response body as an InputStream
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
        // Update the UI with the image data
        if (result != null) {
            // Display the image in an ImageView
            System.out.println("Response code: " + responseCode);
        } else {
            // Display an error message
            Log.d("imageApiCall", "API call for the DiceBear failed.");
        }
    }

    public int getResponseCode() {
        return responseCode;
    }
}