package com.grahambartley.notenougharrows.tow;

public enum TowEnding {
  ARRIVED,
  OBSTRUCTED,
  OUT_OF_TIME,
  TARGET_GONE,
  SHOOTER_GONE,
  OUT_OF_REACH,
  REPLACED;

  public boolean stopsTheTarget() {
    return this == ARRIVED;
  }
}
