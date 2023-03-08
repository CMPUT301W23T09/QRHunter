package com.cmput301w23t09.qrhunter.database;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashSet;
import java.util.Set;

public class TestDatabaseConnection extends DatabaseConnection {

  private Set<String> collectionsReferenced = new HashSet<>();

  @Override
  protected String getCollectionPrefix() {
    return "test";
  }

  @Override
  public CollectionReference getCollection(String name) {
    collectionsReferenced.add(name);
    return super.getCollection(name);
  }

  public void cleanUp() {
    try {
      for (String collectionReference : collectionsReferenced) {
        CollectionReference reference =
            FirebaseFirestore.getInstance().collection(getCollectionName(collectionReference));
        QuerySnapshot querySnapshot = Tasks.await(reference.get());

        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
          Tasks.await(documentSnapshot.getReference().delete());
        }
      }
    } catch (Exception exception) {
      throw new IllegalStateException("Failed to clean up test database.");
    }

    collectionsReferenced.clear();
  }
}
