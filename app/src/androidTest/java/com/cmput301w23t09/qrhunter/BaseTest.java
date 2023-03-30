package com.cmput301w23t09.qrhunter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.cmput301w23t09.qrhunter.database.DatabaseConnection;
import com.cmput301w23t09.qrhunter.locationphoto.LocationPhotoStorage;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.junit.After;
import org.junit.BeforeClass;

/** Base class that implements destruction of the database after each test is completed. */
public abstract class BaseTest {

  private static final String COLLECTION_PREFIX = "test" + Math.random() + "_";
  private static boolean initialized = false;
  private static final Set<String> collectionsToReset = new HashSet<>();
  private static final Set<StorageReference> foldersToDelete = new HashSet<>();

  @BeforeClass
  public static void setupDatabase() throws Exception {
    initialize();
  }

  /**
   * Mocks the DatabaseConnection to know what collections from the real firebase database we need
   * to clear after each test.
   */
  protected static void initialize() {
    if (!initialized) {
      initialized = true;

      // Spy on the database connection so that we can tell what collections are being retrieved.
      DatabaseConnection testConnection = spy(new DatabaseConnection());
      when(testConnection.getCollectionPrefix()).thenReturn(COLLECTION_PREFIX);
      when(testConnection.getCollection(anyString()))
          .thenAnswer(
              invocation -> {
                // Add the collection retrieved to the collections to reset
                collectionsToReset.add(invocation.getArgument(0));

                // Call the actual getCollection method to retrieve the real firebase collection
                return invocation.callRealMethod();
              });

      DatabaseConnection.mockInstance(testConnection);

      // Do the same above for location photos in Cloud Storage
      LocationPhotoStorage locationPhotoStorage = spy(new LocationPhotoStorage());
      when(locationPhotoStorage.getPrefix()).thenReturn(COLLECTION_PREFIX);
      when(locationPhotoStorage.getQRCodeRef(any(QRCode.class)))
          .thenAnswer(
              invocation -> {
                QRCode qrCode = (QRCode) invocation.getArgument(0);
                foldersToDelete.add(
                    FirebaseStorage.getInstance()
                        .getReference()
                        .child(COLLECTION_PREFIX + qrCode.getHash() + "/"));
                return invocation.callRealMethod();
              });
      LocationPhotoStorage.mockInstance(locationPhotoStorage);
    }
  }

  @After
  public void afterEachTest() throws Exception {
    deleteAllCollections();
  }

  /**
   * Deletes all collections accessed in the DatabaseConnection
   *
   * @throws InterruptedException if an exception occurred while deleting the collections
   * @throws ExecutionException if an exception occurred while deleting the collections
   */
  private void deleteAllCollections() throws InterruptedException, ExecutionException {
    for (String collectionName : collectionsToReset) {
      CollectionReference collection =
          DatabaseConnection.getInstance().getCollection(collectionName);

      QuerySnapshot snapshot = Tasks.await(collection.get());
      for (DocumentSnapshot documentSnapshot : snapshot) {
        Tasks.await(collection.document(documentSnapshot.getId()).delete());
      }
    }

    for (StorageReference folder : foldersToDelete) {
      folder
          .listAll()
          .addOnSuccessListener(
              listResult -> {
                for (StorageReference file : listResult.getItems()) {
                  file.delete();
                }
              });
    }
  }
}
