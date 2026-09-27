package com.grahambartley.notenougharrows.control;

public final class Escort {
  public static final double CLOSE_IN_BEYOND = 6.0;
  public static final double SETTLE_WITHIN = 3.0;
  public static final double LOST_TRACK_BEYOND = 16.0;

  private Escort() {}

  public static boolean shouldCloseIn(final double squaredDistance, final double width) {
    final double reach = CLOSE_IN_BEYOND + width / 2.0;
    return squaredDistance > reach * reach;
  }

  public static boolean isCloseEnough(final double squaredDistance, final double width) {
    final double reach = SETTLE_WITHIN + width / 2.0;
    return squaredDistance <= reach * reach;
  }

  public static boolean hasLostTrack(final double squaredDistance) {
    return squaredDistance > LOST_TRACK_BEYOND * LOST_TRACK_BEYOND;
  }
}
