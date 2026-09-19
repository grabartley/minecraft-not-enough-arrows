package com.grahambartley.notenougharrows.control;

import java.util.Optional;
import net.minecraft.util.math.Vec3d;

public enum ControlSteering {
  DRAWN(true) {
    @Override
    public Optional<Vec3d> destination(final Vec3d from, final Vec3d anchor) {
      return Optional.of(anchor);
    }
  },
  FLEEING(false) {
    @Override
    public Optional<Vec3d> destination(final Vec3d from, final Vec3d anchor) {
      final Vec3d away = from.subtract(anchor);
      if (away.lengthSquared() <= 0.0) {
        return Optional.empty();
      }
      return Optional.of(from.add(away.normalize().multiply(FLEE_DISTANCE)));
    }
  },
  WANDERING(true) {
    @Override
    public Optional<Vec3d> destination(final Vec3d from, final Vec3d anchor) {
      return Optional.empty();
    }
  };

  public static final double FLEE_DISTANCE = 12.0;

  private final boolean clearsTarget;

  ControlSteering(final boolean clearsTarget) {
    this.clearsTarget = clearsTarget;
  }

  public abstract Optional<Vec3d> destination(Vec3d from, Vec3d anchor);

  public boolean navigates() {
    return this != WANDERING;
  }

  public boolean clearsTarget() {
    return clearsTarget;
  }
}
