package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.entity.RailgunArrowEntity;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class RailgunArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "railgun-arrow";
  private static final BlockPos LAUNCH_STAND = new BlockPos(3, 3, 3);
  private static final Vec3d STRAIGHT_UP = new Vec3d(0.0, 1.0, 0.0);
  private static final int LAUNCHED = 2;
  private static final int WELL_INTO_THE_CLIMB = 8;
  private static final int LATER_STILL = 24;

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = 60,
      skyAccess = true)
  public void anArrowTakesItsLaunchBoostOnce(TestContext context) {
    final RailgunArrowEntity arrow =
        CombatTestSupport.driftingFrom(
            context, ModArrows.RAILGUN_ARROW.entityType(), LAUNCH_STAND, STRAIGHT_UP);

    context.runAtTick(
        LAUNCHED,
        () -> {
          context.assertTrue(arrow.launched(), "The arrow should have taken its launch boost");
          final double boosted = arrow.getVelocity().length();
          context.assertTrue(
              boosted > STRAIGHT_UP.length(),
              "The boost should have made it faster than it was launched at, it was " + boosted);
          context.runAtTick(
              LAUNCHED + 1,
              () -> {
                context.assertTrue(
                    arrow.getVelocity().length() <= boosted,
                    "A railgun arrow must never boost itself twice, speed went from "
                        + boosted
                        + " to "
                        + arrow.getVelocity().length());
                context.complete();
              });
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = 80,
      skyAccess = true)
  public void anArrowAlwaysFalls(TestContext context) {
    final RailgunArrowEntity arrow =
        CombatTestSupport.driftingFrom(
            context, ModArrows.RAILGUN_ARROW.entityType(), LAUNCH_STAND, STRAIGHT_UP);

    context.runAtTick(
        WELL_INTO_THE_CLIMB,
        () -> {
          final double climbing = arrow.getVelocity().y;
          context.runAtTick(
              LATER_STILL,
              () -> {
                context.assertTrue(
                    arrow.getVelocity().y < climbing,
                    "A railgun arrow must always be pulled down, its rise went from "
                        + climbing
                        + " to "
                        + arrow.getVelocity().y);
                context.complete();
              });
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = 60,
      skyAccess = true)
  public void anArrowKeepsTheDirectionItWasLaunchedIn(TestContext context) {
    final RailgunArrowEntity arrow =
        CombatTestSupport.driftingFrom(
            context, ModArrows.RAILGUN_ARROW.entityType(), LAUNCH_STAND, STRAIGHT_UP);

    context.runAtTick(
        LAUNCHED,
        () -> {
          context.assertTrue(
              Math.abs(arrow.getVelocity().x) < 0.001 && Math.abs(arrow.getVelocity().z) < 0.001,
              "A railgun boost should not steer the arrow, velocity was " + arrow.getVelocity());
          context.complete();
        });
  }
}
