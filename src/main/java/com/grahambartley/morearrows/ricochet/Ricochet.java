package com.grahambartley.morearrows.ricochet;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class Ricochet {
  public static final double SPEED_RETENTION = 0.8;
  public static final double SURFACE_CLEARANCE = 0.1;

  private Ricochet() {}

  public static boolean canBounce(final int bouncesUsed, final int bounceCount) {
    return bouncesUsed >= 0 && bouncesUsed < bounceCount;
  }

  public static Vec3d deflect(final Vec3d velocity, final Direction surface) {
    if (velocity == null || surface == null) {
      return Vec3d.ZERO;
    }
    return reflect(velocity, Vec3d.of(surface.getVector())).multiply(SPEED_RETENTION);
  }

  public static Vec3d clearOf(final Vec3d impact, final Direction surface) {
    if (impact == null || surface == null) {
      return impact == null ? Vec3d.ZERO : impact;
    }
    return impact.add(Vec3d.of(surface.getVector()).multiply(SURFACE_CLEARANCE));
  }

  public static double damageAfterBounce(final double damage, final boolean retainsDamage) {
    if (damage <= 0.0) {
      return 0.0;
    }
    return retainsDamage ? damage : damage * SPEED_RETENTION;
  }

  private static Vec3d reflect(final Vec3d velocity, final Vec3d normal) {
    final double approach = velocity.dotProduct(normal);
    return approach >= 0.0 ? velocity : velocity.subtract(normal.multiply(2.0 * approach));
  }
}
