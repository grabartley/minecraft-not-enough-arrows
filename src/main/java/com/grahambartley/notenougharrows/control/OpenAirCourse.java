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
    BlockPos best = BlockPos.ofFloored(from);
    double bestReach = 0.0;
    for (final Vec3d line : lines) {
      final BlockPos reached = lastOpen(from, line, isAir);
      final double reach = Vec3d.ofBottomCenter(reached).squaredDistanceTo(from);
      if (reach > bestReach) {
        best = reached;
        bestReach = reach;
      }
      if (reached.equals(BlockPos.ofFloored(line))) {
        return best;
      }
    }
    return best;
  }

  private static BlockPos lastOpen(
      final Vec3d from, final Vec3d to, final Predicate<BlockPos> isAir) {
    final Vec3d step = to.subtract(from);
    final double length = step.length();
    BlockPos open = BlockPos.ofFloored(from);
    for (double reached = 1.0; reached <= length; reached += 1.0) {
      final BlockPos next = BlockPos.ofFloored(from.add(step.multiply(reached / length)));
      if (!isAir.test(next)) {
        break;
      }
      open = next;
    }
    return open;
  }
}
