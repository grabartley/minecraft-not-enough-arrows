package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.config.PhysicsArrowConfig;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

final class PhysicsArrowTestSupport {
  static final String TEMPLATE = "more-arrows:fire_pad";
  static final BlockPos SHOOTER_STAND = new BlockPos(1, 2, 3);
  static final BlockPos TARGET_BLOCK = new BlockPos(6, 3, 3);
  static final BlockPos LANDING_BLOCK = new BlockPos(6, 2, 3);
  static final Vec3d OUT_OF_THE_LANE = new Vec3d(1.5, 2.0, 5.5);
  static final int IMPACT_TICK = 5;
  static final int SETTLED_TICK = 40;

  private PhysicsArrowTestSupport() {}

  static PhysicsArrowConfig physics(
      final int gravityImpactRadius, final List<String> gravityBlockExclusions) {
    return new PhysicsArrowConfig(
        gravityImpactRadius,
        gravityBlockExclusions,
        PhysicsArrowConfig.DEFAULT_RICOCHET_BOUNCE_COUNT,
        PhysicsArrowConfig.DEFAULT_RICOCHET_RETAINS_DAMAGE);
  }

  static void stepOutOfTheLane(final TestContext context, final ServerPlayerEntity shooter) {
    MockPlayerSupport.moveTo(context, shooter, OUT_OF_THE_LANE);
  }

  static <E extends Entity> E firedArrow(final TestContext context, final Class<E> type) {
    return context.getWorld().getEntitiesByClass(type, context.getTestBox(), arrow -> true).stream()
        .findFirst()
        .orElse(null);
  }
}
