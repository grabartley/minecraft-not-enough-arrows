package com.grahambartley.notenougharrows.reveal;

public final class RoughDistance {
  public static final int STEP_BLOCKS = 10;

  private RoughDistance() {}

  public static int of(final double blocks) {
    final long steps = Math.round(Math.max(0.0, blocks) / STEP_BLOCKS);
    return (int) Math.max(1, steps) * STEP_BLOCKS;
  }
}
