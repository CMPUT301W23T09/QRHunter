package com.cmput301w23t09.qrhunter.player;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.cmput301w23t09.qrhunter.database.DatabaseConnection;
import com.cmput301w23t09.qrhunter.database.MockDBUtils;
import com.google.firebase.firestore.CollectionReference;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlayerDatabaseTests {

  private PlayerDatabase database;

  @BeforeEach
  public void resetDatabase() {
    DatabaseConnection connection = mock(DatabaseConnection.class);
    CollectionReference mockRef = MockDBUtils.makeCollection();
    when(connection.getCollection(anyString())).thenReturn(mockRef);

    database = new PlayerDatabase(connection);
  }

  @Test
  public void shouldAddPlayerIfNoExistingUsername() {
    Player player =
        new Player(UUID.randomUUID(), "John Doe", "123-456-7890", "example@example.com");

    // The player show be addable since it has no document id.
    assertDoesNotThrow(
        () ->
            database.add(
                player,
                task -> {
                  // There should be no problem with adding a player to the database.
                  assertNull(
                      task.getException(),
                      "There was an exception when adding the player to the database");

                  // Player should now have a document id.
                  assertNotNull(
                      player.getDocumentId(), "Document id was not assigned to player object.");

                  // Ensure player exists in the database now.
                  database.getPlayerByUsername(
                      player.getUsername(),
                      subTask -> {
                        assertNotNull(subTask.getData(), "Player was not added to the database.");
                        assertEquals(
                            player.getUsername(),
                            subTask.getData().getUsername(),
                            "Wrong player was added to the database.");
                      });
                }),
        "Null document id check failed.");
  }

  @Test
  public void shouldPreventAddingPlayerIfUsernameInUse() {
    Player existingPlayer =
        new Player(UUID.randomUUID(), "John Doe", "123-456-7890", "example@example.com");
    Player player =
        new Player(UUID.randomUUID(), "John Doe", "234-567-8901", "example2@example.com");

    // Add the existing player and try and add the second player who shares the same username.
    // it should throw an exception.
    database.add(
        existingPlayer,
        ignored -> {
          database.add(
              player,
              task -> {
                assertNotNull(task.getException(), "Duplicate username was added to database.");
              });
        });
  }
}
