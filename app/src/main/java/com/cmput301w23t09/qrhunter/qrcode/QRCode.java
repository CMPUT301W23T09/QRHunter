package com.cmput301w23t09.qrhunter.qrcode;

import android.graphics.Bitmap;
import com.cmput301w23t09.qrhunter.comment.Comment;
import com.cmput301w23t09.qrhunter.locationphoto.LocationPhoto;
import com.cmput301w23t09.qrhunter.map.QRLocation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
  private QRLocation loc;
  /** This is a list of locations that the QR code has been found in */
  private ArrayList<QRLocation> locations;
  /** This is a list of photos that have been taken of the QR code */
  private ArrayList<LocationPhoto> locationPhotos;
  /** This is a list of comments on the QR code */
  private ArrayList<Comment> comments;
  /** This is a list of players (UUID) who have scanned this QR code */
  private ArrayList<String> players;

  /**
   * Initializes a *newly-scanned* QRCode using only its hash value
   *
   * @param hash The hash of the newly-scanned QRCode
   */
  public QRCode(String hash) {
    this.hash = hash;

    // TODO: Initialize all these fields according to hash
    this.name = generateName(hash);
    this.score = calculateScore(hash);
    this.visualRepresentation = null;

    this.loc = null;
    this.locations = new ArrayList<>();
    this.locationPhotos = new ArrayList<>();
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
   * @param locations This is the list of locations the QR was found in
   * @param locationPhotos This is the list of photos of the QR code
   * @param comments This is the list of comments on the QR code
   * @param players This is the list of players (documentIDs) that have scanned the QR code
   */
  public QRCode(
      String hash,
      String name,
      Integer score,
      QRLocation loc,
      ArrayList<QRLocation> locations,
      ArrayList<LocationPhoto> locationPhotos,
      ArrayList<Comment> comments,
      ArrayList<String> players) {
    this.hash = hash;
    this.name = name;
    this.score = score;
    this.loc = loc;
    this.locations = locations;
    this.locationPhotos = locationPhotos;
    this.comments = comments;
    this.players = players;
    this.visualRepresentation = null;
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
   * This returns the score of the QR code
   *
   * @return Return the score of the QR code
   */
  public Integer getScore() {
    return this.score;
  }

  /**
   * This returns the visual representation of the QR code
   *
   * @throws InterruptedException if failed to retrieve visual
   * @throws ExecutionException if failed to retrieve visual
   * @return Return the visual representation of the QR code
   */
  public Bitmap getVisualRepresentation() throws InterruptedException, ExecutionException {
    if (visualRepresentation == null) {
      visualRepresentation =
          new QRCodeVisualFetcher()
              .execute("https://api.dicebear.com/5.x/pixel-art-neutral/jpg?seed=" + hash)
              .get();
    }
    return visualRepresentation;
  }

  /**
   * This returns the location of the QR code
   *
   * @return Return the location of the QR code
   */
  public QRLocation getLoc() {
    return loc;
  }

  /**
   * This sets the location of the QR code
   *
   * @param loc This is the location to set to
   */
  public void setLoc(QRLocation loc) {
    this.loc = loc;
  }

  /**
   * Returns all locations of the QR code
   *
   * @return An ArrayList of this QR code's locations
   */
  public ArrayList<QRLocation> getLocations() {
    return locations;
  }

  /**
   * Sets the locations of the QR code
   *
   * @param locations The locations of the QRCode
   */
  public void setLocations(ArrayList<QRLocation> locations) {
    this.locations = locations;
  }

  /**
   * Adds a new location to the list of locations, as long as it's farther than 100m from any
   * existing location.
   *
   * @param loc New location to add
   */
  public void addLocation(QRLocation loc) {
    for (QRLocation pastLoc : locations) if (loc.distanceTo(pastLoc) <= 100) return;
    locations.add(loc);
  }

  /**
   * Removes a location from the list of locations.
   *
   * @param loc Location to remove
   */
  public void removeLocation(QRLocation loc) {
    locations.remove(loc);
  }

  /**
   * This returns the photos taken of the QR code
   *
   * @return Return the photos taken of the QR code
   */
  public List<LocationPhoto> getPhotos() {
    return locationPhotos;
  }

  /**
   * This returns the player who have scanned the QR code
   *
   * @return Return the player who have scanned the QR code
   */
  public List<String> getPlayers() {
    return players;
  }

  /**
   * This sets the players who have scanned the QR code
   *
   * @param players The players that have scanned the QR code
   */
  public void setPlayers(ArrayList<String> players) {
    this.players = players;
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
   * This returns the comments on the QR code
   *
   * @return Return the comments on the QR code
   */
  public ArrayList<Comment> getComments() {
    return comments;
  }

  /**
   * This sets the comments on the QR code
   *
   * @param comments The comments on the QR code
   */
  public void setComments(ArrayList<Comment> comments) {
    this.comments = comments;
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
   * This sets the photos of the QR code
   *
   * @param photos The photos on the QR code
   */
  public void setPhotos(ArrayList<LocationPhoto> photos) {
    this.locationPhotos = photos;
  }

  /**
   * This adds a photo taken of the QR code
   *
   * @param locationPhoto This is the photo to add
   */
  public void addPhoto(LocationPhoto locationPhoto) {
    this.locationPhotos.add(locationPhoto);
  }

  /**
   * This deletes a photo taken of the QR code
   *
   * @param locationPhoto This is the photo to delete
   */
  public void deletePhoto(LocationPhoto locationPhoto) {
    this.locationPhotos.remove(locationPhoto);
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
