package com.cmput301w23t09.qrhunter;

import com.cmput301w23t09.qrhunter.database.DatabaseConsumer;
import com.cmput301w23t09.qrhunter.database.DatabaseQueryResults;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.google.firebase.firestore.CollectionReference;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MockPlayerDatabase extends PlayerDatabase {

  private final Map<String, Player> data = new HashMap<>();

  @Override
  protected CollectionReference getCollection() {
    return null;
  }

  @Override
  public void add(Player player, DatabaseConsumer<Player> callback) {
    if (player.getDocumentId() != null) {
      throw new UnsupportedOperationException();
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
          String documentId = player.getDeviceId().toString();
          player.setDocumentId(documentId);
          data.put(documentId, player);

          callback.accept(new DatabaseQueryResults<>(player));
        });
  }

  @Override
  public void update(Player player, DatabaseConsumer<Void> callback) {
    if (player.getDocumentId() == null) {
      throw new UnsupportedOperationException();
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
          data.put(player.getDocumentId(), player);
          callback.accept(new DatabaseQueryResults<>(null));
        });
  }

  @Override
  public void delete(Player player, DatabaseConsumer<Void> callback) {
    if (player.getDocumentId() == null) {
      throw new UnsupportedOperationException();
    }

    String documentId = player.getDocumentId();
    data.remove(documentId);
    callback.accept(new DatabaseQueryResults<>(null));
  }

  @Override
  public void getPlayerByDeviceId(UUID deviceUUID, DatabaseConsumer<Player> callback) {
    String documentId = deviceUUID.toString();
    callback.accept(new DatabaseQueryResults<>(data.getOrDefault(documentId, null)));
  }

  @Override
  public void getPlayerByUsername(String username, DatabaseConsumer<Player> callback) {
    for (Player player : data.values()) {
      if (player.getUsername().equalsIgnoreCase(username)) {
        callback.accept(new DatabaseQueryResults<>(player));
        return;
      }
    }

    callback.accept(new DatabaseQueryResults<>(null));
  }

  public void reset() {
    data.clear();
  }
}
