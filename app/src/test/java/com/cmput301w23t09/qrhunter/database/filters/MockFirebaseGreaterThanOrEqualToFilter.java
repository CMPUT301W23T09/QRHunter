package com.cmput301w23t09.qrhunter.database.filters;

/** firebase filter used to mock the .whereGreaterThanOrEqualTo on a CollectionReference */
public class MockFirebaseGreaterThanOrEqualToFilter extends MockFirebaseFilter {

  public MockFirebaseGreaterThanOrEqualToFilter(String field, Object comparisonValue) {
    super(field, comparisonValue);
  }

  @Override
  public boolean isValid(Object fieldValue) {
    return Double.parseDouble((String) fieldValue) >= Double.parseDouble((String) comparisonValue);
  }
}
