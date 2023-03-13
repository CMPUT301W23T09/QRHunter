package com.cmput301w23t09.qrhunter.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import android.telephony.PhoneNumberUtils;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

public class TestPlayerModel {

  private Player mockPlayer;
  private UUID mockPlayerUUID;

  @BeforeEach
  private void setUp() {
    // Initial values don't matter as we'll be testing each getter/setter separately.
    mockPlayerUUID = UUID.randomUUID();
    mockPlayer =
        new Player(
            "001", mockPlayerUUID, "johndoe42", "7801234567", "doe@ualberta.ca", new ArrayList<>());
  }

  @Test
  public void testGetters() {
    assertEquals("001", mockPlayer.getDocumentId());
    assertEquals(mockPlayerUUID, mockPlayer.getDeviceId());
    assertEquals("johndoe42", mockPlayer.getUsername());
    assertEquals("7801234567", mockPlayer.getPhoneNo());
    assertEquals("doe@ualberta.ca", mockPlayer.getEmail());
    assertEquals(new ArrayList<>(), mockPlayer.getQRCodeHashes());
  }

  @Test
  public void testGetQRCodeHashes() {
    ArrayList<String> scannedQRHashes = new ArrayList<>();
    scannedQRHashes.add("hash1");
    scannedQRHashes.add("hash2");
    mockPlayer.setQRCodeHashes(scannedQRHashes);
    assertEquals(scannedQRHashes, mockPlayer.getQRCodeHashes());
  }

  /* SETTER TESTS*/
  @Test
  public void testSetDocumentId() {
    mockPlayer.setDocumentId("002");
    assertEquals("002", mockPlayer.getDocumentId());
  }

  @Test
  public void testSetDeviceId() {
    UUID newUUID = UUID.randomUUID();
    while (newUUID.equals(mockPlayerUUID)) newUUID = UUID.randomUUID();
    mockPlayer.setDeviceId(newUUID);
    assertEquals(newUUID, mockPlayer.getDeviceId());
  }

  @Test
  public void testSetValidUsername() {
    mockPlayer.setUsername("janedoe115");
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

  // For the following phone number tests, assume that
  // android.telephonyPhoneNumberUtils.isGlobalPhoneNumber
  // returns the proper value.
  @Test
  public void testSetValidPhoneNumber() {
    try (MockedStatic mocked = mockStatic(PhoneNumberUtils.class)) {
      when(PhoneNumberUtils.isGlobalPhoneNumber("7804923111")).thenReturn(true);
      mockPlayer.setPhoneNo("7804923111");
      assertEquals("7804923111", mockPlayer.getPhoneNo());
    }
  }

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

  @Test
  public void testSetValidEmailPlusAddressing() {
    mockPlayer.setEmail("smith+spambox@ualberta.ca");
    assertEquals("smith+spambox@ualberta.ca", mockPlayer.getEmail());
  }

  @Test
  public void testSetValidEmailHyphenated() {
    mockPlayer.setEmail("Smith123@yah-oo.com");
    assertEquals("Smith123@yah-oo.com", mockPlayer.getEmail());
    mockPlayer.setEmail("smith-123@gmail.com");
    assertEquals("smith-123@gmail.com", mockPlayer.getEmail());
  }

  @Test
  public void testSetInvalidEmailHyphenated1() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          mockPlayer.setEmail("Smith123@-yahoo.com");
        });
  }

  @Test
  public void testSetInvalidEmailHyphenated2() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          mockPlayer.setEmail("Smith123@yahoo-.com");
        });
  }

  @Test
  public void testSetInvalidEmailNoDomain() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          mockPlayer.setEmail("smith");
        });
  }

  @Test
  public void testSetInvalidEmailIncompleteDomain() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          mockPlayer.setEmail("smith@gmail");
        });
  }

  @Test
  public void testSetInvalidEmailNoName() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          mockPlayer.setEmail("@gmail.com");
        });
  }

  @Test
  public void testSetBlankEmail() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          mockPlayer.setEmail("");
        });
  }

  @Test
  public void testSetQRCodeHashes() {
    ArrayList<String> scannedQRHashes = new ArrayList<>();
    scannedQRHashes.add("hash1");
    scannedQRHashes.add("hash2");
    scannedQRHashes.add("hash3");
    mockPlayer.setQRCodeHashes(scannedQRHashes);
    assertEquals(scannedQRHashes, mockPlayer.getQRCodeHashes());
  }

  @Test
  public void testSetQRCodeHashesNullList() {
    assertThrows(
        NullPointerException.class,
        () -> {
          mockPlayer.setQRCodeHashes(null);
        });
    assertEquals(new ArrayList<>(), mockPlayer.getQRCodeHashes());
  }
}
