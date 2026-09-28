package com.grahambartley.notenougharrows.updraft;

import java.util.Objects;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public record UpdraftColumn(
    Vec3d base, double radius, int heightBlocks, float strength, long expiryTick) {
  public static final double RADIUS = 1.5;
  public static final double FLOOR_SLACK = 0.5;

  public UpdraftColumn {
    Objects.requireNonNull(base, "A column needs the point it rises from");
    radius = Math.max(0.0, radius);
    heightBlocks = Math.max(0, heightBlocks);
    strength = Math.max(0.0f, strength);
  }

  public boolean hasExpired(final long tick) {
    return tick >= expiryTick;
  }

  public double top() {
    return base.getY() + heightBlocks;
  }

  public boolean contains(final Vec3d feet) {
    if (feet == null) {
      return false;
    }
    final double dx = feet.getX() - base.getX();
    final double dz = feet.getZ() - base.getZ();
    return dx * dx + dz * dz <= radius * radius
        && feet.getY() >= base.getY() - FLOOR_SLACK
        && feet.getY() < top();
  }

  public Box bounds() {
    return new Box(
        base.getX() - radius,
        base.getY() - FLOOR_SLACK,
        base.getZ() - radius,
        base.getX() + radius,
        top(),
        base.getZ() + radius);
  }

  public Vec3d lift(final Vec3d velocity) {
    return new Vec3d(velocity.getX(), Math.max(velocity.getY(), strength), velocity.getZ());
  }
}
