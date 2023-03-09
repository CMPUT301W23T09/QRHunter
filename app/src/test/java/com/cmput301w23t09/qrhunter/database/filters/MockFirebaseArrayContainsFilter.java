package com.cmput301w23t09.qrhunter.database.filters;

import java.util.Collection;

/** firebase filter used to mock the .whereArrayContains on a CollectionReference */
public class MockFirebaseArrayContainsFilter extends MockFirebaseFilter {

  public MockFirebaseArrayContainsFilter(String field, Object comparisonValue) {
    super(field, comparisonValue);
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean isValid(Object fieldValue) {
    Collection<Object> shouldHaveAllFrom = (Collection<Object>) fieldValue;
    Collection<Object> currentValues = (Collection<Object>) fieldValue;

    for (Object obj : shouldHaveAllFrom) {
      if (!currentValues.contains(obj)) {
        return false;
      }
    }

    return true;
  }
}
