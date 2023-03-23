package com.cmput301w23t09.qrhunter.qrcode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Fetches QRCode's visual representation from DiceBear (https://www.dicebear.com) License: MIT Art
 * Style Used: Pixel Art Neutral (https://www.figma.com/community/file/1198754108850888330) By:
 * Florian Körner (contact@florian-koerner.com) License: CC BY
 *
 * <p>API Call Code was adapted from
 * https://www.java67.com/2019/03/7-examples-of-httpurlconnection-in-java.html By: Soma Sharma
 *
 * <p>Again, all the code was by andy-mtng (Andy Nguyen's) but git blamed on jmmabanta who
 * integrated his feature.
 */
public class QRCodeVisualFetcher extends AsyncTask<String, Void, Bitmap> {
  private int responseCode;

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
      return BitmapFactory.decodeStream(inputStream);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public int getResponseCode() {
    return responseCode;
  }
}
