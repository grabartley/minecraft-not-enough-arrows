package com.grahambartley.notenougharrows.reveal;

import java.util.Locale;

public enum CompassPoint {
  SOUTH,
  SOUTH_WEST,
  WEST,
  NORTH_WEST,
  NORTH,
  NORTH_EAST,
  EAST,
  SOUTH_EAST;

  private static final double SECTOR_DEGREES = 360.0 / 8;

  public static CompassPoint toward(final double deltaX, final double deltaZ) {
    final double degrees = Math.toDegrees(Math.atan2(-deltaX, deltaZ));
    final int sector = Math.floorMod(Math.round(degrees / SECTOR_DEGREES), 8);
    return values()[sector];
  }

  public String translationKey() {
    return "direction.not-enough-arrows." + name().toLowerCase(Locale.ROOT);
  }
}
