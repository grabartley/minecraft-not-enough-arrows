package com.grahambartley.notenougharrows.control;

public final class ClimbingGrip {

  private ClimbingGrip() {}

  public static boolean shouldLetGo(
      final boolean onGround, final double climberY, final double destinationY) {
    return !onGround && climberY > destinationY;
  }
}
