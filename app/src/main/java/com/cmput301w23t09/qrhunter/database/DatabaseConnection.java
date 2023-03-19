package com.cmput301w23t09.qrhunter.database;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DatabaseConnection {
  private static final String COLLECTION_PREFIX = "production_";
  private static DatabaseConnection INSTANCE;

  public DatabaseConnection() {}

  /**
   * All collections have a prefix describing their current environment.
   *
   * @return collection prefix
   */
  public String getCollectionPrefix() {
    return COLLECTION_PREFIX;
  }

  /**
   * Retrieve a firebase collection prefixed by the collection prefix
   *
   * @param name name of the collection
   * @return collection
   */
  public CollectionReference getCollection(String name) {
    String collectionName = getCollectionPrefix() + name;
    return FirebaseFirestore.getInstance().collection(collectionName);
  }

  /**
   * Retrieve the instance of the DatabaseConnection
   *
   * @return database connection
   */
  public static DatabaseConnection getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new DatabaseConnection();
    }

    return INSTANCE;
  }

  /**
   * Update the instance of the database connection
   *
   * @param connection database connection
   */
  public static void mockInstance(DatabaseConnection connection) {
    INSTANCE = connection;
  }
}
