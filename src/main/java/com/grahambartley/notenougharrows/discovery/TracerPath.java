package com.grahambartley.notenougharrows.discovery;

import com.grahambartley.notenougharrows.network.RevealPayloads.TracerPathS2CPayload;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.Vec3d;

public final class TracerPath {
  public static final int MAX_POINTS = TracerPathS2CPayload.MAX_POINTS;
  static final double MIN_STEP_SQUARED = 0.01;

  private final List<Vec3d> points = new ArrayList<>();

  public void record(final Vec3d point) {
    if (point == null
        || (!points.isEmpty() && points.getLast().squaredDistanceTo(point) < MIN_STEP_SQUARED)) {
      return;
    }
    if (points.size() >= MAX_POINTS) {
      points.set(points.size() - 1, point);
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
