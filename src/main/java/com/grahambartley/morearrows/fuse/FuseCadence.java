package com.grahambartley.morearrows.fuse;

import java.util.ArrayList;
import java.util.List;

public final class FuseCadence {
  public static final int SLOWEST_INTERVAL_TICKS = 20;
  public static final int FASTEST_INTERVAL_TICKS = 2;

  private FuseCadence() {}

  public static List<Integer> beepTicks(final int delayTicks) {
    if (delayTicks <= 0) {
      return List.of();
    }

    final List<Integer> ticks = new ArrayList<>();
    int remaining = delayTicks;
    while (remaining > 0) {
      ticks.add(remaining);
      remaining -= intervalAt(remaining, delayTicks);
    }
    return List.copyOf(ticks);
  }

  public static boolean beepsAt(final int remainingTicks, final int delayTicks) {
    if (delayTicks <= 0 || remainingTicks <= 0 || remainingTicks > delayTicks) {
      return false;
    }

    int scheduled = delayTicks;
    while (scheduled > remainingTicks) {
      scheduled -= intervalAt(scheduled, delayTicks);
    }
    return scheduled == remainingTicks;
  }

  public static int intervalAt(final int remainingTicks, final int delayTicks) {
    if (delayTicks <= 0) {
      return FASTEST_INTERVAL_TICKS;
    }

    final int elapsedShare = Math.max(0, Math.min(remainingTicks, delayTicks));
    final int spread = SLOWEST_INTERVAL_TICKS - FASTEST_INTERVAL_TICKS;
    return FASTEST_INTERVAL_TICKS + Math.round((float) spread * elapsedShare / delayTicks);
  }
}
