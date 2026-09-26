package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.config.AllegianceArrowConfig;
import com.grahambartley.notenougharrows.control.ControlHoldService;
import com.grahambartley.notenougharrows.control.ControlSteering;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class AllegianceArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "allegiance-arrow";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);
  private static final BlockPos THREAT_STAND = new BlockPos(3, 3, 5);
  private static final int SETTLING_TICKS = 6;

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void theHostileItHitsIsTurnedToTheShootersSide(TestContext context) {
    final ZombieEntity target = ControlTestSupport.stillZombieAt(context, TARGET_STAND);
    MockPlayerSupport.fireEastStraight(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.ALLEGIANCE_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              ControlHoldService.heldIn(context.getWorld(), target)
                  .filter(hold -> hold.steering() == ControlSteering.DEFENDING)
                  .isPresent(),
              "An allegiance arrow should turn the hostile it hits into a defender");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aTurnedHostileGoesAfterWhoeverIsAttackingTheShooter(TestContext context) {
    final ServerPlayerEntity shooter =
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND);
    final ZombieEntity defender = ControlTestSupport.stillZombieAt(context, TARGET_STAND);
    final ZombieEntity threat = ControlTestSupport.stillZombieAt(context, THREAT_STAND);
    threat.setTarget(shooter);

    ControlHoldService.enlist(
        context.getWorld(), defender, shooter, AllegianceArrowConfig.defaults());

    context.runAtTick(
        SETTLING_TICKS,
        () -> {
          context.assertTrue(
              defender.getTarget() == threat,
              "A turned hostile should go after whoever is attacking the shooter");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aTurnedHostileStopsFightingTheShooter(TestContext context) {
    final ServerPlayerEntity shooter =
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND);
    final ZombieEntity defender = ControlTestSupport.stillZombieAt(context, TARGET_STAND);
    defender.setTarget(shooter);

    ControlHoldService.enlist(
        context.getWorld(), defender, shooter, AllegianceArrowConfig.defaults());

    context.runAtTick(
        SETTLING_TICKS,
        () -> {
          context.assertTrue(
              defender.getTarget() != shooter,
              "A turned hostile should stop fighting the shooter it now defends");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 80)
  public void aTurnedHostileIsHandedBackWhenTheTimeRunsOut(TestContext context) {
    final ServerPlayerEntity shooter =
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND);
    final ZombieEntity defender = ControlTestSupport.stillZombieAt(context, TARGET_STAND);

    ControlHoldService.enlist(
        context.getWorld(), defender, shooter, new AllegianceArrowConfig(5, 16.0f));

    context.runAtTick(
        30,
        () -> {
          context.assertTrue(
              ControlHoldService.heldIn(context.getWorld(), defender).isEmpty(),
              "An allegiance should be handed back once its duration runs out");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void somethingThatWasNeverHostileIsLeftAlone(TestContext context) {
    final ServerPlayerEntity shooter =
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND);
    final CowEntity bystander = ControlTestSupport.stillCowAt(context, TARGET_STAND);

    context.assertFalse(
        ControlHoldService.enlist(
            context.getWorld(), bystander, shooter, AllegianceArrowConfig.defaults()),
        "An allegiance arrow should turn only a hostile mob");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aDispensedArrowHasNobodyToDefend(TestContext context) {
    final ZombieEntity target = ControlTestSupport.stillZombieAt(context, TARGET_STAND);
    FiringRangeSupport.dispenseEast(
        context, new BlockPos(1, 3, 3), ModArrows.ALLEGIANCE_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              ControlHoldService.heldIn(context.getWorld(), target).isEmpty(),
              "A dispensed allegiance arrow has no shooter, so it should turn nobody");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aTurnedHostileKeepsFightingAnAttackerThatTurnsOnIt(TestContext context) {
    final ServerPlayerEntity shooter =
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND);
    final ZombieEntity defender = ControlTestSupport.stillZombieAt(context, TARGET_STAND);
    final ZombieEntity threat = ControlTestSupport.stillZombieAt(context, THREAT_STAND);
    threat.setTarget(shooter);
    ControlHoldService.enlist(
        context.getWorld(), defender, shooter, AllegianceArrowConfig.defaults());

    context.runAtTick(
        SETTLING_TICKS,
        () -> {
          threat.setTarget(defender);
        });
    context.runAtTick(
        SETTLING_TICKS * 3,
        () -> {
          context.assertTrue(
              defender.getTarget() == threat,
              "A turned hostile should keep fighting an attacker that turns on it, as a wolf does");
          context.complete();
        });
  }
}
