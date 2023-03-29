package com.cmput301w23t09.qrhunter.player;

import android.util.Log;
import com.cmput301w23t09.qrhunter.database.DatabaseConnection;
import com.cmput301w23t09.qrhunter.database.DatabaseConsumer;
import com.cmput301w23t09.qrhunter.database.DatabaseQueryResults;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Manages all Player database operations. */
public class PlayerDatabase {
  /** Name to associate with any logged messages. */
  private static final String LOGGER_TAG = "PlayerDatabase";
  /** Firebase collection name to store/retrieve data from. */
  private static final String DATABASE_COLLECTION_NAME = "players";

  /** Singleton instance of PlayerDatabase to ensure only one copy is created. */
  private static PlayerDatabase INSTANCE;

  /** Reference to firebase player collection. */
  private final CollectionReference collection;

  protected PlayerDatabase() {
    collection = DatabaseConnection.getInstance().getCollection(DATABASE_COLLECTION_NAME);
  }

  /**
   * Adds a player that does not currently exist in the database to the database.
   *
   * @param player player to add without a database reference id
   * @param callback callback to call once the operation has finished
   * @throws IllegalArgumentException if the player has a document id
   */
  public void add(Player player, DatabaseConsumer<Player> callback) {
    if (player.getDocumentId() != null) {
      throw new IllegalArgumentException("The provided player already has a document reference.");
    }

    // Ensure that player name to add does not already exist.
    getPlayerByUsername(
        player.getUsername(),
        existingUserResults -> {
          if (!existingUserResults.isSuccessful()) {
            callback.accept(new DatabaseQueryResults<>(null, existingUserResults.getException()));
            return;
          }
          if (existingUserResults.getData() != null) {
            callback.accept(
                new DatabaseQueryResults<>(
                    null,
                    new IllegalArgumentException("The provided username is already in use.")));
            return;
          }

          // The username is not in use, add the player.

          collection
              .add(playerToDBValues(player))
              .addOnCompleteListener(
                  task -> {
                    if (!task.isSuccessful()) {
                      callback.accept(new DatabaseQueryResults<>(null, task.getException()));
                      return;
                    }

                    String documentId = task.getResult().getId();
                    player.setDocumentId(documentId);

                    callback.accept(new DatabaseQueryResults<>(player));
                  });
        });
  }

  /**
   * Update a player that already exists in the database.
   *
   * @param player player to add with a database reference id
   * @param callback callback to call once the operation has finished
   * @throws IllegalArgumentException if the player does not have a document id
   */
  public void update(Player player, DatabaseConsumer<Void> callback) {
    if (player.getDocumentId() == null) {
      throw new IllegalArgumentException("The provided player does not have a document reference.");
    }

    // Ensure that player name to update does not already exist.
    getPlayerByUsername(
        player.getUsername(),
        existingUserResults -> {
          if (!existingUserResults.isSuccessful()) {
            callback.accept(new DatabaseQueryResults<>(null, existingUserResults.getException()));
            return;
          }

          if (existingUserResults.getData() != null) {
            // Only throw a username conflict error if the found user is DIFFERENT from the user
            // we're updating.
            boolean isDifferentDocument =
                !existingUserResults.getData().getDocumentId().equals(player.getDocumentId());
            if (isDifferentDocument) {
              callback.accept(
                  new DatabaseQueryResults<>(
                      null,
                      new IllegalArgumentException("The provided username is already in use.")));
              return;
            }
          }

          // It is safe to update the player.
          collection
              .document(player.getDocumentId())
              .update(playerToDBValues(player))
              .addOnCompleteListener(
                  task -> {
                    if (!task.isSuccessful()) {
                      callback.accept(new DatabaseQueryResults<>(null, task.getException()));
                      return;
                    }

                    // Update was successful
                    callback.accept(new DatabaseQueryResults<>(null));
                  });
        });
  }

  /**
   * Delete a player from the database
   *
   * @param player player with a database reference id to delete
   * @param callback callback to call once the operation has finished
   * @throws IllegalArgumentException if the player does not have a document id
   */
  public void delete(Player player, DatabaseConsumer<Void> callback) {
    if (player.getDocumentId() == null) {
      throw new IllegalArgumentException("The provided player does not have a document reference.");
    }

    collection
        .document(player.getDocumentId())
        .delete()
        .addOnCompleteListener(
            task -> {
              if (!task.isSuccessful()) {
                callback.accept(new DatabaseQueryResults<>(null, task.getException()));
                return;
              }

              // Deletion was successful
              callback.accept(new DatabaseQueryResults<>(null));
            });
  }

  /**
   * Retrieve a player by their device UUID from the database
   *
   * @param deviceUUID device UUID to lookup
   * @param callback callback to call once the operation has finished
   */
  public void getPlayerByDeviceId(UUID deviceUUID, DatabaseConsumer<Player> callback) {
    collection
        .whereEqualTo("deviceUUID", deviceUUID.toString())
        .get()
        .addOnCompleteListener(
            task -> {
              if (!task.isSuccessful()) {
                callback.accept(new DatabaseQueryResults<>(null, task.getException()));
                return;
              }

              // Return found player if any.
              for (QueryDocumentSnapshot snapshot : task.getResult()) {
                callback.accept(new DatabaseQueryResults<>(snapshotToPlayer(snapshot)));
                return;
              }

              // No player by the device id exists.
              callback.accept(new DatabaseQueryResults<>(null));
            });
  }

  public void getPlayerByDocumentId(String documentId, DatabaseConsumer<Player> callback) {
    collection
        .document(documentId)
        .get()
        .addOnCompleteListener(
            task -> {
              if (!task.isSuccessful()) {
                callback.accept(new DatabaseQueryResults<>(null, task.getException()));
                return;
              }

              // Return found player if any.
              if (task.getResult() != null) {
                callback.accept(new DatabaseQueryResults<>(snapshotToPlayer(task.getResult())));
                return;
              }

              // No player by the device id exists.
              callback.accept(new DatabaseQueryResults<>(null));
            });
  }

  /**
   * Retrieve a player by their username from the database
   *
   * @param username username to lookup
   * @param callback callback to call once the operation has finished
   */
  public void getPlayerByUsername(String username, DatabaseConsumer<Player> callback) {
    collection
        .whereEqualTo("username_lower", username.toLowerCase())
        .get()
        .addOnCompleteListener(
            task -> {
              if (!task.isSuccessful()) {
                callback.accept(new DatabaseQueryResults<>(null, task.getException()));
                Log.d(LOGGER_TAG, "Failed to execute getPlayerByUsername", task.getException());
                return;
              }

              // Return found player if any.
              for (QueryDocumentSnapshot snapshot : task.getResult()) {
                callback.accept(new DatabaseQueryResults<>(snapshotToPlayer(snapshot)));
                return;
              }

              // No player by the username exists.
              callback.accept(new DatabaseQueryResults<>(null));
            });
  }

  /**
   * Retrieve all players from the database
   *
   * @param callback callback to call once the operation has finished
   */
  public void getAllPlayers(DatabaseConsumer<Set<Player>> callback) {
    collection
        .get()
        .addOnCompleteListener(
            task -> {
              if (!task.isSuccessful()) {
                callback.accept(new DatabaseQueryResults<>(null, task.getException()));
                return;
              }

              // Convert all snapshots to Players
              Set<Player> players = new HashSet<>();
              for (QueryDocumentSnapshot snapshot : task.getResult()) {
                players.add(snapshotToPlayer(snapshot));
              }

              callback.accept(new DatabaseQueryResults<>(players));
            });
  }

  /**
   * Converts a database snapshot to its Player object equivalent.
   *
   * @param snapshot database snapshot
   * @return Player object
   */
  private Player snapshotToPlayer(DocumentSnapshot snapshot) {
    String documentId = snapshot.getId();
    UUID deviceUUID = UUID.fromString(snapshot.getString("deviceUUID"));
    String username = snapshot.getString("username");
    String phoneNo = snapshot.getString("phoneNo");
    String email = snapshot.getString("email");
    ArrayList<String> qrCodeHashes = (ArrayList<String>) snapshot.get("qrCodeHashes");

    return new Player(
        documentId, deviceUUID, username, phoneNo, email, new ArrayList<>(qrCodeHashes));
  }

  /**
   * Converts a Player object to database insertable values.
   *
   * @param player player object
   * @return a map of database insertable values
   */
  private Map<String, Object> playerToDBValues(Player player) {
    Map<String, Object> values = new HashMap<>();
    values.put("deviceUUID", player.getDeviceId().toString());
    values.put("username", player.getUsername());
    values.put("username_lower", player.getUsername().toLowerCase());
    values.put("phoneNo", player.getPhoneNo());
    values.put("email", player.getEmail());
    values.put("qrCodeHashes", player.getQRCodeHashes());

    return values;
  }

  /**
   * Retrieves the PlayerDatabase
   *
   * @return PlayerDatabase
   */
  public static PlayerDatabase getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new PlayerDatabase();
    }

    return INSTANCE;
  }

  /**
   * Sets instance to a mocked PlayerDatabase for testing purposes.
   *
   * @param mockPlayerDB The mocked PlayerDatabase to use.
   */
  public static void mockInstance(PlayerDatabase mockPlayerDB) {
    INSTANCE = mockPlayerDB;
  }
}
