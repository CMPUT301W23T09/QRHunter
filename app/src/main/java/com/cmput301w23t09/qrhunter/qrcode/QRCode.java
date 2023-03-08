package com.cmput301w23t09.qrhunter.qrcode;

import android.graphics.Bitmap;
import android.media.Image;
import com.cmput301w23t09.qrhunter.comment.Comment;
import com.cmput301w23t09.qrhunter.map.Location;
import com.cmput301w23t09.qrhunter.player.Player;
import java.io.Serializable;
import java.util.ArrayList;

/** This class defines a QR code */
public class QRCode implements Serializable {
  /** This is the hash of the QR code */
  private String hash;
  /** This is the name of the QR code */
  private String name;
  /** This is the visual representation of the QR code */
  private Image visualRepresentation; // type subject to change
  /** This is the score of the QR code */
  private Integer score;
  /** This is the location of the QR code */
  private Location loc;
  /** This is a list of photos that have been taken of the QR code */
  private ArrayList<Bitmap> photos;
  /** This is a list of comments on the QR code */
  private ArrayList<Comment> comments;
  /** This is a list of players who have scanned this QR code */
  private ArrayList<Player> players;

  /**
   * Initializes a *newly-scanned* QRCode using only its hash value
   *
   * @param hash The hash of the newly-scanned QRCode
   */
  public QRCode(String hash) {
    this.hash = hash;

    // TODO: Initialize all these fields according to hash
    this.name = "";
    this.visualRepresentation = null;
    this.score = 0;

    this.loc = null;
    this.photos = new ArrayList<>();
    this.comments = new ArrayList<>();
    this.players = new ArrayList<>();
  }

  /**
   * This initializes a QR code with its hash, name, visual representation, score, location, photo,
   * comments, and players who have scanned it
   *
   * @param hash This is the hash of the QR code
   * @param name This is the name of the QR code
   * @param visualRepresentation This is the visual representation of the QR code
   * @param score This is the score of the QR code
   * @param loc This is the location of the QR code
   * @param photos This is the list of photos of the QR code
   * @param comments This is the list of comments on the QR code
   * @param players This is the list of players that have scanned the QR code
   */
  public QRCode(
      String hash,
      String name,
      Image visualRepresentation,
      Integer score,
      Location loc,
      ArrayList<Bitmap> photos,
      ArrayList<Comment> comments,
      ArrayList<Player> players) {
    this.hash = hash;
    this.name = name;
    this.visualRepresentation = visualRepresentation;
    this.score = score;
    this.loc = loc;
    this.photos = photos;
    this.comments = comments;
    this.players = players;
  }

  /**
   * This returns the hash of the QR code
   *
   * @return Return the hash of the QR code
   */
  public String getHash() {
    return hash;
  }

  /**
   * This returns the name of the QR code
   *
   * @return Return the name of the QR code
   */
  public String getName() {
    return name;
  }

  /**
   * This returns the visual representation of the QR code
   *
   * @return Return the visual representation of the QR code
   */
  public Image getVisualRepresentation() {
    return visualRepresentation;
  }

  /**
   * This returns the score of the QR code
   *
   * @return Return the score of the QR code
   */
  public Integer getScore() {
    return this.score;
  }

  /**
   * This returns the location of the QR code
   *
   * @return Return the location of the QR code
   */
  public Location getLoc() {
    return loc;
  }

  /**
   * This returns the photos taken of the QR code
   *
   * @return Return the photos taken of the QR code
   */
  public ArrayList<Bitmap> getPhotos() {
    return photos;
  }

  /**
   * This returns the player who have scanned the QR code
   *
   * @return Return the player who have scanned the QR code
   */
  public ArrayList<Player> getPlayers() {
    return players;
  }

  /**
   * This returns the comments on the QR code
   *
   * @return Return the comments on the QR code
   */
  public ArrayList<Comment> getComments() {
    return comments;
  }

  /**
   * This sets the location of the QR code
   *
   * @param loc This is the location to set to
   */
  public void setLoc(Location loc) {
    this.loc = loc;
  }

  /**
   * This adds a comment to the QR code
   *
   * @param comment This is the comment to add
   */
  public void addComment(Comment comment) {
    this.comments.add(comment);
  }

  /**
   * This deletes a comment from the QR code
   *
   * @param comment This is the comment to delete
   */
  public void deleteComment(Comment comment) {
    this.comments.remove(comment);
  }

  /**
   * This adds a photo taken of the QR code
   *
   * @param photo This is the photo to add
   */
  public void addPhoto(Bitmap photo) {
    this.photos.add(photo);
  }

  /**
   * This deletes a photo taken of the QR code
   *
   * @param photo This is the photo to delete
   */
  public void deletePhoto(Bitmap photo) {
    this.photos.remove(photo);
  }

  /**
   * This adds a player that has scanned the QR code
   *
   * @param player This is the player to add
   */
  public void addPlayer(Player player) {
    this.players.add(player);
  }

  /**
   * This removes a player that has scanned the QR code
   *
   * @param player This is the player to delete
   */
  public void deletePlayer(Player player) {
    this.players.remove(player);
  }
}
