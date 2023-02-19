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

    public QRCode(String hash, String name, Image visualRepresentation, Integer score, Location loc, Photo locPhoto, ArrayList<Comment> comments, ArrayList<Player> players) {
        this.hash = hash;
        this.name = name;
        this.visualRepresentation = visualRepresentation;
        this.score = score;
        this.loc = loc;
        this.locPhoto = locPhoto;
        this.comments = comments;
        this.players = players;
    }

    public QRCode(String hash, String name, Image visualRepresentation, Integer score) {
        this.hash = hash;
        this.name = name;
        this.visualRepresentation = visualRepresentation;
        this.score = score;
    }

    public Integer getScore() {
        return this.score;
    }
}
