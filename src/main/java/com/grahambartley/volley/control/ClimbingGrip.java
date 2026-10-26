package com.grahambartley.volley.control;

public final class ClimbingGrip {

  private ClimbingGrip() {}

  public static boolean shouldLetGo(final double climberY, final double destinationY) {
    return climberY > destinationY;
  }
}
