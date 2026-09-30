package com.grahambartley.notenougharrows.control;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class GhastCourse {
  private static final double WORTH_FLYING = 1.0;

  private GhastCourse() {}

  public static Optional<Vec3d> plan(
      final Vec3d from, final Vec3d destination, final double lift, final Predicate<Vec3d> fitsAt) {
    final Vec3d over = destination.add(0.0, lift, 0.0);
    final List<Vec3d> waypoints =
        List.of(
            destination,
            over,
            new Vec3d(from.x, over.y, from.z),
            new Vec3d(over.x, from.y, over.z));
    for (final Vec3d waypoint : waypoints) {
      if (waypoint.squaredDistanceTo(from) >= WORTH_FLYING * WORTH_FLYING
          && isClear(from, waypoint, fitsAt)) {
        return Optional.of(waypoint);
      }
    }
    return farthestClear(from, over, fitsAt);
  }

  public static Predicate<Vec3d> fitsAt(final Entity ghast) {
    return position ->
        ghast
            .getWorld()
            .isSpaceEmpty(ghast, ghast.getBoundingBox().offset(position.subtract(ghast.getPos())));
  }

  public static boolean isClear(final Vec3d from, final Vec3d to, final Predicate<Vec3d> fitsAt) {
    final Vec3d line = to.subtract(from);
    final Vec3d step = line.normalize();
    final int steps = MathHelper.ceil(line.length());
    for (int i = 1; i < steps; i++) {
      if (!fitsAt.test(from.add(step.multiply(i)))) {
        return false;
      }
    }
    return true;
  }

  private static Optional<Vec3d> farthestClear(
      final Vec3d from, final Vec3d toward, final Predicate<Vec3d> fitsAt) {
    final Vec3d line = toward.subtract(from);
    final Vec3d step = line.normalize();
    Vec3d reached = null;
    for (int i = 1; i <= line.length(); i++) {
      final Vec3d next = from.add(step.multiply(i));
      if (!fitsAt.test(next)) {
        break;
      }
      reached = next;
    }
    return Optional.ofNullable(reached);
  }
}
