package com.cmput301w23t09.qrhunter.util;

/**
 * Tuple data structure to represent a pair
 *
 * @param <A> type of the left node
 * @param <B> type of the right node
 */
public class Tuple<A, B> {

  private final A left;
  private final B right;

  public Tuple(A left, B right) {
    this.left = left;
    this.right = right;
  }

  /**
   * Retrieve the left node object
   *
   * @return left node
   */
  public A getLeft() {
    return left;
  }

  /**
   * Retrieve the right node object
   *
   * @return right node
   */
  public B getRight() {
    return right;
  }
}
