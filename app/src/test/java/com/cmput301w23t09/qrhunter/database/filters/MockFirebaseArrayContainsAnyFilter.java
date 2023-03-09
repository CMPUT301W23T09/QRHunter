package com.cmput301w23t09.qrhunter.database.filters;

import java.util.Collection;

public class MockFirebaseArrayContainsAnyFilter extends MockFirebaseFilter {

  public MockFirebaseArrayContainsAnyFilter(String field, Object comparisonValue) {
    super(field, comparisonValue);
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean isValid(Object fieldValue) {
    Collection<Object> shouldHaveAnyFrom = (Collection<Object>) fieldValue;
    Collection<Object> currentValues = (Collection<Object>) fieldValue;

    for (Object obj : shouldHaveAnyFrom) {
      if (currentValues.contains(obj)) {
        return true;
      }
    }

    return false;
  }
}
