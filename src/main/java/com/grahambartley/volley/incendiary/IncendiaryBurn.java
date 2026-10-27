package com.grahambartley.volley.incendiary;

public final class IncendiaryBurn {
  public static final int TICKS_PER_SECOND = 20;

  private IncendiaryBurn() {}

  public static int igniteTicks(final int seconds) {
    return Math.max(0, seconds) * TICKS_PER_SECOND;
  }
}
