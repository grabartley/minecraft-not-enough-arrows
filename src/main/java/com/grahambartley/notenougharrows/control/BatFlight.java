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
    final double dx = target.getX() + 0.5 - bat.getX();
    final double dy = target.getY() + 0.1 - bat.getY();
    final double dz = target.getZ() + 0.5 - bat.getZ();
    final Vec3d velocity = bat.getVelocity();
    final Vec3d steered =
        velocity.add(
            (Math.signum(dx) * HORIZONTAL_PULL - velocity.x) * RESPONSIVENESS,
            (Math.signum(dy) * VERTICAL_PULL - velocity.y) * RESPONSIVENESS,
            (Math.signum(dz) * HORIZONTAL_PULL - velocity.z) * RESPONSIVENESS);
    bat.setVelocity(steered);
    final float heading =
        (float) (MathHelper.atan2(steered.z, steered.x) * MathHelper.DEGREES_PER_RADIAN) - 90.0f;
    bat.forwardSpeed = WINGBEAT;
    bat.setYaw(bat.getYaw() + MathHelper.wrapDegrees(heading - bat.getYaw()));
  }

  public static void forgetAll() {
    STEERED_AT.clear();
  }
}
