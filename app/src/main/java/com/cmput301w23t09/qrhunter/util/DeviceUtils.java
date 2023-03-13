package com.cmput301w23t09.qrhunter.util;

import android.app.Activity;
import android.content.SharedPreferences;
import java.util.UUID;

public class DeviceUtils {
  public static final String DEVICE_UUID_FILE = "device_uuid.dat";
  public static final String DEVICE_UUID_FILE_FIELD = "uuid";

  private DeviceUtils() {}

  /**
   * Retrieve the UUID associated with this device.
   *
   * @return device UUID
   * @param activity instance of an activity
   */
  public static UUID getDeviceUUID(Activity activity) {
    SharedPreferences sharedPreferences = activity.getSharedPreferences(DEVICE_UUID_FILE, 0);
    String savedDeviceId = sharedPreferences.getString(DEVICE_UUID_FILE_FIELD, null);

    if (savedDeviceId == null) {
      savedDeviceId = UUID.randomUUID().toString();
      sharedPreferences.edit().putString(DEVICE_UUID_FILE_FIELD, savedDeviceId).apply();
    }

    return UUID.fromString(savedDeviceId);
  }
}
