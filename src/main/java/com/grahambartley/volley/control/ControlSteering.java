package com.grahambartley.volley.control;

import java.util.Optional;
import net.minecraft.util.math.Vec3d;

public enum ControlSteering {
  DRAWN {
    @Override
    public Optional<Vec3d> destination(final Vec3d from, final Vec3d anchor) {
      return Optional.of(anchor);
    }
  },
  FLEEING {
    @Override
    public Optional<Vec3d> destination(final Vec3d from, final Vec3d anchor) {
      final Vec3d away = new Vec3d(from.getX() - anchor.getX(), 0.0, from.getZ() - anchor.getZ());
      if (away.lengthSquared() <= 0.0) {
        return Optional.empty();
      }
      return Optional.of(from.add(away.normalize().multiply(FLEE_DISTANCE)));
    }
  },
  DEFENDING {
    @Override
    public Optional<Vec3d> destination(final Vec3d from, final Vec3d anchor) {
      return Optional.empty();
    }
  };

  public static final double FLEE_DISTANCE = 12.0;
  public static final double STEERING_SPEED = 1.1;
  public static final double FLEEING_SPEED = 1.5;

  public abstract Optional<Vec3d> destination(Vec3d from, Vec3d anchor);

  public boolean retargetsEveryTick() {
    return this != FLEEING;
  }

  public double speed() {
    return this == FLEEING ? FLEEING_SPEED : STEERING_SPEED;
  }

  public boolean navigates() {
    return this != DEFENDING;
  }
}
