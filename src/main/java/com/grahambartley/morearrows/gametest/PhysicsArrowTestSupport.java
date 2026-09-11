package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.config.PhysicsArrowConfig;
import java.util.List;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

final class PhysicsArrowTestSupport {
  static final BlockPos TARGET_BLOCK = FiringRangeSupport.BACKSTOP;
  static final BlockPos LANDING_BLOCK = TARGET_BLOCK.down();
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
}
