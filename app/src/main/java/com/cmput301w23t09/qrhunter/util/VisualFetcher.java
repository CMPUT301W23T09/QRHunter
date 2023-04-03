package com.cmput301w23t09.qrhunter.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Fetches QRCode's visual representation and profile pictures from DiceBear
 * (https://www.dicebear.com) License: MIT
 *
 * <p>Art Styles Used:
 *
 * <ul>
 *   <li>Botts Neutral by Pablo Stanley (https://twitter.com/pablostanley) (License: Free for
 *       personal/commercial use)
 *   <li>Identicon by Florian Körner (contact@florian-koerner.com) (License: CC BY)
 * </ul>
 *
 * <p>API Call Code was adapted from: Source:
 * https://stackoverflow.com/questions/24399294/android-asynctask-to-make-an-http-get-request By:
 * Rishabh Dixit (https://stackoverflow.com/users/6940935/rishabh-dixit) (03/09/17) License: cc-wiki
 *
 * <p>Source: https://www.digitalocean.com/community/tutorials/android-asynctask-example-tutorial
 * By: Anupam Chugh (09/03/22) License: CC BY-NC-SA 4.0
 *
 * <p>Source:
 * https://stackoverflow.com/questions/62585357/how-to-print-read-image-from-rest-api-call-using-java
 * By: abk (https://stackoverflow.com/users/3500996/abk) (03/24/21) Licsense: cc-wiki
 *
 * <p>Again, all the code was by andy-mtng (Andy Nguyen's) but git blamed on jmmabanta who
 * integrated his feature.
 */
public class VisualFetcher extends AsyncTask<String, Void, Bitmap> {
  private int responseCode;

  @Override
  protected Bitmap doInBackground(String... urls) {
    try {
      URL url = new URL(urls[0]);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");

      responseCode = conn.getResponseCode();
      InputStream inputStream;
      if (responseCode < HttpURLConnection.HTTP_BAD_REQUEST) {
        inputStream = conn.getInputStream();
      } else {
        inputStream = conn.getErrorStream();
      }

      Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
      return bitmap;

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  //  @Override
  //  protected void onPostExecute(Bitmap result) {
  //    if (result != null) {
  //      qrCode.setVisualRepresentation(result);
  //    } else {
  //      Log.d("imageApiCall", "API call for DiceBear failed.");
  //    }
  //  }

  public int getResponseCode() {
    return responseCode;
  }
}
