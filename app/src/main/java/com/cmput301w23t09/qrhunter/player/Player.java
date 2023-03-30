package com.cmput301w23t09.qrhunter.player;

import com.cmput301w23t09.qrhunter.util.ValidationUtils;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/** Represents a Player of QRHunter */
public class Player implements Serializable {
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
  /** QRCode hashes the player has scanned */
  private List<String> qrCodeHashes;

  /**
   * This initializes a Player with the deviceId, username, phoneNo, and email.
   *
   * @param deviceId device id
   * @param username username
   * @param phoneNo phone no
   * @param email email
   * @param qrCodeHashes QRCodes the player has scanned
   */
  public Player(
      UUID deviceId, String username, String phoneNo, String email, List<String> qrCodeHashes) {
    this.deviceId = deviceId;
    this.username = username;
    this.phoneNo = phoneNo;
    this.email = email;
    this.qrCodeHashes = qrCodeHashes;
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
   * @param qrCodeHashes QRCodes the player has scanned
   */
  public Player(
      String documentId,
      UUID deviceId,
      String username,
      String phoneNo,
      String email,
      List<String> qrCodeHashes) {
    this.documentId = documentId;
    this.deviceId = deviceId;
    this.username = username;
    this.phoneNo = phoneNo;
    this.email = email;
    this.qrCodeHashes = qrCodeHashes;
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
   * Get the phone number associated with this player
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
    if (!ValidationUtils.isValidEmail(email)) {
      throw new IllegalArgumentException("The provided email is not valid.");
    }

    this.email = email;
  }

  /**
   * Returns a set of QR code hashes that have been scanned by player
   *
   * @return a set of qr code hashes scanned by player
   */
  public List<String> getQRCodeHashes() {
    return qrCodeHashes;
  }

  /**
   * Sets the list of QR code hashes that have been scanned by player
   *
   * @param scannedQRHashes a list of scanned QR code Hashes associated with player
   */
  public void setQRCodeHashes(List<String> scannedQRHashes) {
    this.qrCodeHashes = scannedQRHashes;
  }

//  public boolean contains(String str, String substring) {
//    return str.indexOf(substring) != -1;
//  }
}
