package com.cmput301w23t09.qrhunter.qrcode;

import android.graphics.Bitmap;
import android.location.Location;
import com.cmput301w23t09.qrhunter.comment.Comment;
import com.cmput301w23t09.qrhunter.photo.Photo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/** This class defines a QR code */
public class QRCode implements Serializable {
  /** This is the hash of the QR code */
  private String hash;
  /** This is the name of the QR code */
  private String name;
  /** This is the visual representation of the QR code */
  private Bitmap visualRepresentation;
  /** This is the score of the QR code */
  private Integer score;
  /** This is the location of the QR code */
  private Location loc;
  /** This is a list of photos that have been taken of the QR code */
  private ArrayList<Photo> photos;
  /** This is a list of comments on the QR code */
  private ArrayList<Comment> comments;
  /** This is a list of players (UUID) who have scanned this QR code */
  private ArrayList<String> players;

  /**
   * Initializes a *newly-scanned* QRCode using only its hash value
   *
   * @param hash The hash of the newly-scanned QRCode
   */
  public QRCode(String hash) throws ExecutionException, InterruptedException {
    this.hash = hash;

    // TODO: Initialize all these fields according to hash
    this.name = generateName(hash);
    this.visualRepresentation =
        new QRCodeVisualFetcher(this)
            .execute("https://api.dicebear.com/5.x/pixel-art-neutral/jpg?seed=" + hash)
            .get();
    this.score = calculateScore(hash);

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
   * @param score This is the score of the QR code
   * @param loc This is the location of the QR code
   * @param photos This is the list of photos of the QR code
   * @param comments This is the list of comments on the QR code
   * @param players This is the list of players (documentIDs) that have scanned the QR code
   */
  public QRCode(
      String hash,
      String name,
      Integer score,
      Location loc,
      ArrayList<Photo> photos,
      ArrayList<Comment> comments,
      ArrayList<String> players)
      throws ExecutionException, InterruptedException {
    this.hash = hash;
    this.name = name;
    this.visualRepresentation =
        new QRCodeVisualFetcher(this)
            .execute("https://api.dicebear.com/5.x/pixel-art-neutral/jpg?seed=" + hash)
            .get();
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
  public Bitmap getVisualRepresentation() {
    return visualRepresentation;
  }

  /**
   * Sets the visual representation of the QR code
   *
   * @param visualRepresentation The Bitmap to represent the QR code
   */
  public void setVisualRepresentation(Bitmap visualRepresentation) {
    this.visualRepresentation = visualRepresentation;
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
  public ArrayList<Photo> getPhotos() {
    return photos;
  }

  /**
   * This returns the player who have scanned the QR code
   *
   * @return Return the player who have scanned the QR code
   */
  public ArrayList<String> getPlayers() {
    return players;
  }

  public void setPlayers(ArrayList<String> players) {
    this.players = players;
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
  public void addPhoto(Photo photo) {
    this.photos.add(photo);
  }

  /**
   * This deletes a photo taken of the QR code
   *
   * @param photo This is the photo to delete
   */
  public void deletePhoto(Photo photo) {
    this.photos.remove(photo);
  }

  /**
   * This adds a player that has scanned the QR code
   *
   * @param player This is the player to add
   */
  public void addPlayer(String player) {
    this.players.add(player);
  }

  /**
   * This removes a player that has scanned the QR code
   *
   * @param player This is the player to delete
   */
  public void deletePlayer(String player) {
    this.players.remove(player);
  }

  /**
   * Generates a name given the QRCode's hash
   *
   * @param hash The QRCode's hash
   * @return The name of the QRCode given its hash
   */
  private String generateName(String hash) {
    // Though the git blame is on jmmabanta (John Mabanta), this is all andy-mtng (Andy Nguyen's)
    // code
    String binary = getFirstSixBits(hash);
    String nameBitOne = (binary.charAt(0) == '0') ? "So" : "Ro";
    String nameBitTwo = (binary.charAt(1) == '0') ? "ba" : "da";
    String nameBitThree = (binary.charAt(2) == '0') ? "yin" : "qin";
    String nameBitFour = (binary.charAt(3) == '0') ? "ect" : "ly";
    String nameBitFive = (binary.charAt(4) == '0') ? "Panda" : "Tiger";
    String nameBitSix = (binary.charAt(5) == '0') ? "★" : "✿";

    return nameBitOne + nameBitTwo + nameBitThree + nameBitFour + nameBitFive + nameBitSix;
  }

  /**
   * Gets the first six bits of a hash
   *
   * <p>Source: https://stackoverflow.com/a/8640831 By: Sergey Kalinichenko
   * (https://stackoverflow.com/users/335858/sergey-kalinichenko) (2011-12-27) Edited By: Dave
   * Jarvis (https://stackoverflow.com/users/59087/dave-jarvis) (2014-08-20) License: CC BY-SA
   *
   * @param hash The QRCode's hash
   * @return A string representing the first six bits of the hash
   */
  private String getFirstSixBits(String hash) {
    hash = hash.substring(hash.length() - 5);
    long decimal = Long.parseLong(hash, 16);
    String binary = Long.toBinaryString(decimal);
    return binary;
  }

  /**
   * Given the hash, calculate sore based on the proposed scoring system
   *
   * @param hash SHA256 hash to base score off of
   * @return The score value of the QRCode
   */
  private int calculateScore(String hash) {
    int score = 0;
    char lastChar = '0';
    int streak = 0;
    for (int i = 0; i < hash.length(); i++) {
      char current = hash.charAt(i);
      if (current == lastChar) streak++;
      else if (streak > 0) {
        int value;
        if (lastChar == '0') value = 20;
        else value = Integer.parseInt(String.valueOf(lastChar), 16);
        score += Math.pow(value, streak);
        lastChar = current;
        streak = 0;
      } else {
        lastChar = current;
        streak = 0;
      }
    }

    // Add any left over streak
    if (streak > 0) {
      int value;
      if (lastChar == '0') value = 20;
      else value = Integer.parseInt(String.valueOf(lastChar), 16);
      score += Math.pow(value, streak);
    }

    return score;
  }
}
