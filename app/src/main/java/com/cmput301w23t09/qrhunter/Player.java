package com.cmput301w23t09.qrhunter;

import java.util.ArrayList;

/**
 * This class defines a player that is using this app
 */
public class Player {
    /**
     * This is the device id of the player
     */
    private Integer deviceId;
    /**
     * This is the username of the player
     */
    private String username;
    /**
     * This is the contact info of the player
     */
    private String contact;  // string or integer?
    /**
     * This is a list of the player's QR codes
     */
    private ArrayList<QRCode> qrCodes = new ArrayList<QRCode>();

    /**
     * This initializes a Player with a device id, username, contact info, and list of QR codes
     * @param deviceId
     * This is the device id of the player
     * @param username
     * This is the username of the player
     * @param contact
     * This is the contact of the player
     * @param qrCodes
     * This is a list of the player's QR codes
     */
    public Player(Integer deviceId, String username, String contact, ArrayList<QRCode> qrCodes) {
        this.deviceId = deviceId;
        this.username = username;
        this.contact = contact;
        this.qrCodes = qrCodes;
    }

    /**
     * This returns the QR codes of the player
     * @return
     * Return the QR codes the player has scanned
     */
    public ArrayList<QRCode> getQRCodes() {
        return qrCodes;
    }

    /**
     * This returns the device id of the player
     * @return
     * Return the device id of the player
     */
    public Integer getDeviceId() {
        return deviceId;
    }

    /**
     * This returns the contact info of the player
     * @return
     * Return the contact info of the player
     */
    public String getContact() {
        return contact;
    }

    /**
     * This returns the username of the player
     * @return
     * Return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * This sets the contact info of the player
     * @param contact
     * This is the contact info to set to
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * This sets the device id of the player
     * @param deviceId
     * This is the device id to set to
     */
    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * This sets the QR codes that the player has scanned
     * @param qrCodes
     * This is the list of QR codes to set to
     */
    public void setQrCodes(ArrayList<QRCode> qrCodes) {
        this.qrCodes = qrCodes;
    }

    /**
     * This sets the username of the player
     * @param username
     * This is the username to set to
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * This adds a QR code to the ones that the user has scanned
     * @param qrCode
     * This is the QR code to add
     */
    public void addQRCode(QRCode qrCode) {
        qrCodes.add(qrCode);
    }

    /**
     * This removes a QR code from the ones that the user has scanned
     * @param qrCode
     * This is the QR code to remove
     */
    public void removeQRCode(QRCode qrCode) {
        qrCodes.remove(qrCode);
    }
}
