package com.cmput301w23t09.qrhunter.database.filters;

public class MockFirebaseGreaterThanOrEqualToFilter extends MockFirebaseFilter {

  public MockFirebaseGreaterThanOrEqualToFilter(String field, Object comparisonValue) {
    super(field, comparisonValue);
  }

  @Override
  public boolean isValid(Object fieldValue) {
    return Double.parseDouble((String) fieldValue) >= Double.parseDouble((String) comparisonValue);
  }
}
