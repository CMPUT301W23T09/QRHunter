package com.cmput301w23t09.qrhunter.database;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.Map;
import java.util.UUID;

/** Document that the mock firebase database contains */
public class MockDocument {

  private final String documentId;
  private Map<String, Object> data;

  public MockDocument(Map<String, Object> data) {
    this.documentId = UUID.randomUUID().toString();
    this.data = data;
  }

  public String getDocumentId() {
    return documentId;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public void setData(Map<String, Object> data) {
    this.data = data;
  }

  /**
   * Converts the mock document to a document snapshot
   *
   * @return query document snapshot
   */
  public QueryDocumentSnapshot toSnapshot() {
    QueryDocumentSnapshot snapshot = mock(QueryDocumentSnapshot.class);

    // Mock ALL methods that could return data from the snapshot.
    when(snapshot.getId()).thenAnswer(answer -> getDocumentId());

    Map<String, Object> data = getData();
    when(snapshot.get(anyString())).thenAnswer(answer -> data.get((String) answer.getArgument(0)));
    when(snapshot.getBoolean(anyString()))
        .thenAnswer(answer -> data.get((String) answer.getArgument(0)));
    when(snapshot.getDouble(anyString()))
        .thenAnswer(answer -> data.get((String) answer.getArgument(0)));
    when(snapshot.getString(anyString()))
        .thenAnswer(answer -> data.get((String) answer.getArgument(0)));
    when(snapshot.getLong(anyString()))
        .thenAnswer(answer -> data.get((String) answer.getArgument(0)));
    when(snapshot.getDate(anyString()))
        .thenAnswer(answer -> data.get((String) answer.getArgument(0)));
    when(snapshot.getTimestamp(anyString()))
        .thenAnswer(answer -> data.get((String) answer.getArgument(0)));
    when(snapshot.getBlob(anyString()))
        .thenAnswer(answer -> data.get((String) answer.getArgument(0)));
    when(snapshot.getGeoPoint(anyString()))
        .thenAnswer(answer -> data.get((String) answer.getArgument(0)));
    when(snapshot.getDocumentReference(anyString()))
        .thenAnswer(answer -> data.get((String) answer.getArgument(0)));
    return snapshot;
  }
}
