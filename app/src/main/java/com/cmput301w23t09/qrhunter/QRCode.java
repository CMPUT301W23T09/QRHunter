package com.cmput301w23t09.qrhunter;

import android.media.Image;

import java.util.ArrayList;

/**
 * This class defines a QR code
 */
public class QRCode {
    private String hash;
    private String name;
    private Image visualRepresentation;  // type subject to change
    private Integer score;
    private Location loc;
    private Photo locPhoto;
    private ArrayList<Comment> comments;
    private ArrayList<Player> players;
}
