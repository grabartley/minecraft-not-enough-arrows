package com.grahambartley.notenougharrows.control;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.util.math.Vec3d;

public record ControlHold(
    UUID mobId,
    Vec3d anchor,
    Optional<UUID> subjectId,
    double reach,
    ControlSteering steering,
    long expiryTick) {

  public ControlHold {
    Objects.requireNonNull(mobId, "mobId");
    Objects.requireNonNull(anchor, "anchor");
    Objects.requireNonNull(subjectId, "subjectId");
    Objects.requireNonNull(steering, "steering");
    reach = Math.max(0.0, reach);
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

  public static ControlHold fleeing(
      final UUID mobId, final Vec3d anchor, final double fleeDistance, final long expiryTick) {
    return new ControlHold(
        mobId, anchor, Optional.empty(), fleeDistance, ControlSteering.FLEEING, expiryTick);
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

  public boolean hasFledFarEnough(final Vec3d position) {
    return steering == ControlSteering.FLEEING
        && reach > 0.0
        && Math.hypot(position.x - anchor.x, position.z - anchor.z) >= reach;
  }

  public boolean hasExpired(final long tick) {
    return tick >= expiryTick;
  }
}
