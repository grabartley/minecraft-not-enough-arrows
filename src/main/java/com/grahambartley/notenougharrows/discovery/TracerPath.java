package com.grahambartley.notenougharrows.discovery;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.Vec3d;

public final class TracerPath {
  public static final int MAX_POINTS = 256;
  static final double MIN_STEP_SQUARED = 0.01;

  private final List<Vec3d> points = new ArrayList<>();

  public void record(final Vec3d point) {
    if (point == null || points.size() >= MAX_POINTS) {
      return;
    }
    if (!points.isEmpty() && points.getLast().squaredDistanceTo(point) < MIN_STEP_SQUARED) {
      return;
    }
    points.add(point);
  }

  public List<Vec3d> points() {
    return List.copyOf(points);
  }

  public boolean isDrawable() {
    return points.size() >= 2;
  }
}
