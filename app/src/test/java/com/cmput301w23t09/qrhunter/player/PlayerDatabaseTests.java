package com.cmput301w23t09.qrhunter.player;

import static org.junit.jupiter.api.Assertions.*;

import com.cmput301w23t09.qrhunter.database.TestDatabaseConnection;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PlayerDatabaseTests {

  private static TestDatabaseConnection connection;
  private static PlayerDatabase playerDatabase;

  @BeforeAll
  public static void setupDatabasePrefix() {
    connection = new TestDatabaseConnection();
    playerDatabase = new PlayerDatabase(connection);
  }

  @AfterEach
  public void cleanUp() {
    connection.cleanUp();
  }

  @Test
  public void shouldAddPlayerIfNoExistingUsername() {
    Player player =
        new Player(UUID.randomUUID(), "John Doe", "123-456-7890", "example@example.com");
    playerDatabase.add(
        player,
        task -> {
          System.out.println("1823u2132193");
          assertFalse(true);
        });
  }
}
