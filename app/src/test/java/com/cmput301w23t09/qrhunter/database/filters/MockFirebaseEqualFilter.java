package com.cmput301w23t09.qrhunter.database.filters;

/** firebase filter used to mock the .whereEqualsTo on a CollectionReference */
public class MockFirebaseEqualFilter extends MockFirebaseFilter {

  public MockFirebaseEqualFilter(String field, Object comparisonValue) {
    super(field, comparisonValue);
  }

  @Override
  public boolean isValid(Object fieldValue) {
    return comparisonValue.equals(fieldValue);
  }
}
