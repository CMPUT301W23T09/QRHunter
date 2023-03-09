package com.cmput301w23t09.qrhunter.player;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.cmput301w23t09.qrhunter.database.DatabaseConnection;
import com.cmput301w23t09.qrhunter.database.MockDBUtils;
import com.google.firebase.firestore.CollectionReference;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class PlayerDatabaseTests {

  @Test
  public void shouldAddPlayerIfNoExistingUsername() {
    Player player =
        new Player(UUID.randomUUID(), "John Doe", "123-456-7890", "example@example.com");

    // Setup mock collection and database
    DatabaseConnection connection = mock(DatabaseConnection.class);
    CollectionReference mockRef = MockDBUtils.makeCollection();
    when(connection.getCollection(anyString())).thenReturn(mockRef);
    PlayerDatabase playerDatabase = new PlayerDatabase(connection);

    // The player show be addable since it has no document id.
    assertDoesNotThrow(
        () ->
            playerDatabase.add(
                player,
                task -> {
                  // There should be no problem with adding a player to the database.
                  assertNull(
                      task.getException(),
                      "There was an exception when adding the player to the database");

                  // Player should now have a document id.
                  assertNotNull(
                      player.getDocumentId(), "Document id was not assigned to player object.");
                }),
        "Null document id check failed.");
  }
}
