package com.cmput301w23t09.qrhunter.player;

import android.util.Log;

import com.cmput301w23t09.qrhunter.database.DatabaseConsumer;
import com.cmput301w23t09.qrhunter.database.DatabaseQueryResults;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerDatabase {
    private static final String LOGGER_TAG = "PlayerDatabase";
    private static final String DATABASE_COLLECTION_NAME = "players";

    private static PlayerDatabase INSTANCE;

    private final CollectionReference collection;

    private PlayerDatabase() {
        collection = FirebaseFirestore.getInstance().collection(DATABASE_COLLECTION_NAME);
    }

    public void add(Player player, DatabaseConsumer<Player> callback) {
        if (player.getDocumentId() != null) {
            throw new IllegalArgumentException("The provided player already has a document reference.");
        }

        collection.add(playerToDBValues(player)).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                callback.accept(new DatabaseQueryResults<>(null, task.getException()));
                return;
            }

            String documentId = task.getResult().getId();
            player.setDocumentId(documentId);

            callback.accept(new DatabaseQueryResults<>(player));
        });
    }

    public void update(Player player, DatabaseConsumer<Void> callback) {
        if (player.getDocumentId() == null) {
            throw new IllegalArgumentException("The provided player does not have a document reference.");
        }

        collection.document(player.getDocumentId()).update(playerToDBValues(player)).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                callback.accept(new DatabaseQueryResults<>(null, task.getException()));
                return;
            }

            // Update was successful
            callback.accept(new DatabaseQueryResults<>(null));
        });
    }

    public void delete(Player player, DatabaseConsumer<Void> callback) {
        if (player.getDocumentId() == null) {
            throw new IllegalArgumentException("The provided player does not have a document reference.");
        }

        collection.document(player.getDocumentId()).delete().addOnCompleteListener(task -> {
           if (!task.isSuccessful()) {
               callback.accept(new DatabaseQueryResults<>(null, task.getException()));
               return;
           }

           // Deletion was successful
           callback.accept(new DatabaseQueryResults<>(null));
        });
    }

    public void getPlayerByDeviceId(UUID deviceUUID, DatabaseConsumer<Player> callback) {
        collection.whereEqualTo("deviceUUID", deviceUUID.toString()).get()
                .addOnCompleteListener(task -> {
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

    public void getPlayerByUsername(String username, DatabaseConsumer<Player> callback) {
        collection.whereEqualTo("username", username).get()
                .addOnCompleteListener(task -> {
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

    private Player snapshotToPlayer(QueryDocumentSnapshot snapshot) {
        String documentId = snapshot.getId();
        UUID deviceUUID = UUID.fromString(snapshot.getString("deviceUUID"));
        String username = snapshot.getString("username");
        String phoneNo = snapshot.getString("phoneNo");
        String email = snapshot.getString("email");

        return new Player(documentId, deviceUUID, username, phoneNo, email);
    }

    private Map<String, Object> playerToDBValues(Player player) {
        Map<String, Object> values = new HashMap<>();
        values.put("deviceUUID", player.getDeviceId().toString());
        values.put("username", player.getUsername());
        values.put("phoneNo", player.getPhoneNo());
        values.put("email", player.getEmail());

        return values;
    }


    public static PlayerDatabase getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerDatabase();
        }

        return INSTANCE;
    }

}
