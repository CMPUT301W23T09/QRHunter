package com.cmput301w23t09.qrhunter.qrcode;

import android.location.Location;
import android.util.Log;
import androidx.annotation.Nullable;
import com.cmput301w23t09.qrhunter.DatabaseChangeListener;
import com.cmput301w23t09.qrhunter.database.DatabaseConnection;
import com.cmput301w23t09.qrhunter.database.DatabaseConsumer;
import com.cmput301w23t09.qrhunter.database.DatabaseQueryResults;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Manages all QRCode-related database operations, and ensures that the collection of QRCodes that a
 * each player has scanned is consistent with the collection of players that have scanned a specific
 * QR code.
 */
public class QRCodeDatabase {
  private static final String LOGGER_TAG = "QRCodeDatabase";
  private static final String DATABASE_COLLECTION_NAME = "qrcodes";

  /** Singleton instance of QRCodeDatabase to ensure only one copy is created. */
  private static QRCodeDatabase INSTANCE;

  /** Reference to the QRCode collection on firebase */
  private final CollectionReference collection;

  private QRCodeDatabase() {
    collection = DatabaseConnection.getInstance().getCollection(DATABASE_COLLECTION_NAME);
  }

  /**
   * Retrieves the QRCodeDatabase
   *
   * @return QRCodeDatabase
   */
  public static QRCodeDatabase getInstance() {
    if (INSTANCE == null) INSTANCE = new QRCodeDatabase();
    return INSTANCE;
  }

  /**
   * Manually set QRCodeDatabase to some mocked instance for testing
   *
   * @param mockInstance The mocked QRCodeDatabase
   */
  public static void mockInstance(QRCodeDatabase mockInstance) {
    INSTANCE = mockInstance;
  }

  /**
   * Retrieve a QRCode by its has from the database
   *
   * @param hash QRCode hash to lookup
   * @param callback The callback function to handle result
   */
  public void getQRCodeByHash(String hash, DatabaseConsumer<QRCode> callback) {
    collection
        .whereEqualTo("hash", hash)
        .get()
        .addOnCompleteListener(
            task -> {
              if (!task.isSuccessful()) {
                callback.accept(new DatabaseQueryResults<>(null, task.getException()));
                Log.d(LOGGER_TAG, "Failed to execute getQRCodeByHash", task.getException());
                return;
              }

              // Return found QRCode if any.
              for (QueryDocumentSnapshot snapshot : task.getResult()) {
                callback.accept(new DatabaseQueryResults<>(snapshotToQRCode(snapshot)));
                return;
              }

              // No hash by the QRCode exists.
              try {
                QRCode addedQR = new QRCode(hash);
                Map<String, Object> data = qrCodeToDBValues(addedQR);
                collection
                    .document(hash)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener(
                        results -> {
                          callback.accept(new DatabaseQueryResults<>(addedQR));
                        })
                    .addOnFailureListener(
                        e -> {
                          Log.w(LOGGER_TAG, "Error writing document", e);
                        });
              } catch (Exception e) {
                e.printStackTrace();
              }
            });
  }

  /**
   * Retrieve a set of QRCodes by their hashes
   *
   * @param hashes the hashes
   * @param callback callback to call on query completion
   */
  public void getQRCodeHashes(List<String> hashes, DatabaseConsumer<List<QRCode>> callback) {
    if (hashes.size() == 0) {
      callback.accept(new DatabaseQueryResults<>(new ArrayList<>()));
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
                List<QRCode> qrCodes =
                    task.getResult().getDocuments().stream()
                        .map(this::snapshotToQRCode)
                        .collect(Collectors.toList());

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
  public void getAllQRCodes(DatabaseConsumer<List<QRCode>> callback) {
    collection
        .get()
        .addOnCompleteListener(
            task -> {
              if (!task.isSuccessful()) {
                callback.accept(new DatabaseQueryResults<>(null, task.getException()));
                return;
              }

              // Convert all snapshots to QRCodes.
              List<QRCode> qrCodes = new ArrayList<>();
              for (QueryDocumentSnapshot snapshot : task.getResult()) {
                qrCodes.add(snapshotToQRCode(snapshot));
              }

              callback.accept(new DatabaseQueryResults<>(qrCodes));
            });
  }

  /**
   * Update a QRCode that already exists in the database.
   *
   * @param qrCode The QRCode to update
   * @param callback callback to call once the operation has finished
   */
  public void updateQRCode(QRCode qrCode, DatabaseConsumer<Void> callback) {
    getQRCodeByHash(
        qrCode.getHash(),
        results -> {
          if (!results.isSuccessful()) {
            callback.accept(new DatabaseQueryResults<>(null, results.getException()));
            return;
          }
          collection
              .document(qrCode.getHash())
              .update(qrCodeToDBValues(qrCode))
              .addOnCompleteListener(
                  task -> {
                    if (!task.isSuccessful()) {
                      callback.accept(new DatabaseQueryResults<>(null, task.getException()));
                      return;
                    }
                    // Update was successful
                    callback.accept(new DatabaseQueryResults<>(null));
                  });
        });
  }

  /**
   * Checks if a player has already added a specific QRCode to their profile.
   *
   * @param player The player to check.
   * @param qrCode The QRCode to check.
   * @param callback The callback function to handle the result.
   */
  public void playerHasQRCode(Player player, QRCode qrCode, DatabaseConsumer<Boolean> callback) {
    PlayerDatabase.getInstance()
        .getPlayerByDeviceId(
            player.getDeviceId(),
            results -> {
              if (results.isSuccessful()) {
                List<String> scannedQRCodeList = results.getData().getQRCodeHashes();
                if (scannedQRCodeList != null && scannedQRCodeList.contains(qrCode.getHash()))
                  callback.accept(new DatabaseQueryResults<>(true));
                else callback.accept(new DatabaseQueryResults<>(false));
              } else {
                callback.accept(new DatabaseQueryResults<>(null, results.getException()));
              }
            });
  }

  /**
   * Adds a never-before-scanned QRCode to the database
   *
   * @param qrCode The never-before-scanned QRCode to add
   */
  public void addQRCode(QRCode qrCode, DatabaseConsumer<Void> task) {
    getQRCodeByHash(
        qrCode.getHash(),
        existingQRCode -> {
          if (existingQRCode.getData() == null) {
            Map<String, Object> data = qrCodeToDBValues(qrCode);
            collection
                .document(qrCode.getHash())
                .set(data, SetOptions.merge())
                .addOnSuccessListener(
                    results -> {
                      Log.d(LOGGER_TAG, "QRCode added to database");
                      task.accept(new DatabaseQueryResults<>(null));
                    })
                .addOnFailureListener(
                    e -> {
                      Log.w(LOGGER_TAG, "Error writing document", e);
                      task.accept(new DatabaseQueryResults<>(null, e));
                    });
          } else {
            task.accept(
                new DatabaseQueryResults<>(
                    null, new IllegalArgumentException("QRCode already exists in database.")));
          }
        });
  }

  /**
   * Adds the QRCode to the player's account, as well as the Player to the QRCode's collection of
   * players that have added it.
   *
   * @param player The player to add QRCode to.
   * @param qrCode The QRCode to be added.
   */
  public void addPlayerToQR(Player player, QRCode qrCode, DatabaseConsumer<Void> task) {
    // Adds QRCode's hash to player's collection of QRCodes
    if (player.getQRCodeHashes() == null) { // Player hasn't scanned any codes yet
      player.setQRCodeHashes(new ArrayList<>());
    }
    player.getQRCodeHashes().add(qrCode.getHash());
    PlayerDatabase.getInstance()
        .update(
            player,
            result -> {
              if (!result.isSuccessful()) {
                Log.w(LOGGER_TAG, "Error adding QR code to account.", result.getException());
                task.accept(new DatabaseQueryResults<>(null, result.getException()));
                return;
              }

              Log.d(LOGGER_TAG, "QR code added to user's account.");
              // Add Player's UUID to QRCode's collection of players that have scanned it.
              getQRCodeByHash(
                  qrCode.getHash(),
                  qrHashTask -> {
                    if (!qrHashTask.isSuccessful()) {
                      task.accept(new DatabaseQueryResults<>(null, qrHashTask.getException()));
                      return;
                    }

                    QRCode updatedQRCode = qrHashTask.getData();
                    updatedQRCode.addPlayer(player.getDocumentId());
                    updatedQRCode.setLoc(qrCode.getLoc());
                    updateQRCode(
                        updatedQRCode,
                        updateResult -> {
                          if (!updateResult.isSuccessful()) {
                            Log.w(
                                LOGGER_TAG,
                                "Error adding player to QR code",
                                updateResult.getException());
                            task.accept(
                                new DatabaseQueryResults<>(null, updateResult.getException()));
                            return;
                          }
                          Log.d(LOGGER_TAG, "Player has been added to QRCode's players list");
                          task.accept(new DatabaseQueryResults<>(null));
                        });
                  });
            });
  }

  /**
   * Removes the QRCode to the player's account, as well as the Player from the QRCode's collection
   * of players that have added it.
   *
   * @param player The player to remove QRCode from.
   * @param qrCode The QRCode to be removed
   */
  public void removeQRCodeFromPlayer(Player player, QRCode qrCode, DatabaseConsumer<Void> task) {
    // Adds QRCode's hash to player's collection of QRCodes
    if (player.getQRCodeHashes() == null) { // Player hasn't scanned any codes yet
      throw new IllegalArgumentException("Player has no QRCodes to remove!");
    }
    if (!player.getQRCodeHashes().remove(qrCode.getHash())) {
      throw new IllegalArgumentException("Player does not have QRCode!");
    }

    // Update player database to remove qr hash from their data
    PlayerDatabase.getInstance()
        .update(
            player,
            result -> {
              if (!result.isSuccessful()) {
                Log.w(LOGGER_TAG, "Error removing QR code from account.", result.getException());
                task.accept(new DatabaseQueryResults<>(null, result.getException()));
              }
              Log.d(LOGGER_TAG, "QR code has been removed from user's account.");

              // Remove Player's UUID from QRCode's collection of players that have scanned it.
              collection
                  .document(qrCode.getHash())
                  .get()
                  .addOnCompleteListener(
                      snapshot -> {
                        qrCode.setPlayers(
                            (ArrayList<String>) snapshot.getResult().getData().get("players"));
                        if (qrCode.getPlayers() == null) {
                          task.accept(
                              new DatabaseQueryResults<>(
                                  null,
                                  new IllegalArgumentException(
                                      "QRCode hasn't been scanned by anybody!")));
                          return;
                        }
                        if (!qrCode.getPlayers().remove(player.getDocumentId())) {
                          task.accept(
                              new DatabaseQueryResults<>(
                                  null,
                                  new IllegalArgumentException(
                                      "QRCode has not been added by the player!")));
                          return;
                        }

                        collection
                            .document(qrCode.getHash())
                            .update(qrCodeToDBValues(qrCode))
                            .addOnSuccessListener(
                                results -> {
                                  Log.d(
                                      LOGGER_TAG,
                                      "Player has been removed from QRCode's players list");
                                  task.accept(new DatabaseQueryResults<>(null));
                                })
                            .addOnFailureListener(
                                e -> {
                                  Log.w(LOGGER_TAG, "Error removing player from QR code.", e);
                                  task.accept(new DatabaseQueryResults<>(null, e));
                                });
                      });
            });
  }

  /**
   * Converts a database snapshot to its QRCode object equivalent.
   *
   * @param snapshot database snapshot
   * @return QRCode object
   */
  private QRCode snapshotToQRCode(DocumentSnapshot snapshot) {
    String hash = snapshot.getId();
    String name = snapshot.getString("name");
    Integer score = (int) (long) snapshot.get("score");
    Location location;
    if (snapshot.get("latitude") == null || snapshot.get("longitude") == null) location = null;
    else {
      location = new Location("");
      location.setLatitude((double) snapshot.get("latitude"));
      location.setLongitude((double) snapshot.get("longitude"));
    }
    ArrayList<String> players = (ArrayList<String>) snapshot.get("players");
    try {
      return new QRCode(hash, name, score, location, new ArrayList<>(), new ArrayList<>(), players);
    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Maps the QRCode's attributes to database insertable values.
   *
   * @param qrCode The QRCode object
   * @return A map of database insertable values.
   */
  private Map<String, Object> qrCodeToDBValues(QRCode qrCode) {
    Map<String, Object> values = new HashMap<>();
    values.put("hash", qrCode.getHash());
    values.put("name", qrCode.getName());
    values.put("score", qrCode.getScore());
    values.put("latitude", qrCode.getLoc() != null ? qrCode.getLoc().getLatitude() : null);
    values.put("longitude", qrCode.getLoc() != null ? qrCode.getLoc().getLongitude() : null);
    values.put("players", qrCode.getPlayers());
    values.put("comments", qrCode.getComments());
    return values;
  }

  /** Add snapshot listener to database */
  public void addListener(DatabaseChangeListener listener) {
    collection.addSnapshotListener(
        new EventListener<QuerySnapshot>() {
          @Override
          public void onEvent(
              @Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
            listener.onChange();
          }
        });
  }
}
