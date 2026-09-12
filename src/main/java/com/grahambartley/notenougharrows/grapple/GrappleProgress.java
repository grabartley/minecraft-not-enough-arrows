package com.grahambartley.notenougharrows.grapple;

public record GrappleProgress(double closestApproach, int idleTicks) {
  public static final double MEANINGFUL_CLOSING = 0.05;
  public static final int IDLE_TICKS_LIMIT = 20;

  public GrappleProgress {
    closestApproach = Math.max(0.0, closestApproach);
    idleTicks = Math.max(0, idleTicks);
  }

  public static GrappleProgress startingAt(final double distance) {
    return new GrappleProgress(distance, 0);
  }

  public GrappleProgress closedTo(final double distance) {
    return distance <= closestApproach - MEANINGFUL_CLOSING
        ? new GrappleProgress(distance, 0)
        : new GrappleProgress(closestApproach, idleTicks + 1);
  }

  public boolean hasStopped() {
    return idleTicks >= IDLE_TICKS_LIMIT;
  }
}
