package com.grahambartley.morearrows.hud;

public final class CountdownArc {
  public static final int TICKS_PER_SECOND = 20;
  public static final float URGENT_SECONDS = 1.5f;
  public static final float FULL_SWEEP = (float) (Math.PI * 2.0);
  public static final float SEGMENT_RADIANS = FULL_SWEEP / 64.0f;

  private CountdownArc() {}

  public static float ticksLeft(final int remainingTicks, final float tickDelta) {
    return Math.max(0.0f, remainingTicks - clampDelta(tickDelta));
  }

  public static float secondsLeft(final int remainingTicks, final float tickDelta) {
    return ticksLeft(remainingTicks, tickDelta) / TICKS_PER_SECOND;
  }

  public static float fractionLeft(
      final int remainingTicks, final int delayTicks, final float tickDelta) {
    if (delayTicks <= 0) {
      return 0.0f;
    }
    return Math.min(1.0f, ticksLeft(remainingTicks, tickDelta) / delayTicks);
  }

  public static float sweepRadians(final float fraction) {
    return FULL_SWEEP * Math.max(0.0f, Math.min(1.0f, fraction));
  }

  public static int segmentsFor(final float sweepRadians) {
    if (sweepRadians <= 0.0f) {
      return 0;
    }
    return (int) Math.ceil(sweepRadians / SEGMENT_RADIANS);
  }

  public static float segmentEnd(final int segment, final float sweepRadians) {
    return Math.min((segment + 1) * SEGMENT_RADIANS, sweepRadians);
  }

  public static boolean isUrgent(final int remainingTicks, final float tickDelta) {
    return secondsLeft(remainingTicks, tickDelta) <= URGENT_SECONDS;
  }

  private static float clampDelta(final float tickDelta) {
    return Math.max(0.0f, Math.min(1.0f, tickDelta));
  }
}
