package com.cmput301w23t09.qrhunter.database.filters;

import java.util.Collection;

public class MockFirebaseArrayNotContainsFilter extends MockFirebaseFilter {

  public MockFirebaseArrayNotContainsFilter(String field, Object comparisonValue) {
    super(field, comparisonValue);
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean isValid(Object fieldValue) {
    Collection<Object> cannotHaveAny = (Collection<Object>) fieldValue;
    Collection<Object> currentValues = (Collection<Object>) fieldValue;

    for (Object obj : cannotHaveAny) {
      if (currentValues.contains(obj)) {
        return false;
      }
    }

    return true;
  }
}
