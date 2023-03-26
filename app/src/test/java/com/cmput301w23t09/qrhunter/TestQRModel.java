package com.cmput301w23t09.qrhunter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.cmput301w23t09.qrhunter.comment.Comment;
import com.cmput301w23t09.qrhunter.photo.Photo;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;

import android.graphics.Bitmap;
import android.location.Location;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.ArrayList;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class TestQRModel {
  // create a mock hash
  private String mockHash = "8926bb85b4e02cf2c877070dd8dc920acbf6c7e0153b735a3d9381ec5c2ac11d";

  // create a mock location
  @Mock
  private Location mockLoc = new Location("");

  // create a mock list of player IDs
  private ArrayList<String> mockPlayers() {
    ArrayList<String> players = new ArrayList<>();
    String playerID = String.valueOf(UUID.randomUUID());
    players.add(playerID);
    return players;
  }

  // create a mock list of comments
  private ArrayList<Comment> mockComments() {
    ArrayList<Comment> comments = new ArrayList<>();
    Comment comment = new Comment("MockComment", null);
    comments.add(comment);
    return comments;
  }

  // create a mock list of photos
  private ArrayList<Photo> mockPhotos() {
    ArrayList<Photo> photos = new ArrayList<>();
    Photo photo = new Photo((Bitmap) null, null);
    photos.add(photo);
    return photos;
  }

  // create a mock qr code
  private QRCode mockCode() {
    return new QRCode(mockHash);
  }

  @Test
  public void testGetHash() {
    assertEquals(mockCode().getHash(), mockHash);
  }

  @Test
  public void testGetName() {
    assertEquals(mockCode().getName(), "RobaqinectTiger✿");
  }

  @Test
  public void testGetScore() {
    assertEquals((int) mockCode().getScore(), 32);
  }

  @Test
  public void testGetLoc() {
    QRCode qr = new QRCode(mockHash, "RobaqinectTiger✿", 32, mockLoc, null, null, null);
    Location loc = qr.getLoc();
    assertEquals(loc.getClass().toString(), "class android.location.Location");
    assertEquals(System.identityHashCode(loc), System.identityHashCode(mockLoc));
  }

  @Test
  public void testSetLoc() {
    QRCode qr = mockCode();
    qr.setLoc(mockLoc);
    Location loc = qr.getLoc();
    assertEquals(loc.getClass().toString(), "class android.location.Location");
    assertEquals(System.identityHashCode(loc), System.identityHashCode(mockLoc));
  }

  @Test
  public void testGetPlayers() {
    ArrayList<String> players = mockPlayers();
    QRCode qr = new QRCode(mockHash, "RobaqinectTiger✿", 32, mockLoc, null, null, players);
    assertEquals(qr.getPlayers(), players);
  }

  @Test
  public void testSetPlayers() {
    ArrayList<String> players = mockPlayers();
    QRCode qr = mockCode();
    qr.setPlayers(players);
    assertEquals(qr.getPlayers(), players);
  }

  @Test
  public void testAddPlayer() {
    QRCode qr = mockCode();
    assertEquals(0, qr.getPlayers().size());
    String playerID = mockPlayers().get(0);
    qr.addPlayer(playerID);
    assertEquals(1, qr.getPlayers().size());
    assertEquals(qr.getPlayers().get(0), playerID);
  }

  @Test
  public void testDeletePlayer() {
    ArrayList<String> players = mockPlayers();
    QRCode qr = mockCode();
    qr.setPlayers(players);
    assertEquals(1, qr.getPlayers().size());
    qr.deletePlayer(players.get(0));
    assertEquals(0, qr.getPlayers().size());
  }

  @Test
  public void testGetComments() {
    ArrayList<Comment> comments = mockComments();
    QRCode qr = new QRCode(mockHash, "RobaqinectTiger✿", 32, mockLoc, null, comments, null);
    assertEquals(qr.getComments(), comments);
  }

  @Test
  public void testSetComments() {
    ArrayList<Comment> comments = mockComments();
    QRCode qr = mockCode();
    qr.setComments(comments);
    assertEquals(qr.getComments(), comments);
  }

  @Test
  public void testAddComment() {
    QRCode qr = mockCode();
    assertEquals(0, qr.getComments().size());
    Comment comment = mockComments().get(0);
    qr.addComment(comment);
    assertEquals(1, qr.getComments().size());
    assertEquals(qr.getComments().get(0), comment);
  }

  @Test
  public void testDeleteComment() {
    QRCode qr = mockCode();
    ArrayList<Comment> comments = mockComments();
    qr.setComments(comments);
    assertEquals(1, qr.getComments().size());
    qr.deleteComment(comments.get(0));
    assertEquals(0, qr.getComments().size());
  }

  @Test
  public void testGetPhotos() {
    ArrayList<Photo> photos = mockPhotos();
    QRCode qr = new QRCode(mockHash, "RobaqinectTiger✿", 32, mockLoc, photos, null, null);
    assertEquals(qr.getPhotos(), photos);
  }

  @Test
  public void testSetPhotos() {
    ArrayList<Photo> photos = mockPhotos();
    QRCode qr = mockCode();
    qr.setPhotos(photos);
    assertEquals(qr.getPhotos(), photos);
  }

  @Test
  public void testAddPhoto() {
    QRCode qr = mockCode();
    assertEquals(0, qr.getPhotos().size());
    Photo photo = mockPhotos().get(0);
    qr.addPhoto(photo);
    assertEquals(1, qr.getPhotos().size());
    assertEquals(qr.getPhotos().get(0), photo);
  }

  @Test
  public void testDeletePhoto() {
    QRCode qr = mockCode();
    ArrayList<Photo> photos = mockPhotos();
    qr.setPhotos(photos);
    assertEquals(1, qr.getPhotos().size());
    qr.deletePhoto(photos.get(0));
    assertEquals(0, qr.getPhotos().size());
  }
}
