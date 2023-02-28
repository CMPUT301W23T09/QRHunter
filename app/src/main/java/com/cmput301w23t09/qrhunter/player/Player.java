package com.cmput301w23t09.qrhunter.player;

import com.cmput301w23t09.qrhunter.util.ValidationUtils;
import java.util.UUID;

public class Player {
  /** This is the firebase document id if any is associated with this object */
  private String documentId;
  /** UUID of the device this player is associated with */
  private UUID deviceId;
  /** Unique username of the player */
  private String username;
  /** Phone number of the player */
  private String phoneNo;
  /** Email of the player */
  private String email;

  /**
   * This initializes a Player with the deviceId, username, phoneNo, and email.
   *
   * @param deviceId device id
   * @param username username
   * @param phoneNo phone no
   * @param email email
   */
  public Player(UUID deviceId, String username, String phoneNo, String email) {
    this.deviceId = deviceId;
    this.username = username;
    this.phoneNo = phoneNo;
    this.email = email;
  }

  /**
   * This initializes a Player with the firebase document id, deviceId, username, phoneNo, and
   * email.
   *
   * @param documentId firebase document id of the player
   * @param deviceId device id of the player
   * @param username username of the player
   * @param phoneNo phone number of the player
   * @param email email of the player
   */
  public Player(String documentId, UUID deviceId, String username, String phoneNo, String email) {
    this.documentId = documentId;
    this.deviceId = deviceId;
    this.username = username;
    this.phoneNo = phoneNo;
    this.email = email;
  }

  /**
   * Get the firebase document id associated with this player
   *
   * @return document id
   */
  public String getDocumentId() {
    return documentId;
  }

  /**
   * Change the firebase document id associated with this player
   *
   * @param documentId document id
   */
  public void setDocumentId(String documentId) {
    this.documentId = documentId;
  }

  /**
   * Get the device id associated with this player
   *
   * @return device id
   */
  public UUID getDeviceId() {
    return deviceId;
  }

  /**
   * Set the device id associated with this player
   *
   * @param deviceId device id
   */
  public void setDeviceId(UUID deviceId) {
    this.deviceId = deviceId;
  }

  /**
   * Get the username associated with this player
   *
   * @return username of this player
   */
  public String getUsername() {
    return username;
  }

  /**
   * Change the username associated with this player
   *
   * @param username username
   * @throws IllegalArgumentException if username is not valid
   */
  public void setUsername(String username) {
    if (!ValidationUtils.isValidUsername(username)) {
      throw new IllegalArgumentException("The provided username is not valid.");
    }

    this.username = username;
  }

  /**
   * Get the pohne number associated with this player
   *
   * @return phone number
   */
  public String getPhoneNo() {
    return phoneNo;
  }

  /**
   * Change the phone number associated with this player
   *
   * @param phoneNo phone number of this player
   * @throws IllegalArgumentException if phone number is not valid
   */
  public void setPhoneNo(String phoneNo) {
    if (!ValidationUtils.isValidPhoneNo(phoneNo)) {
      throw new IllegalArgumentException("The provided phone number is not valid.");
    }

    this.phoneNo = phoneNo;
  }

  /**
   * Get the email associated with this player
   *
   * @return email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Change the email associated with this player
   *
   * @param email email associated with this player
   */
  public void setEmail(String email) {
    if (!ValidationUtils.isValidPhoneNo(email)) {
      throw new IllegalArgumentException("The provided email is not valid.");
    }

    this.email = email;
  }
}
