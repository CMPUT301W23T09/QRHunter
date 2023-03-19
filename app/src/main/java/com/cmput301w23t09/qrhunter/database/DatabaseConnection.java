package com.cmput301w23t09.qrhunter.database;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DatabaseConnection {
  private static DatabaseConnection INSTANCE;

  public DatabaseConnection() {}

  public String getCollectionPrefix() {
    return "production_";
  }

  public CollectionReference getCollection(String name) {
    String collectionName = getCollectionPrefix() + name;
    return FirebaseFirestore.getInstance().collection(collectionName);
  }

  public static DatabaseConnection getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new DatabaseConnection();
    }

    return INSTANCE;
  }

  public static void mockInstance(DatabaseConnection connection) {
    INSTANCE = connection;
  }
}
