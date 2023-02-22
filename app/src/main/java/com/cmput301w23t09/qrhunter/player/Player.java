package com.cmput301w23t09.qrhunter.player;

import com.cmput301w23t09.qrhunter.util.ValidationUtils;

import java.util.UUID;

public class Player {
    /**
     * This is the firebase document id if any is associated with this object.
     */
    private String documentId;
    private UUID deviceId;
    private String username;
    private String phoneNo;
    private String email;

    public Player(UUID deviceId, String username, String phoneNo, String email) {
        this.deviceId = deviceId;
        this.username = username;
        this.phoneNo = phoneNo;
        this.email = email;
    }

    /**
     * This initializes a Player with all the values of a player and a firebase document id.
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

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (!ValidationUtils.isValidUsername(username)) {
            throw new IllegalArgumentException("The provided username is not valid.");
        }

        this.username = username;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        if (!ValidationUtils.isValidPhoneNo(phoneNo)) {
            throw new IllegalArgumentException("The provided phone number is not valid.");
        }

        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (!ValidationUtils.isValidPhoneNo(email)) {
            throw new IllegalArgumentException("The provided email is not valid.");
        }

        this.email = email;
    }

}
