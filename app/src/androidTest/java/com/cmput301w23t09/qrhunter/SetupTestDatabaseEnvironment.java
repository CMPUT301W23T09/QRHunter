package com.cmput301w23t09.qrhunter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.cmput301w23t09.qrhunter.database.DatabaseConnection;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/** Used to automatically setup the test database prefix for all tests. */
public class SetupTestDatabaseEnvironment
    implements BeforeAllCallback, AfterEachCallback, ExtensionContext.Store.CloseableResource {

  private static final String COLLECTION_PREFIX = "test_";
  private boolean initialized = false;
  private final Set<String> collectionsToReset = new HashSet<>();

  @Override
  public void beforeAll(ExtensionContext context) {
    if (!initialized) {
      initialized = true;

      DatabaseConnection testConnection = spy(new DatabaseConnection());
      when(testConnection.getCollectionPrefix()).thenReturn(COLLECTION_PREFIX);
      when(testConnection.getCollection(anyString()))
          .thenAnswer(
              invocation -> {
                collectionsToReset.add(invocation.getArgument(0));
                return invocation.callRealMethod();
              });

      DatabaseConnection.mockInstance(testConnection);
    }
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    deleteAllCollections();
  }

  @Override
  public void close() throws Throwable {
    deleteAllCollections();
  }

  private void deleteAllCollections() throws InterruptedException, ExecutionException {
    for (String collectionName : collectionsToReset) {
      CollectionReference collection =
          DatabaseConnection.getInstance().getCollection(collectionName);

      QuerySnapshot snapshot = Tasks.await(collection.get());
      for (DocumentSnapshot documentSnapshot : snapshot) {
        Tasks.await(collection.document(documentSnapshot.getId()).delete());
      }
    }

    collectionsToReset.clear();
  }
}
