package com.cmput301w23t09.qrhunter;

/** Specifies a listener for database changes. */
public interface DatabaseChangeListener {

  /** Method to call on a database change. */
  public void onChange();
}
