package com.cmput301w23t09.qrhunter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import android.telephony.PhoneNumberUtils;
import com.cmput301w23t09.qrhunter.player.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/** This tests the methods of the Player class */
public class TestPlayerModel {
  /** This is a mock player */
  private Player mockPlayer;
  /** This is a mock device ID */
  private UUID mockPlayerUUID;

  /** Set up for tests by creating a mock player and ID */
  @BeforeEach
  public void setUp() {
    // Initial values don't matter as we'll be testing each getter/setter separately.
    mockPlayerUUID = UUID.randomUUID();
    mockPlayer =
        new Player(
            "001", mockPlayerUUID, "johndoe42", "7801234567", "doe@ualberta.ca", new ArrayList<>());
  }

  /** Test the getters of the Player class */
  @Test
  public void testGetters() {
    assertEquals("001", mockPlayer.getDocumentId());
    assertEquals(mockPlayerUUID, mockPlayer.getDeviceId());
    assertEquals("johndoe42", mockPlayer.getUsername());
    assertEquals("7801234567", mockPlayer.getPhoneNo());
    assertEquals("doe@ualberta.ca", mockPlayer.getEmail());
    assertEquals(new ArrayList<>(), mockPlayer.getQRCodeHashes());
  }

  /** Test the getter for the qr code hashes after hashes have been added */
  @Test
  public void testGetQRCodeHashes() {
    List<String> scannedQRHashes = new ArrayList<>();
    scannedQRHashes.add("hash1");
    scannedQRHashes.add("hash2");
    mockPlayer.setQRCodeHashes(scannedQRHashes);
    assertEquals(scannedQRHashes, mockPlayer.getQRCodeHashes());
  }

  /** Test the setter for the document ID of a Player */
  @Test
  public void testSetDocumentId() {
    mockPlayer.setDocumentId("002");
    assertEquals("002", mockPlayer.getDocumentId());
  }

  /** Test the setter for the device ID of a Player */
  @Test
  public void testSetDeviceId() {
    UUID newUUID = UUID.randomUUID();
    while (newUUID.equals(mockPlayerUUID)) newUUID = UUID.randomUUID();
    mockPlayer.setDeviceId(newUUID);
    assertEquals(newUUID, mockPlayer.getDeviceId());
  }

  /** Test setting a valid username for a Player */
  @Test
  public void testSetValidUsername() {
    mockPlayer.setUsername("janedoe115");
    assertEquals("janedoe115", mockPlayer.getUsername());
  }

  /** Test setting a username that is too long for a Player */
  @Test
  public void testSetTooLongUsername() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          // Usernames can be up-to 20 chars...
          mockPlayer.setUsername("jack_tacktheritrix102"); // ...this one has 21 chars
        });
  }

  /** Test setting a blank username for a Player */
  @Test
  public void testSetBlankUsername() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          // Usernames can't be blank
          mockPlayer.setUsername("");
        });
  }

  // For the following phone number tests, assume that
  // android.telephonyPhoneNumberUtils.isGlobalPhoneNumber
  // returns the proper value.
  /** Test setting a valid phone number for a Player */
  @Test
  public void testSetValidPhoneNumber() {
    try (MockedStatic mocked = mockStatic(PhoneNumberUtils.class)) {
      when(PhoneNumberUtils.isGlobalPhoneNumber("7804923111")).thenReturn(true);
      mockPlayer.setPhoneNo("7804923111");
      assertEquals("7804923111", mockPlayer.getPhoneNo());
    }
  }

  /** Test setting an invalid phone number for a Player */
  @Test
  public void testSetInvalidPhoneNumber() {
    try (MockedStatic mocked = mockStatic(PhoneNumberUtils.class)) {
      when(PhoneNumberUtils.isGlobalPhoneNumber("abc")).thenReturn(false);
      assertThrows(
          IllegalArgumentException.class,
          () -> {
            mockPlayer.setPhoneNo("abc");
          });
    }
  }

  /** Test setting a blank phone number for a Player */
  @Test
  public void testSetBlankPhoneNumber() {
    try (MockedStatic mocked = mockStatic(PhoneNumberUtils.class)) {
      when(PhoneNumberUtils.isGlobalPhoneNumber("")).thenReturn(false);
      assertThrows(
          IllegalArgumentException.class,
          () -> {
            mockPlayer.setPhoneNo("");
          });
    }
  }

  /** Test setting a valid email for a Player */
  @Test
  public void testSetValidEmail() {
    mockPlayer.setEmail("smith@ualberta.ca");
    assertEquals("smith@ualberta.ca", mockPlayer.getEmail());
    mockPlayer.setEmail("Smith123@yahoo.com");
    assertEquals("Smith123@yahoo.com", mockPlayer.getEmail());
    mockPlayer.setEmail("smith_123@ualberta.ca");
    assertEquals("smith_123@ualberta.ca", mockPlayer.getEmail());
    mockPlayer.setEmail("smith.123@ualberta.ca");
    assertEquals("smith.123@ualberta.ca", mockPlayer.getEmail());
  }

  /** Test setting a valid email that has addressing for a Player */
  @Test
  public void testSetValidEmailPlusAddressing() {
    mockPlayer.setEmail("smith+spambox@ualberta.ca");
    assertEquals("smith+spambox@ualberta.ca", mockPlayer.getEmail());
  }

  /** Test setting a valid email that has hyphens for a Player */
  @Test
  public void testSetValidEmailHyphenated() {
    mockPlayer.setEmail("Smith123@yah-oo.com");
    assertEquals("Smith123@yah-oo.com", mockPlayer.getEmail());
    mockPlayer.setEmail("smith-123@gmail.com");
    assertEquals("smith-123@gmail.com", mockPlayer.getEmail());
  }

  /** Test setting an invalid email that has hyphens after the @ for a Player */
  @Test
  public void testSetInvalidEmailHyphenated1() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          mockPlayer.setEmail("Smith123@-yahoo.com");
        });
  }

  /** Test setting an invalid email that has hyphens before the domain for a Player */
  @Test
  public void testSetInvalidEmailHyphenated2() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          mockPlayer.setEmail("Smith123@yahoo-.com");
        });
  }

  /** Test setting an invalid email that has a domain for a Player */
  @Test
  public void testSetInvalidEmailNoDomain() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          mockPlayer.setEmail("smith");
        });
  }

  /** Test setting an invalid email that has an incomplete domain for a Player */
  @Test
  public void testSetInvalidEmailIncompleteDomain() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          mockPlayer.setEmail("smith@gmail");
        });
  }

  /** Test setting an invalid email that has no name for a Player */
  @Test
  public void testSetInvalidEmailNoName() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          mockPlayer.setEmail("@gmail.com");
        });
  }

  /** Test setting an blank email for a Player */
  @Test
  public void testSetBlankEmail() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          mockPlayer.setEmail("");
        });
  }

  /** Tests the setter for qr code hashes */
  @Test
  public void testSetQRCodeHashes() {
    List<String> scannedQRHashes = new ArrayList<>();
    scannedQRHashes.add("hash1");
    scannedQRHashes.add("hash2");
    scannedQRHashes.add("hash3");
    mockPlayer.setQRCodeHashes(scannedQRHashes);
    assertEquals(scannedQRHashes, mockPlayer.getQRCodeHashes());
  }
}
