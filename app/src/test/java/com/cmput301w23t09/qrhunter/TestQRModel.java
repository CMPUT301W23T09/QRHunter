package com.cmput301w23t09.qrhunter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import android.graphics.Bitmap;
import com.cmput301w23t09.qrhunter.comment.Comment;
import com.cmput301w23t09.qrhunter.locationphoto.LocationPhoto;
import com.cmput301w23t09.qrhunter.map.QRLocation;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.ScoreComparator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** This tests the methods of the QRCode class */
public class TestQRModel {
  // create a mock hash
  /** This is a mock hash */
  private final String mockHash =
      "8926bb85b4e02cf2c877070dd8dc920acbf6c7e0153b735a3d9381ec5c2ac11d";

  /**
   * This creates a mock QRLocation objects
   *
   * @return Return the created object
   */
  private QRLocation mockLoc() {
    return new QRLocation("0;0;City");
  }

  /**
   * This creates a mock QRCode object
   *
   * @return Return the created object
   */
  private QRCode mockCode() {
    return new QRCode(mockHash);
  }

  /**
   * This creates a mock list of player IDs
   *
   * @return Return the created mock list
   */
  private ArrayList<String> mockPlayers() {
    ArrayList<String> players = new ArrayList<>();
    String playerID = String.valueOf(UUID.randomUUID());
    players.add(playerID);
    return players;
  }

  /**
   * This creates a mock list of Comment objects
   *
   * @return Return the created mock list
   */
  private ArrayList<Comment> mockComments() {
    ArrayList<Comment> comments = new ArrayList<>();
    Comment comment = new Comment("UserId", "Username", "MockComment");
    comments.add(comment);
    return comments;
  }

  /**
   * This creates a mock list of LocationPhoto objects
   *
   * @return Return the created mock list
   */
  private ArrayList<LocationPhoto> mockPhotos() {
    ArrayList<LocationPhoto> photos = new ArrayList<>();
    LocationPhoto photo = new LocationPhoto((Bitmap) null, null);
    photos.add(photo);
    return photos;
  }

  /** This tests the getter for the QRCode's hash */
  @Test
  public void testGetHash() {
    assertEquals(mockCode().getHash(), mockHash);
  }

  /** This tests the getter for the QRCode's name */
  @Test
  public void testGetName() {
    assertEquals(mockCode().getName(), "Robbel Spicy Tiger");
  }

  /** This tests the getter for the QRCode's score */
  @Test
  public void testGetScore() {
    assertEquals((int) mockCode().getScore(), 32);
  }

  /** This tests the getter for the QRCode's location */
  @Test
  public void testGetLoc() {
    QRCode qr = new QRCode(mockHash, "Robbel Spicy Tiger", 32, mockLoc(), null, null, null, null);
    QRLocation loc = qr.getLoc();
    assertNotNull(loc);
    assertEquals(loc.getLatitude(), mockLoc().getLatitude());
    assertEquals(loc.getLongitude(), mockLoc().getLongitude());
  }

  /** This tests the setter for the QRCode's location */
  @Test
  public void testSetLoc() {
    QRCode qr = mockCode();
    qr.setLoc(mockLoc());
    QRLocation loc = qr.getLoc();
    assertNotNull(loc);
    assertEquals(loc.getLatitude(), mockLoc().getLatitude());
    assertEquals(loc.getLongitude(), mockLoc().getLongitude());
  }

  /** This tests the getter for the QRCode's players */
  @Test
  public void testGetPlayers() {
    ArrayList<String> players = mockPlayers();
    QRCode qr = new QRCode(mockHash, "Robbel Spicy Tiger", 32, null, null, null, null, players);
    assertEquals(qr.getPlayers(), players);
  }

  /** This tests the setter for the QRCode's players */
  @Test
  public void testSetPlayers() {
    ArrayList<String> players = mockPlayers();
    QRCode qr = mockCode();
    qr.setPlayers(players);
    assertEquals(qr.getPlayers(), players);
  }

  /** Checks the adding of a player to a QRCode */
  @Test
  public void testAddPlayer() {
    QRCode qr = mockCode();
    assertEquals(0, qr.getPlayers().size());
    String playerID = mockPlayers().get(0);
    qr.addPlayer(playerID);
    assertEquals(1, qr.getPlayers().size());
    assertEquals(qr.getPlayers().get(0), playerID);
  }

  /** Checks the deletion of a player from a QRCode */
  @Test
  public void testDeletePlayer() {
    ArrayList<String> players = mockPlayers();
    QRCode qr = mockCode();
    qr.setPlayers(players);
    assertEquals(1, qr.getPlayers().size());
    qr.deletePlayer(players.get(0));
    assertEquals(0, qr.getPlayers().size());
  }

  /** This tests the getter for the QRCode's comments */
  @Test
  public void testGetComments() {
    ArrayList<Comment> comments = mockComments();
    QRCode qr = new QRCode(mockHash, "Robbel Spicy Tiger", 32, null, null, null, comments, null);
    assertEquals(qr.getComments(), comments);
  }

  /** This tests the setter for the QRCode's comments */
  @Test
  public void testSetComments() {
    ArrayList<Comment> comments = mockComments();
    QRCode qr = mockCode();
    qr.setComments(comments);
    assertEquals(qr.getComments(), comments);
  }

  /** Checks the addition of a comment to a QRCode */
  @Test
  public void testAddComment() {
    QRCode qr = mockCode();
    assertEquals(0, qr.getComments().size());
    Comment comment = mockComments().get(0);
    qr.addComment(comment);
    assertEquals(1, qr.getComments().size());
    assertEquals(qr.getComments().get(0), comment);
  }

  /** Checks the deletion of a comment from a QRCode */
  @Test
  public void testDeleteComment() {
    QRCode qr = mockCode();
    ArrayList<Comment> comments = mockComments();
    qr.setComments(comments);
    assertEquals(1, qr.getComments().size());
    qr.deleteComment(comments.get(0));
    assertEquals(0, qr.getComments().size());
  }

  /** This tests the getter for the QRCode's photos */
  @Test
  public void testGetPhotos() {
    ArrayList<LocationPhoto> photos = mockPhotos();
    QRCode qr = new QRCode(mockHash, "Robbel Spicy Tiger", 32, null, null, photos, null, null);
    assertEquals(qr.getPhotos(), photos);
  }

  /** This tests the setter for the QRCode's photos */
  @Test
  public void testSetPhotos() {
    ArrayList<LocationPhoto> photos = mockPhotos();
    QRCode qr = mockCode();
    qr.setPhotos(photos);
    assertEquals(qr.getPhotos(), photos);
  }

  /** Checks the addition of a photo to a QRCode */
  @Test
  public void testAddPhoto() {
    QRCode qr = mockCode();
    assertEquals(0, qr.getPhotos().size());
    LocationPhoto photo = mockPhotos().get(0);
    qr.addPhoto(photo);
    assertEquals(1, qr.getPhotos().size());
    assertEquals(qr.getPhotos().get(0), photo);
  }

  /** Checks the deletion of a photo from a QRCode */
  @Test
  public void testDeletePhoto() {
    QRCode qr = mockCode();
    ArrayList<LocationPhoto> photos = mockPhotos();
    qr.setPhotos(photos);
    assertEquals(1, qr.getPhotos().size());
    qr.deletePhoto(photos.get(0));
    assertEquals(0, qr.getPhotos().size());
  }

  /** Checks the addition of a location to a QRCode */
  @Test
  public void testAddLocation() {
    QRLocation csc = new QRLocation("Edmonton", 53.52678, -113.52708); // CSC
    QRCode mockQR = mockCode();
    mockQR.addLocation(csc);
    assertTrue(mockQR.getLocations().contains(csc));
  }

  /** Checks the addition of a location near an existing location of a QRCode */
  @Test
  public void testAddLocationTooClose() {
    QRLocation csc = new QRLocation("Edmonton", 53.52678, -113.52708); // CSC
    QRLocation athabasca =
        new QRLocation("Edmonton", 53.52671, -113.52663); // Athabasca Hall (within 100m of CSC)
    QRCode mockQR = mockCode();
    mockQR.addLocation(csc);
    mockQR.addLocation(athabasca); // Should not add Athabasca
    assertTrue(mockQR.getLocations().contains(csc));
    assertFalse(mockQR.getLocations().contains(athabasca));
  }

  /** This tests the setter and removal for the location of a QRCode */
  @Test
  public void testSetAndRemoveLocation() {
    QRLocation csc = new QRLocation("Edmonton", 53.52678, -113.52708); // CSC
    ArrayList<QRLocation> locations = new ArrayList<>();
    locations.add(csc);
    QRCode mockQR = mockCode();
    mockQR.setLocations(locations);
    assertTrue(mockQR.getLocations().contains(csc));
    mockQR.removeLocation(csc);
    assertFalse(mockQR.getLocations().contains(csc));
  }

  /** Test the sorting of a list of QRCodes */
  @Test
  public void testQRCodeSort() {
    // add qr codes to an array
    ArrayList<QRCode> qrCodes = new ArrayList<>();
    qrCodes.add(mockCode()); // adds a QRCode with a score of 32
    String otherHash = "91d6263f14b535452bb0556e1c0fecbac5c2b043344fc224fdbbab78148f7e6f";
    qrCodes.add(new QRCode(otherHash)); // adds a QRCode with a score of 36
    // create a sort comparator
    Comparator<QRCode> comparator = new ScoreComparator();
    // check sorting in ascending order
    qrCodes.sort(comparator);
    assertEquals(qrCodes.get(0).getScore(), 32);
    // check sorting in descending order
    qrCodes.sort(comparator.reversed());
    assertEquals(qrCodes.get(0).getScore(), 36);
  }
}
