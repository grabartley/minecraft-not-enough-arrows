package com.grahambartley.notenougharrows.zipline;

public enum RideEnding {
  ARRIVED(true),
  OBSTRUCTED(true),
  OUT_OF_TIME(true),
  LET_GO(false),
  SPAN_LOST(false),
  REPLACED(false),
  RIDER_GONE(false);

  private final boolean ownsTheFall;

  RideEnding(final boolean ownsTheFall) {
    this.ownsTheFall = ownsTheFall;
  }

  public boolean ownsTheFall() {
    return ownsTheFall;
  }
}
