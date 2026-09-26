package com.grahambartley.notenougharrows.control;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.util.math.Vec3d;

public record ControlHold(
    UUID mobId, Vec3d anchor, Optional<UUID> subjectId, ControlSteering steering, long expiryTick) {

  public ControlHold {
    Objects.requireNonNull(mobId, "mobId");
    Objects.requireNonNull(anchor, "anchor");
    Objects.requireNonNull(subjectId, "subjectId");
    Objects.requireNonNull(steering, "steering");
  }

  public static ControlHold at(
      final UUID mobId, final Vec3d anchor, final ControlSteering steering, final long expiryTick) {
    return new ControlHold(mobId, anchor, Optional.empty(), steering, expiryTick);
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
        steering,
        expiryTick);
  }

  public boolean hasExpired(final long tick) {
    return tick >= expiryTick;
  }
}
