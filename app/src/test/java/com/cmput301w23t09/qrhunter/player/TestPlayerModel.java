package com.cmput301w23t09.qrhunter.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestPlayerModel {

  private Player mockPlayer;
  private UUID mockPlayerUUID;

  @BeforeEach
  private void setUp() {
    // Initial values don't matter as we'll be testing each getter/setter separately.
    mockPlayerUUID = UUID.randomUUID();
    mockPlayer = new Player("001", mockPlayerUUID, "johndoe42", "7801234567", "doe@ualberta.ca");
  }

  @Test
  public void testGetters() {
    assertEquals("001", mockPlayer.getDocumentId());
    assertEquals(mockPlayerUUID, mockPlayer.getDeviceId());
    assertEquals("johndoe42", mockPlayer.getUsername());
    assertEquals("7801234567", mockPlayer.getPhoneNo());
    assertEquals("doe@ualberta.ca", mockPlayer.getEmail());
  }

  /* SETTER TESTS*/
  @Test
  public void testSetDocumentId() {
    mockPlayer.setDocumentId("002");
    assertNotEquals("001", mockPlayer.getDocumentId());
    assertEquals("002", mockPlayer.getDocumentId());
  }

  @Test
  public void testSetDeviceId() {
    UUID newUUID = UUID.randomUUID();
    while (newUUID.equals(mockPlayerUUID)) newUUID = UUID.randomUUID();
    mockPlayer.setDeviceId(newUUID);
    assertNotEquals(mockPlayerUUID, mockPlayer.getDeviceId());
    assertEquals(newUUID, mockPlayer.getDeviceId());
  }

  @Test
  public void testSetValidUsername() {
    String newUsername = "janedoe115";
    mockPlayer.setUsername(newUsername);
    assertNotEquals("johndoe42", mockPlayer.getUsername());
    assertEquals("janedoe115", mockPlayer.getUsername());
  }

  @Test
  public void testSetTooLongUsername() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          // Usernames can be up-to 20 chars...
          mockPlayer.setUsername("jack_tacktheritrix102"); // ...this one has 21 chars
        });
  }

  @Test
  public void testSetBlankUsername() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          // Usernames can't be blank
          mockPlayer.setUsername("");
        });
  }
}
