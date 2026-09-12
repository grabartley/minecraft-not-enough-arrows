package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.rope.RopeService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class RopeClimbGameTest implements FabricGameTest {
  private static final String BATCH = "rope-climb";
  private static final int LONGER_THAN_THE_SHAFT = 32;
  private static final BlockPos ON_THE_ROPE = new BlockPos(2, 12, 2);
  private static final BlockPos IN_OPEN_AIR = new BlockPos(0, 12, 0);
  private static final int FALLING_TICKS = 10;
  private static final int SETTLING_TICK = 1;
  private static final double CLIMBING_DESCENT_PER_TICK = 0.15;
  private static final double MEASUREMENT_SLACK = 0.5;

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void anEntityHangingInARopeIsClimbing(TestContext context) {
    dropRope(context);
    final PigEntity climber = spawn(context, ON_THE_ROPE);

    context.runAtTick(
        SETTLING_TICK,
        () -> {
          context.assertTrue(
              climber.isClimbing(),
              "An entity standing in a rope should be climbing, which is what lets it go up and"
                  + " down under its own power");
          context.complete();
        });
  }

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void anEntityInOpenAirIsNotClimbing(TestContext context) {
    final PigEntity faller = spawn(context, IN_OPEN_AIR);

    context.runAtTick(
        SETTLING_TICK,
        () -> {
          context.assertFalse(faller.isClimbing(), "Open air is not something an entity can climb");
          context.complete();
        });
  }

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void anEntityDescendsARopeSlowerThanItFallsBesideIt(TestContext context) {
    dropRope(context);
    final PigEntity climber = spawn(context, ON_THE_ROPE);
    final PigEntity faller = spawn(context, IN_OPEN_AIR);
    final double climberStartY = climber.getY();
    final double fallerStartY = faller.getY();

    context.runAtTick(
        FALLING_TICKS,
        () -> {
          final double climbed = climberStartY - climber.getY();
          final double fallen = fallerStartY - faller.getY();
          context.assertTrue(
              climbed <= CLIMBING_DESCENT_PER_TICK * FALLING_TICKS + MEASUREMENT_SLACK,
              "A rope should lower an entity at climbing speed, but it dropped " + climbed);
          context.assertTrue(
              fallen > climbed,
              "The same entity falling beside the rope should drop further than it does on the"
                  + " rope, but it fell "
                  + fallen
                  + " against "
                  + climbed);
          context.complete();
        });
  }

  private static PigEntity spawn(final TestContext context, final BlockPos relativePos) {
    return context.spawnEntity(EntityType.PIG, relativePos);
  }

  private static void dropRope(final TestContext context) {
    RopeTestSupport.raiseCeiling(context);
    RopeService.drop(
        context.getWorld(),
        context.getAbsolutePos(RopeTestSupport.CEILING),
        null,
        LONGER_THAN_THE_SHAFT);
  }
}
