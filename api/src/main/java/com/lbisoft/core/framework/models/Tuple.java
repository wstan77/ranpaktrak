package com.lbisoft.core.framework.models;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Tuple<L, R> {

  private L left;
  private R right;

  public Tuple(L left, R right) {
    this.left = left;
    this.right = right;
  }

  public L getLeft() {
    return left;
  }

  public void setLeft(L left) {
    this.left = left;
  }

  public R getRight() {
    return right;
  }

  public void setRight(R right) {
    this.right = right;
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof Tuple) {
      Tuple<?, ?> otherTuple = (Tuple<?, ?>) other;
      return ((left == null && otherTuple.getLeft() == null)
              || (left != null && left.equals(otherTuple.getLeft())))
          && ((right == null && otherTuple.getRight() == null)
              || (right != null && right.equals(otherTuple.getRight())));
    }
    return false;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(left).append(right).build();
  }
}
