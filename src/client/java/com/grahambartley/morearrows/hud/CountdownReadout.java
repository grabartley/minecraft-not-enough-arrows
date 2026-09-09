package com.grahambartley.morearrows.hud;

import java.util.Locale;

public final class CountdownReadout {
  public static final int TICKS_PER_SECOND = 20;
  public static final float URGENT_SECONDS = 1.5f;

  private CountdownReadout() {}

  public static float secondsLeft(final int remainingTicks) {
    return Math.max(0, remainingTicks) / (float) TICKS_PER_SECOND;
  }

  public static String secondsText(final int remainingTicks) {
    return String.format(Locale.ROOT, "%.1fs", secondsLeft(remainingTicks));
  }

  public static float fractionLeft(final int remainingTicks, final int delayTicks) {
    if (delayTicks <= 0) {
      return 0f;
    }
    final int remaining = Math.max(0, Math.min(remainingTicks, delayTicks));
    return remaining / (float) delayTicks;
  }

  public static boolean isUrgent(final int remainingTicks) {
    return secondsLeft(remainingTicks) <= URGENT_SECONDS;
  }

  public static int barWidth(final int remainingTicks, final int delayTicks, final int fullWidth) {
    if (fullWidth <= 0) {
      return 0;
    }
    return Math.round(fullWidth * fractionLeft(remainingTicks, delayTicks));
  }
}
