package com.grahambartley.notenougharrows.cloud;

import java.util.Objects;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public record TimedCloud(Vec3d center, double radius, long expiryTick) {

  public TimedCloud {
    Objects.requireNonNull(center, "center");
    radius = Math.max(0.0, radius);
  }

  public boolean hasExpired(final long tick) {
    return tick >= expiryTick;
  }

  public boolean contains(final Vec3d point) {
    return point != null && center.squaredDistanceTo(point) <= radius * radius;
  }

  public Box bounds() {
    return new Box(center, center).expand(radius);
  }
}
