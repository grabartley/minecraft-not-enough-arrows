package com.grahambartley.notenougharrows.control;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class BatFlight {
  public static final int STEERING_GRACE_TICKS = 2;

  private static final double HORIZONTAL_PULL = 0.5;
  private static final double VERTICAL_PULL = 0.7;
  private static final double RESPONSIVENESS = 0.1;
  private static final float WINGBEAT = 0.5f;
  private static final Map<UUID, Long> STEERED_AT = new HashMap<>();

  private BatFlight() {}

  public static void steer(final BatEntity bat, final BlockPos toward) {
    bat.hangingPosition = toward;
    STEERED_AT.put(bat.getUuid(), bat.getWorld().getTime());
  }

  public static boolean isSteered(final BatEntity bat) {
    final Long at = STEERED_AT.get(bat.getUuid());
    if (at == null) {
      return false;
    }
    if (bat.getWorld().getTime() - at > STEERING_GRACE_TICKS) {
      STEERED_AT.remove(bat.getUuid());
      return false;
    }
    return bat.hangingPosition != null;
  }

  public static void flyToward(final BatEntity bat, final BlockPos target) {
    bat.setRoosting(false);
    final Vec3d steered =
        steered(
            bat.getVelocity(),
            new Vec3d(
                target.getX() + 0.5 - bat.getX(),
                target.getY() + 0.1 - bat.getY(),
                target.getZ() + 0.5 - bat.getZ()));
    bat.setVelocity(steered);
    final float heading =
        (float) (MathHelper.atan2(steered.z, steered.x) * MathHelper.DEGREES_PER_RADIAN) - 90.0f;
    bat.forwardSpeed = WINGBEAT;
    bat.setYaw(bat.getYaw() + MathHelper.wrapDegrees(heading - bat.getYaw()));
  }

  static Vec3d steered(final Vec3d velocity, final Vec3d toTarget) {
    final Vec3d heading = new Vec3d(toTarget.x, 0.0, toTarget.z).normalize();
    return velocity.add(
        (heading.x * HORIZONTAL_PULL - velocity.x) * RESPONSIVENESS,
        (Math.signum(toTarget.y) * VERTICAL_PULL - velocity.y) * RESPONSIVENESS,
        (heading.z * HORIZONTAL_PULL - velocity.z) * RESPONSIVENESS);
  }

  public static void forgetAll() {
    STEERED_AT.clear();
  }
}
