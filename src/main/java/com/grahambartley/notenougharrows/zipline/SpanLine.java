package com.grahambartley.notenougharrows.zipline;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class SpanLine {
  private static final double TIE = 1.0E-9;

  private SpanLine() {}

  public static List<BlockPos> between(final BlockPos from, final BlockPos to) {
    if (from == null || to == null || from.equals(to)) {
      return List.of();
    }
    final int[] cell = {from.getX(), from.getY(), from.getZ()};
    final int[] end = {to.getX(), to.getY(), to.getZ()};
    final int[] step = new int[3];
    final double[] nextCrossing = new double[3];
    final double[] crossingInterval = new double[3];
    for (int axis = 0; axis < 3; axis++) {
      final int delta = end[axis] - cell[axis];
      step[axis] = Integer.signum(delta);
      crossingInterval[axis] = delta == 0 ? Double.POSITIVE_INFINITY : 1.0 / Math.abs(delta);
      nextCrossing[axis] = crossingInterval[axis] / 2.0;
    }
    final List<BlockPos> line = new ArrayList<>();
    while (true) {
      final int axis = nextAxis(nextCrossing);
      cell[axis] += step[axis];
      nextCrossing[axis] += crossingInterval[axis];
      if (cell[0] == end[0] && cell[1] == end[1] && cell[2] == end[2]) {
        return List.copyOf(line);
      }
      line.add(new BlockPos(cell[0], cell[1], cell[2]));
    }
  }

  public static double separation(final BlockPos from, final BlockPos to) {
    return Vec3d.ofCenter(from).distanceTo(Vec3d.ofCenter(to));
  }

  public static Direction.Axis axisOf(final BlockPos from, final BlockPos to) {
    final int dx = Math.abs(to.getX() - from.getX());
    final int dy = Math.abs(to.getY() - from.getY());
    final int dz = Math.abs(to.getZ() - from.getZ());
    if (dy >= dx && dy >= dz) {
      return Direction.Axis.Y;
    }
    return dx >= dz ? Direction.Axis.X : Direction.Axis.Z;
  }

  private static int nextAxis(final double[] nextCrossing) {
    int nearest = 0;
    for (int axis = 1; axis < 3; axis++) {
      if (nextCrossing[axis] < nextCrossing[nearest] - TIE) {
        nearest = axis;
      }
    }
    return nearest;
  }
}
