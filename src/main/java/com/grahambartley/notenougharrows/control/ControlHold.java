package com.grahambartley.notenougharrows.control;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.util.math.Vec3d;

public record ControlHold(
    UUID mobId,
    Vec3d anchor,
    Optional<UUID> subjectId,
    double defendRadius,
    ControlSteering steering,
    long expiryTick) {

  public ControlHold {
    Objects.requireNonNull(mobId, "mobId");
    Objects.requireNonNull(anchor, "anchor");
    Objects.requireNonNull(subjectId, "subjectId");
    Objects.requireNonNull(steering, "steering");
    defendRadius = Math.max(0.0, defendRadius);
  }

  public static ControlHold at(
      final UUID mobId, final Vec3d anchor, final ControlSteering steering, final long expiryTick) {
    return new ControlHold(mobId, anchor, Optional.empty(), 0.0, steering, expiryTick);
  }

  public static ControlHold on(
      final UUID mobId,
      final Vec3d anchor,
      final UUID subjectId,
      final ControlSteering steering,
      final long expiryTick) {
    return new ControlHold(
        mobId,
        anchor,
        Optional.of(Objects.requireNonNull(subjectId, "subjectId")),
        0.0,
        steering,
        expiryTick);
  }

  public static ControlHold defending(
      final UUID mobId,
      final Vec3d anchor,
      final UUID subjectId,
      final double defendRadius,
      final long expiryTick) {
    return new ControlHold(
        mobId,
        anchor,
        Optional.of(Objects.requireNonNull(subjectId, "subjectId")),
        defendRadius,
        ControlSteering.DEFENDING,
        expiryTick);
  }

  public boolean hasExpired(final long tick) {
    return tick >= expiryTick;
  }
}
