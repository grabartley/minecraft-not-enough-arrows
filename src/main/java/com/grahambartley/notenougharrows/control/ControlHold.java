package com.grahambartley.notenougharrows.control;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.util.math.Vec3d;

public record ControlHold(UUID mobId, Vec3d anchor, ControlSteering steering, long expiryTick) {

  public ControlHold {
    Objects.requireNonNull(mobId, "mobId");
    Objects.requireNonNull(anchor, "anchor");
    Objects.requireNonNull(steering, "steering");
  }

  public boolean hasExpired(final long tick) {
    return tick >= expiryTick;
  }
}
