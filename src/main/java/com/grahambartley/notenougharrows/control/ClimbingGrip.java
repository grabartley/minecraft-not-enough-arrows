package com.grahambartley.notenougharrows.control;

public final class ClimbingGrip {

  private ClimbingGrip() {}

  public static boolean shouldLetGo(
      final boolean climbing, final double climberY, final double destinationY) {
    return climbing && climberY > destinationY;
  }
}
