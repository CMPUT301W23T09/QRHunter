package com.cmput301w23t09.qrhunter.qrcode;

import android.util.Log;
import com.cmput301w23t09.qrhunter.database.DatabaseConsumer;
import com.cmput301w23t09.qrhunter.database.DatabaseQueryResults;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class QRCodeDatabase {
  /** Name to associate with any logged messages. */
  private static final String LOGGER_TAG = "QRCodeDatabase";
  /** Firebase collection name to store/retrieve data from. */
  private static final String DATABASE_COLLECTION_NAME = "players";

  /** Singleton instance of QRCodeDatabase to ensure only one copy is created. */
  private static QRCodeDatabase INSTANCE;

  /** Reference to firebase qrcode collection. */
  private final CollectionReference collection;

  private QRCodeDatabase() {
    collection = FirebaseFirestore.getInstance().collection(DATABASE_COLLECTION_NAME);
  }

  /**
   * Retrieve a QRCode by it's hash
   *
   * @param hash the hash
   * @param callback callback to call with result
   */
  public void getQRCodeByHash(String hash, DatabaseConsumer<QRCode> callback) {
    collection
        .document(hash)
        .get()
        .addOnCompleteListener(
            task -> {
              if (!task.isSuccessful()) {
                callback.accept(new DatabaseQueryResults<>(null, task.getException()));
                Log.w(LOGGER_TAG, "Failed to execute getQRCodeByHash", task.getException());
                return;
              }

              // Return found qrcode if any.
              if (task.getResult() != null) {
                callback.accept(new DatabaseQueryResults<>(snapshotToQRCode(task.getResult())));
              }

              // No QRCode by that hash exists.
              callback.accept(new DatabaseQueryResults<>(null));
            });
  }

  /**
   * Retrieve a set of QRCodes by their hashes
   *
   * @param hashes the hashes
   * @param callback callback to call on query completion
   */
  public void getQRCodeHashes(Set<String> hashes, DatabaseConsumer<Set<QRCode>> callback) {
    AtomicInteger entriesLeft = new AtomicInteger(hashes.size());
    AtomicReference<Exception> exception = new AtomicReference<>();
    Set<QRCode> qrCodes = new HashSet<>();

    for (String hash : hashes) {
      getQRCodeByHash(
          hash,
          task -> {
            if (task.isSuccessful()) {
              QRCode qrCode = task.getData();

              if (qrCode != null) {
                qrCodes.add(qrCode);
              }
            } else {
              exception.set(task.getException());
            }
          });

      if (entriesLeft.getAndDecrement() == 0) {
        DatabaseQueryResults<Set<QRCode>> query;
        if (exception.get() != null) {
          query = new DatabaseQueryResults<>(qrCodes);
        } else {
          query = new DatabaseQueryResults<>(null, exception.get());
        }

        callback.accept(query);
      }
    }
  }

  /**
   * Retrieve all QRCodes stored in the database.
   *
   * @param callback callback to call with the qr codes.
   */
  public void getAllQRCodes(DatabaseConsumer<Set<QRCode>> callback) {
    collection
        .get()
        .addOnCompleteListener(
            task -> {
              if (!task.isSuccessful()) {
                callback.accept(new DatabaseQueryResults<>(null, task.getException()));
                return;
              }

              // Convert all snapshots to QRCodes.
              Set<QRCode> qrCodes = new HashSet<>();
              for (QueryDocumentSnapshot snapshot : task.getResult()) {
                qrCodes.add(snapshotToQRCode(snapshot));
              }

              callback.accept(new DatabaseQueryResults<>(qrCodes));
            });
  }

  private QRCode snapshotToQRCode(DocumentSnapshot snapshot) {
    String hash = snapshot.getId();
    Long score = snapshot.getLong("score");
    Set<String> playerDocumentIds =
        new HashSet<>((List<String>) snapshot.get("players", ArrayList.class));

    return new QRCode(hash, null, null, score, null, null, null, playerDocumentIds);
  }

  /**
   * Retrieves the QRCodeDatabase
   *
   * @return QRCodeDatabase
   */
  public static QRCodeDatabase getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new QRCodeDatabase();
    }

    return INSTANCE;
  }
}
