package com.grahambartley.notenougharrows.grapple;

public enum GrappleEnding {
  ARRIVED(true, true),
  OBSTRUCTED(true, false),
  OUT_OF_TIME(true, false),
  CANCELLED(true, false),
  ANCHOR_LOST(false, false),
  SHOOTER_GONE(false, false);

  private final boolean ownsTheFall;
  private final boolean returnsTheArrow;

  GrappleEnding(final boolean ownsTheFall, final boolean returnsTheArrow) {
    this.ownsTheFall = ownsTheFall;
    this.returnsTheArrow = returnsTheArrow;
  }

  public boolean ownsTheFall() {
    return ownsTheFall;
  }

  public boolean returnsTheArrow() {
    return returnsTheArrow;
  }
}
