package com.grahambartley.notenougharrows.control;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class OpenAirCourse {
  private OpenAirCourse() {}

  public static BlockPos toward(
      final Vec3d from, final Vec3d destination, final Predicate<BlockPos> isAir) {
    final List<Vec3d> lines =
        List.of(
            destination,
            new Vec3d(destination.x, from.y, from.z),
            new Vec3d(from.x, from.y, destination.z));
    Reach best = new Reach(BlockPos.ofFloored(from), 0.0, false);
    for (final Vec3d line : lines) {
      if (line.squaredDistanceTo(from) < 1.0) {
        continue;
      }
      final Reach reach = lastOpen(from, line, isAir);
      if (reach.distance() > best.distance()) {
        best = reach;
      }
      if (reach.clear()) {
        return best.open();
      }
    }
    return best.open();
  }

  private static Reach lastOpen(final Vec3d from, final Vec3d to, final Predicate<BlockPos> isAir) {
    final Vec3d step = to.subtract(from);
    final double length = step.length();
    BlockPos open = BlockPos.ofFloored(from);
    double reached = 0.0;
    for (double next = 1.0; next <= length; next += 1.0) {
      final BlockPos ahead = BlockPos.ofFloored(from.add(step.multiply(next / length)));
      if (!isAir.test(ahead)) {
        return new Reach(open, reached, false);
      }
      open = ahead;
      reached = next;
    }
    return new Reach(open, reached, true);
  }

  private record Reach(BlockPos open, double distance, boolean clear) {}
}
