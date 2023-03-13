package com.cmput301w23t09.qrhunter.qrcode;

import com.cmput301w23t09.qrhunter.database.DatabaseConsumer;
import com.cmput301w23t09.qrhunter.database.DatabaseQueryResults;
import com.cmput301w23t09.qrhunter.player.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MockQRCodeDatabase extends QRCodeDatabase {

  private final Map<String, QRCode> qrCodesByHash = new HashMap<>();

  @Override
  protected void initFirebase() {}

  @Override
  public void getQRCodeByHash(String hash, DatabaseConsumer<QRCode> callback) {
    callback.accept(new DatabaseQueryResults<>(qrCodesByHash.getOrDefault(hash, null)));
  }

  @Override
  public void getQRCodeHashes(List<String> hashes, DatabaseConsumer<List<QRCode>> callback) {
    callback.accept(
        new DatabaseQueryResults<>(
            hashes.stream()
                .map(hash -> qrCodesByHash.getOrDefault(hash, null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList())));
  }

  @Override
  public void getAllQRCodes(DatabaseConsumer<List<QRCode>> callback) {
    callback.accept(new DatabaseQueryResults<>(new ArrayList<>(qrCodesByHash.values())));
  }

  @Override
  public void updateQRCode(QRCode qrCode, DatabaseConsumer<Void> callback) {
    if (qrCodesByHash.containsKey(qrCode.getHash())) {
      qrCodesByHash.put(qrCode.getHash(), qrCode);
    }
    callback.accept(new DatabaseQueryResults<>(null));
    notifyListeners(new ArrayList<>(qrCodesByHash.values()));
  }

  @Override
  public void playerHasQRCode(Player player, QRCode qrCode, DatabaseConsumer<Boolean> callback) {
    QRCode databaseQR = qrCodesByHash.getOrDefault(qrCode.getHash(), null);

    if (databaseQR == null) {
      callback.accept(new DatabaseQueryResults<>(false));
      return;
    }

    callback.accept(
        new DatabaseQueryResults<>(databaseQR.getPlayers().contains(player.getDocumentId())));
  }

  @Override
  public void addQRCode(QRCode qrCode) {
    qrCodesByHash.put(qrCode.getHash(), qrCode);
    notifyListeners(new ArrayList<>(qrCodesByHash.values()));
  }

  @Override
  public void addPlayerToQR(Player player, QRCode qrCode) {
    qrCode.getPlayers().add(player.getDocumentId());
    qrCodesByHash.put(qrCode.getHash(), qrCode);

    notifyListeners(new ArrayList<>(qrCodesByHash.values()));
  }

  @Override
  public void removeQRCodeFromPlayer(Player player, QRCode qrCode) {
    qrCode.getPlayers().remove(player.getDocumentId());
    qrCodesByHash.put(qrCode.getHash(), qrCode);

    notifyListeners(new ArrayList<>(qrCodesByHash.values()));
  }
}
