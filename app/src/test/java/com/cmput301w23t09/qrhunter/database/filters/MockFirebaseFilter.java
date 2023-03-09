package com.cmput301w23t09.qrhunter.database.filters;

public abstract class MockFirebaseFilter {

  protected final String field;
  protected final Object comparisonValue;

  public MockFirebaseFilter(String field, Object comparisonValue) {
    this.field = field;
    this.comparisonValue = comparisonValue;
  }

  public String getField() {
    return field;
  }

  public abstract boolean isValid(Object fieldValue);
}
