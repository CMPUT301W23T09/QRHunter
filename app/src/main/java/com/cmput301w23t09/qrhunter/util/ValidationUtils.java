package com.cmput301w23t09.qrhunter.util;

import android.telephony.PhoneNumberUtils;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** Utility class to validate various inputs. */
public class ValidationUtils {

  private ValidationUtils() {}

  /**
   * Checks if the provided username is a valid username that is between lengths 1-20 inclusive.
   *
   * @param username username to check
   * @return if the username is valid
   */
  public static boolean isValidUsername(String username) {
    return username.length() > 0 && username.length() <= 20;
  }

  /**
   * Checks if the provided phone number is a valid phone number.
   *
   * @param phoneNo phone number to check
   * @return if the phone number is valid
   */
  public static boolean isValidPhoneNo(String phoneNo) {
    return PhoneNumberUtils.isGlobalPhoneNumber(phoneNo);
  }

  /**
   * Checks if the provided email is as valid email.
   *
   * @param email email to check
   * @return if the email is valid
   */
  public static boolean isValidEmail(String email) {
    List<String> parts =
        Arrays.stream(email.split("@"))
            .map(String::trim)
            .filter(str -> str.length() > 0)
            .collect(Collectors.toList());

    if (parts.size() != 2) {
      return false; // multiple @
    }

    String user = parts.get(0);
    String domain = parts.get(1);

    return user.matches("(^[\\w][\\w.\\-+]+[\\w]$)|^(\\w)+$")
        && domain.matches("(^[\\w][\\w.\\-\\+]+[\\w][.][\\w]+)|^(\\w)+\\.\\+(\\w)+$");
  }
}
