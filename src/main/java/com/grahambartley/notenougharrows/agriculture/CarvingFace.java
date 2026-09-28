package com.grahambartley.notenougharrows.agriculture;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class CarvingFace {

  private CarvingFace() {}

  public static Direction towardTheShooter(final Vec3d flight) {
    final Direction heading =
        Math.abs(flight.x) >= Math.abs(flight.z)
            ? (flight.x >= 0 ? Direction.EAST : Direction.WEST)
            : (flight.z >= 0 ? Direction.SOUTH : Direction.NORTH);
    return heading.getOpposite();
  }

  public static Direction carved(final Direction struckFace, final Direction towardTheShooter) {
    return struckFace.getAxis() == Direction.Axis.Y ? towardTheShooter : struckFace;
  }
}
