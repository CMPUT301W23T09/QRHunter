package com.cmput301w23t09.qrhunter.database;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/** Intermediate class that handles fetching firebase collections */
public class DatabaseConnection {
  private static DatabaseConnection INSTANCE;

  protected String getCollectionPrefix() {
    return "production";
  }

  protected String getCollectionName(String subName) {
    return getCollectionPrefix() + "_" + subName;
  }

  /**
   * Retrieve a cached firebase collection.
   *
   * @param name collection name
   * @return collection
   */
  public CollectionReference getCollection(String name) {
    return FirebaseFirestore.getInstance().collection(getCollectionName(name));
  }

  /**
   * Retrieve an instance of the database connection.
   *
   * @return database connection
   */
  public static DatabaseConnection getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new DatabaseConnection();
    }
    return INSTANCE;
  }
}
