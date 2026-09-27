package com.grahambartley.notenougharrows.control;

public final class Escort {
  public static final double CLOSE_IN_BEYOND = 6.0;
  public static final double SETTLE_WITHIN = 3.0;

  private Escort() {}

  public static boolean shouldCloseIn(final double squaredDistance) {
    return squaredDistance > CLOSE_IN_BEYOND * CLOSE_IN_BEYOND;
  }

  public static boolean isCloseEnough(final double squaredDistance) {
    return squaredDistance <= SETTLE_WITHIN * SETTLE_WITHIN;
  }
}
