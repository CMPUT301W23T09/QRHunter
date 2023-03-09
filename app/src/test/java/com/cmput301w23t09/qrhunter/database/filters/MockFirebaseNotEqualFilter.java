package com.cmput301w23t09.qrhunter.database.filters;

public class MockFirebaseNotEqualFilter extends MockFirebaseFilter {

  public MockFirebaseNotEqualFilter(String field, Object comparisonValue) {
    super(field, comparisonValue);
  }

  @Override
  public boolean isValid(Object fieldValue) {
    return !comparisonValue.equals(fieldValue);
  }
}
