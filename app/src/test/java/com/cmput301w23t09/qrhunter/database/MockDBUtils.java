package com.cmput301w23t09.qrhunter.database;

import static org.mockito.Mockito.*;

import com.cmput301w23t09.qrhunter.database.filters.MockFirebaseArrayContainsAnyFilter;
import com.cmput301w23t09.qrhunter.database.filters.MockFirebaseArrayContainsFilter;
import com.cmput301w23t09.qrhunter.database.filters.MockFirebaseArrayNotContainsFilter;
import com.cmput301w23t09.qrhunter.database.filters.MockFirebaseEqualFilter;
import com.cmput301w23t09.qrhunter.database.filters.MockFirebaseFilter;
import com.cmput301w23t09.qrhunter.database.filters.MockFirebaseGreaterThanFilter;
import com.cmput301w23t09.qrhunter.database.filters.MockFirebaseGreaterThanOrEqualToFilter;
import com.cmput301w23t09.qrhunter.database.filters.MockFirebaseLessThanFilter;
import com.cmput301w23t09.qrhunter.database.filters.MockFirebaseLessThanOrEqualToFilter;
import com.cmput301w23t09.qrhunter.database.filters.MockFirebaseNotEqualFilter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MockDBUtils {

  private MockDBUtils() {}

  /**
   * Creates a mock Firebase collection
   *
   * @param documents documents that should exist within the collection
   * @return
   */
  public static CollectionReference makeCollection(MockDocument... documents) {
    // Create a virtual cache of the documents to store in the firebase collection.
    Map<String, MockDocument> mockDocumentMap = new HashMap<>();
    for (MockDocument mockDocument : documents) {
      mockDocumentMap.put(mockDocument.getDocumentId(), mockDocument);
    }

    CollectionReference reference = mock(CollectionReference.class);

    // Mock all possible filter options
    Set<MockFirebaseFilter> filters = new HashSet<>();
    when(reference.whereEqualTo(anyString(), any()))
        .thenAnswer(
            invocation -> {
              filters.add(
                  new MockFirebaseEqualFilter(
                      invocation.getArgument(0), invocation.getArgument(1)));
              return reference;
            });
    when(reference.whereNotEqualTo(anyString(), any()))
        .thenAnswer(
            invocation -> {
              filters.add(
                  new MockFirebaseNotEqualFilter(
                      invocation.getArgument(0), invocation.getArgument(1)));
              return reference;
            });
    when(reference.whereGreaterThanOrEqualTo(anyString(), any()))
        .thenAnswer(
            invocation -> {
              filters.add(
                  new MockFirebaseGreaterThanOrEqualToFilter(
                      invocation.getArgument(0), invocation.getArgument(1)));
              return reference;
            });
    when(reference.whereGreaterThan(anyString(), any()))
        .thenAnswer(
            invocation -> {
              filters.add(
                  new MockFirebaseGreaterThanFilter(
                      invocation.getArgument(0), invocation.getArgument(1)));
              return reference;
            });
    when(reference.whereLessThanOrEqualTo(anyString(), any()))
        .thenAnswer(
            invocation -> {
              filters.add(
                  new MockFirebaseLessThanOrEqualToFilter(
                      invocation.getArgument(0), invocation.getArgument(1)));
              return reference;
            });
    when(reference.whereLessThan(anyString(), any()))
        .thenAnswer(
            invocation -> {
              filters.add(
                  new MockFirebaseLessThanFilter(
                      invocation.getArgument(0), invocation.getArgument(1)));
              return reference;
            });
    when(reference.whereArrayContains(anyString(), any()))
        .thenAnswer(
            invocation -> {
              filters.add(
                  new MockFirebaseArrayContainsFilter(
                      invocation.getArgument(0), invocation.getArgument(1)));
              return reference;
            });
    when(reference.whereArrayContainsAny(anyString(), any()))
        .thenAnswer(
            invocation -> {
              filters.add(
                  new MockFirebaseArrayContainsAnyFilter(
                      invocation.getArgument(0), invocation.getArgument(1)));
              return reference;
            });
    when(reference.whereNotIn(anyString(), any()))
        .thenAnswer(
            invocation -> {
              filters.add(
                  new MockFirebaseArrayNotContainsFilter(
                      invocation.getArgument(0), invocation.getArgument(1)));
              return reference;
            });
    when(reference.whereIn(anyString(), any()))
        .thenAnswer(
            invocation ->
                reference.whereArrayContains(
                    (String) invocation.getArgument(0), invocation.getArgument(1)));

    // Mock retrieval methods
    when(reference.get())
        .thenAnswer(
            invocation -> {
              // Apply filters to all documents stored in the collection.
              Set<MockDocument> filteredDocuments =
                  mockDocumentMap.values().stream()
                      .filter(
                          mockDocument -> {
                            for (MockFirebaseFilter filter : filters) {
                              if (!filter.isValid(
                                  mockDocument.getData().getOrDefault(filter.getField(), null))) {
                                return false;
                              }
                            }
                            return true;
                          })
                      .collect(Collectors.toSet());

              filters.clear();

              QuerySnapshotTask returnTask = new QuerySnapshotTask();
              returnTask.setResult(makeQuerySnapshot(filteredDocuments));
              return returnTask;
            });
    when(reference.document(anyString()))
        .thenAnswer(
            answer -> {
              String documentId = (String) answer.getArgument(0);
              if (!mockDocumentMap.containsKey(documentId)) {
                return null;
              }

              return createReference(mockDocumentMap.get(documentId), mockDocumentMap);
            });

    // Mock adding documents to the collection.
    when(reference.add(any()))
        .thenAnswer(
            invocation -> {
              Map<String, Object> data = invocation.getArgument(0);
              MockDocument document = new MockDocument(data);
              mockDocumentMap.put(document.getDocumentId(), document);

              DocumentReference mockReference = createReference(document, mockDocumentMap);

              DocumentReferenceTask documentTask = new DocumentReferenceTask();
              documentTask.setReference(mockReference);
              return documentTask;
            });

    return reference;
  }

  /**
   * Creates a document reference based off of a mock document.
   *
   * @param mockDocument mock document
   * @param mockDocumentMap reference to all of the mock documents stored by the collection the
   *     document belongs to
   * @return document reference
   */
  private static DocumentReference createReference(
      MockDocument mockDocument, Map<String, MockDocument> mockDocumentMap) {
    DocumentReference mockReference = mock(DocumentReference.class);
    when(mockReference.getId()).thenAnswer(answer -> mockDocument.getDocumentId());
    when(mockReference.update(any()))
        .thenAnswer(
            answer -> {
              mockDocument.setData(answer.getArgument(0));
              return new VoidTask();
            });
    when(mockReference.delete())
        .thenAnswer(
            answer -> {
              mockDocumentMap.remove(mockDocument.getDocumentId());
              return new VoidTask();
            });

    return mockReference;
  }

  /**
   * Creates a mock query snapshot that contains numerous documents
   *
   * @param documents documents to include
   * @return mock query snapshot
   */
  private static QuerySnapshot makeQuerySnapshot(Collection<MockDocument> documents) {
    Iterator<MockDocument> mockDocumentIterator = documents.iterator();
    QuerySnapshot snapshot = mock(QuerySnapshot.class);

    when(snapshot.iterator())
        .thenReturn(
            new Iterator<QueryDocumentSnapshot>() {
              @Override
              public boolean hasNext() {
                return mockDocumentIterator.hasNext();
              }

              @Override
              public QueryDocumentSnapshot next() {
                MockDocument document = mockDocumentIterator.next();
                return document.toSnapshot();
              }
            });
    return snapshot;
  }
}
