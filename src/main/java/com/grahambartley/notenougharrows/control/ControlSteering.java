package com.grahambartley.notenougharrows.control;

import java.util.Optional;
import net.minecraft.util.math.Vec3d;

public enum ControlSteering {
  DRAWN(TargetPolicy.AIM_AT_SUBJECT) {
    @Override
    public Optional<Vec3d> destination(final Vec3d from, final Vec3d anchor) {
      return Optional.of(anchor);
    }
  },
  FLEEING(TargetPolicy.DROP_UNLESS_CORNERED) {
    @Override
    public Optional<Vec3d> destination(final Vec3d from, final Vec3d anchor) {
      final Vec3d away = new Vec3d(from.getX() - anchor.getX(), 0.0, from.getZ() - anchor.getZ());
      if (away.lengthSquared() <= 0.0) {
        return Optional.empty();
      }
      return Optional.of(from.add(away.normalize().multiply(FLEE_DISTANCE)));
    }
  },
  DEFENDING(TargetPolicy.DEFEND_SUBJECT) {
    @Override
    public Optional<Vec3d> destination(final Vec3d from, final Vec3d anchor) {
      return Optional.empty();
    }
  };

  public static final double FLEE_DISTANCE = 12.0;

  private final TargetPolicy targetPolicy;

  ControlSteering(final TargetPolicy targetPolicy) {
    this.targetPolicy = targetPolicy;
  }

  public abstract Optional<Vec3d> destination(Vec3d from, Vec3d anchor);

  public TargetPolicy targetPolicy() {
    return targetPolicy;
  }

  public boolean navigates() {
    return this != DEFENDING;
  }
}
