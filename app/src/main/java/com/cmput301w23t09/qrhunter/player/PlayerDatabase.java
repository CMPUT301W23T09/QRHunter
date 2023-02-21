package com.cmput301w23t09.qrhunter.player;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Optional;
import java.util.UUID;

public class PlayerDatabase {
    private static PlayerDatabase INSTANCE;
    private static final String DATABASE_COLLECTION_NAME = "players";

    private final CollectionReference collection;

    private PlayerDatabase() {
        collection = FirebaseFirestore.getInstance().collection(DATABASE_COLLECTION_NAME);
    }

    public void save(Player player) {

    }

    public void delete(Player player) {

    }

    public Player getPlayerByDeviceId(UUID deviceUUID) {
        return null;
    }

    public Player getPlayerByUsername(String username) {
        return null;
    }


    public static PlayerDatabase getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerDatabase();
        }

        return INSTANCE;
    }

}
