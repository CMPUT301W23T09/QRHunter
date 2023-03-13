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
import java.util.stream.Collectors;

public class QRCodeDatabase {
  /** Name to associate with any logged messages. */
  private static final String LOGGER_TAG = "QRCodeDatabase";
  /** Firebase collection name to store/retrieve data from. */
  private static final String DATABASE_COLLECTION_NAME = "qrcodes";

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
              } else {
                // No QRCode by that hash exists.
                callback.accept(new DatabaseQueryResults<>(null));
              }
            });
  }

  /**
   * Retrieve a set of QRCodes by their hashes
   *
   * @param hashes the hashes
   * @param callback callback to call on query completion
   */
  public void getQRCodeHashes(Set<String> hashes, DatabaseConsumer<Set<QRCode>> callback) {
    if (hashes.size() == 0) {
      callback.accept(new DatabaseQueryResults<>(new HashSet<>()));
      return;
    }

    collection
        .whereIn("hash", new ArrayList<>(hashes))
        .get()
        .addOnCompleteListener(
            task -> {
              if (!task.isSuccessful()) {
                callback.accept(new DatabaseQueryResults<>(null, task.getException()));
                Log.w(LOGGER_TAG, "Failed to execute getQRCodeHashes", task.getException());
                return;
              }

              // Return found qrcodes if any.
              if (task.getResult() != null) {
                Set<QRCode> qrCodes =
                    task.getResult().getDocuments().stream()
                        .map(this::snapshotToQRCode)
                        .collect(Collectors.toSet());

                callback.accept(new DatabaseQueryResults<>(qrCodes));
              } else {
                // No QRCode by that hash exists.
                callback.accept(new DatabaseQueryResults<>(null));
              }
            });
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
    Long score = (Long) snapshot.get("score");
    if (score == null) {
      // TODO: THIS IS DEBUGGING WHILE WE DON'T HAVE A SCORE ASSOCIATED WITH QR CODES.
      score = Long.valueOf(0);
    }

    List<String> playerIds = (List<String>) snapshot.get("players");
    if (playerIds == null) {
      playerIds = new ArrayList<>();
    }

    String name = (String) snapshot.get("name");

    return new QRCode(hash, name, null, score, null, null, null, playerIds);
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
