package com.cmput301w23t09.qrhunter.database;

/**
 * Database query result helper class used as an intermediate between the database and application
 * in data representation.
 *
 * @param <T> The type of the data results
 */
public class DatabaseQueryResults<T> {
  private final Exception exception;
  private final T data;

  public DatabaseQueryResults(T result, Exception exception) {
    data = result;
    this.exception = exception;
  }

  public DatabaseQueryResults(T result) {
    this(result, null);
  }

  /**
   * Check if the database query was successful.
   *
   * @return if the database query was successful
   */
  public boolean isSuccessful() {
    return getException() == null;
  }

  /**
   * Retrieve the result of the database query.
   *
   * @return database results
   * @throws IllegalStateException if database query was not successful
   */
  public T getData() {
    if (!isSuccessful()) {
      throw new IllegalStateException("Database query was not successful.", getException());
    }

    return data;
  }

  /**
   * Retrieve the exception of the database query if any.
   *
   * @return exception or null if no exception was found.
   */
  public Exception getException() {
    return exception;
  }
}
