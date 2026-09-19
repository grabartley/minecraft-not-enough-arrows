package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.TargetingArrowConfig;
import com.grahambartley.notenougharrows.control.ControlHoldService;
import com.grahambartley.notenougharrows.control.ControlSteering;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class ControlHoldServiceGameTest implements FabricGameTest {
  private static final String BATCH = "control-holds";
  private static final BlockPos HOSTILE_STAND = new BlockPos(3, 3, 3);
  private static final BlockPos PREY_STAND = new BlockPos(3, 3, 5);
  private static final int BRIEF_HOLD_TICKS = 5;
  private static final int AFTER_A_BRIEF_HOLD = 20;
  private static final int KEEPING_IT_TICKS = 20;

  private static TargetingArrowConfig holdingFor(final int ticks) {
    return new TargetingArrowConfig(8.0f, ticks, 8.0f, ticks, ticks);
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aHoldIsHandedBackOnceItsDurationRunsOut(TestContext context) {
    final ZombieEntity hostile = ControlTestSupport.stillZombieAt(context, HOSTILE_STAND);
    final Vec3d centre = hostile.getBoundingBox().getCenter();
    ControlHoldService.repel(context.getWorld(), centre, holdingFor(BRIEF_HOLD_TICKS));

    context.runAtTick(
        AFTER_A_BRIEF_HOLD,
        () -> {
          context.assertTrue(
              ControlHoldService.heldIn(context.getWorld(), hostile).isEmpty(),
              "A hold should be handed back once its duration runs out, never left standing");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 80)
  public void aHandedBackMobKeepsTheTargetItPicksUpAfterwards(TestContext context) {
    final ZombieEntity hostile = ControlTestSupport.stillZombieAt(context, HOSTILE_STAND);
    ControlHoldService.daze(context.getWorld(), hostile, holdingFor(BRIEF_HOLD_TICKS));

    context.runAtTick(
        AFTER_A_BRIEF_HOLD,
        () -> hostile.setTarget(FiringRangeSupport.liveTargetOnPedestalAt(context, PREY_STAND)));

    context.runAtTick(
        AFTER_A_BRIEF_HOLD + KEEPING_IT_TICKS,
        () -> {
          context.assertTrue(
              hostile.getTarget() != null,
              "Once handed back, nothing should keep clearing the mob's target, but it was cleared");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aSecondHoldOnOneMobReplacesTheFirstRatherThanQueuing(TestContext context) {
    final ZombieEntity hostile = ControlTestSupport.stillZombieAt(context, HOSTILE_STAND);
    final Vec3d centre = hostile.getBoundingBox().getCenter();
    ControlHoldService.repel(context.getWorld(), centre, holdingFor(200));
    ControlHoldService.daze(context.getWorld(), hostile, holdingFor(200));

    context.assertTrue(
        ControlHoldService.heldIn(context.getWorld(), hostile)
            .filter(hold -> hold.steering() == ControlSteering.WANDERING)
            .isPresent(),
        "The second hold on one mob should replace the first rather than queue behind it");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aDurationOfZeroHoldsNothingAtAll(TestContext context) {
    final ZombieEntity hostile = ControlTestSupport.stillZombieAt(context, HOSTILE_STAND);
    final Vec3d centre = hostile.getBoundingBox().getCenter();

    context.assertEquals(
        ControlHoldService.taunt(context.getWorld(), centre, holdingFor(0)),
        0,
        "A taunt configured to last no time should hold nothing");
    context.assertEquals(
        ControlHoldService.repel(context.getWorld(), centre, holdingFor(0)),
        0,
        "A repel configured to last no time should hold nothing");
    context.assertFalse(
        ControlHoldService.daze(context.getWorld(), hostile, holdingFor(0)),
        "A daze configured to last no time should hold nothing");
    context.complete();
  }
}
