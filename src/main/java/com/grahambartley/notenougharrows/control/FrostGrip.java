package com.grahambartley.notenougharrows.control;

public final class FrostGrip {
  public static final int HEADROOM_TICKS = 4;

  private FrostGrip() {}

  public static int pinnedTicks(final int minFreezeDamageTicks) {
    return Math.max(0, minFreezeDamageTicks) + HEADROOM_TICKS;
  }

  public static boolean holds(final int durationTicks) {
    return durationTicks > 0;
  }
}
