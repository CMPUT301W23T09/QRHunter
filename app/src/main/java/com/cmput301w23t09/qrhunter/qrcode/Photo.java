package com.cmput301w23t09.qrhunter.qrcode;

import android.media.Image;

import com.cmput301w23t09.qrhunter.player.Player;

/**
 * This is a class that stores the photo a player took of a QR code
 */
public class Photo {
    /**
     * This is an image of a QR code
     */
    private Image photo;  // type subject to change
    /**
     * This is the player that took the photo
     */
    private Player player;

    /**
     * This initializes a Photo with an image of a QRCode and the player that took it
     * @param photo
     * This is a image of a QR code
     * @param player
     * This is the player that took the photo
     */
    public Photo(Image photo, Player player) {
        this.photo = photo;
        this.player = player;
    }

    /**
     * This returns the image of the photo
     * @return
     * Return the image
     */
    public Image getPhoto() {
        return photo;
    }

    /**
     * This returns the player who took the photo
     * @return
     * Return the player who took the photo
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * This sets the image of the photo
     * @param photo
     * This is the image to set to
     */
    public void setPhoto(Image photo) {
        this.photo = photo;
    }
}
