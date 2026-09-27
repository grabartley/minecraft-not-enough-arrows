package com.grahambartley.notenougharrows.control;

public final class FrostGrip {
  public static final int HEADROOM_TICKS = 4;
  public static final int SHIMMER_INTERVAL_TICKS = 5;
  public static final int FREEZE_DAMAGE_INTERVAL_TICKS = 40;
  public static final float FREEZE_DAMAGE = 1.0f;
  public static final float EXTRA_FREEZE_DAMAGE = 5.0f;

  private FrostGrip() {}

  public static int pinnedTicks(final int minFreezeDamageTicks) {
    return Math.max(0, minFreezeDamageTicks) + HEADROOM_TICKS;
  }

  public static boolean holds(final int durationTicks) {
    return durationTicks > 0;
  }

  public static boolean shimmersOn(final long worldTick) {
    return worldTick % SHIMMER_INTERVAL_TICKS == 0;
  }

  public static boolean bitesOn(final int targetAge) {
    return targetAge % FREEZE_DAMAGE_INTERVAL_TICKS == 0;
  }
}
