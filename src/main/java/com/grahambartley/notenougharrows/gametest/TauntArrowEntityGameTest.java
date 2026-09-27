package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.control.ControlHoldService;
import com.grahambartley.notenougharrows.control.ControlSteering;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

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

  @GameTest(
      templateName = CombatTestSupport.LONG_RANGE,
      batchId = BATCH + "-long-range",
      tickLimit = 60)
  public void hittingAMobPullsEveryoneHuntingTheShooterOntoIt(TestContext context) {
    final ServerPlayerEntity shooter =
        MockPlayerSupport.playerAt(context, CombatTestSupport.LONG_RANGE_SHOOTER_STAND);
    shooter.changeGameMode(GameMode.SURVIVAL);
    final CowEntity struck = ControlTestSupport.sturdyStillCow(context, new BlockPos(10, 2, 3));
    final ZombieEntity hunter = ControlTestSupport.stillZombieAt(context, new BlockPos(1, 3, 6));
    hunter.setTarget(shooter);
    context.assertTrue(
        hunter.squaredDistanceTo(struck) > 8.0 * 8.0,
        "The hunter must stand beyond the taunt radius of the impact, or this proves nothing");

    MockPlayerSupport.fireEastStraight(context, shooter, ModArrows.TAUNT_ARROW.item());

    context.runAtTick(
        30,
        () -> {
          context.assertTrue(
              hunter.getTarget() == struck,
              "A taunt arrow that hits a mob should pull everything hunting the shooter onto it,"
                  + " however far from the impact they are");
          context.complete();
        });
  }
}
