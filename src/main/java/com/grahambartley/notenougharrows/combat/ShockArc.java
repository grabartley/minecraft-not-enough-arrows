package com.grahambartley.notenougharrows.combat;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.math.Vec3d;

public final class ShockArc {

  private ShockArc() {}

  public static <T> Optional<T> nearest(
      final Vec3d origin,
      final List<T> candidates,
      final Function<T, Vec3d> position,
      final double radius) {
    Objects.requireNonNull(position, "position");

    if (origin == null || candidates == null || radius <= 0.0) {
      return Optional.empty();
    }

    final double limit = radius * radius;
    T closest = null;
    double closestDistance = Double.MAX_VALUE;
    for (final T candidate : candidates) {
      final Vec3d candidatePosition = position.apply(candidate);
      if (candidatePosition == null) {
        continue;
      }
      final double distance = origin.squaredDistanceTo(candidatePosition);
      if (distance <= limit && distance < closestDistance) {
        closest = candidate;
        closestDistance = distance;
      }
    }
    return Optional.ofNullable(closest);
  }
}
