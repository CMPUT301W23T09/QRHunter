package com.cmput301w23t09.qrhunter.util;

public class Tuple<A, B> {

  private final A left;
  private final B right;

  public Tuple(A left, B right) {
    this.left = left;
    this.right = right;
  }

  public A getLeft() {
    return left;
  }

  public B getRight() {
    return right;
  }
}
