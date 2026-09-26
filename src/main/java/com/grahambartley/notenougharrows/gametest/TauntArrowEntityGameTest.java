package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.control.ControlHoldService;
import com.grahambartley.notenougharrows.control.ControlSteering;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class TauntArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "taunt-arrow";
  private static final BlockPos HOSTILE_STAND = new BlockPos(5, 3, 4);
  private static final BlockPos PREY_STAND = new BlockPos(3, 3, 5);

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aHostileAlreadyFightingIsDrawnToTheImpact(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final ZombieEntity hostile =
        ControlTestSupport.engagedZombieAt(context, HOSTILE_STAND, PREY_STAND);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.TAUNT_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              ControlHoldService.heldIn(context.getWorld(), hostile)
                  .filter(hold -> hold.steering() == ControlSteering.DRAWN)
                  .isPresent(),
              "A hostile already fighting should be drawn to the taunt's impact");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aDrawnHostileLetsGoOfWhatItWasFighting(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final ZombieEntity hostile =
        ControlTestSupport.engagedZombieAt(context, HOSTILE_STAND, PREY_STAND);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.TAUNT_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              hostile.getTarget() == null,
              "A taunted hostile should let go of what it was fighting");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowThatDrawsNobodyIsRecoveredRatherThanSpent(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.TAUNT_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.expectEntity(ModArrows.TAUNT_ARROW.entityType());
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aHostileFightingNobodyIsLeftAlone(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final ZombieEntity calm = ControlTestSupport.stillZombieAt(context, HOSTILE_STAND);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.TAUNT_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              ControlHoldService.heldIn(context.getWorld(), calm).isEmpty(),
              "A taunt should move attention that already existed, never create it");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowIsSpentByItsOwnTauntRatherThanEmbedding(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    ControlTestSupport.engagedZombieAt(context, HOSTILE_STAND, PREY_STAND);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.TAUNT_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.dontExpectEntity(ModArrows.TAUNT_ARROW.entityType());
          context.complete();
        });
  }
}
