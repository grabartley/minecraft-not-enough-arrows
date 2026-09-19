package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class LifestealArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "lifesteal-arrow";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);
  private static final BlockPos DISPENSER_STAND = new BlockPos(1, 3, 3);
  private static final float A_WOUNDED_SHOOTER = 4.0f;
  private static final float A_SINGLE_HEART = 2.0f;

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aHitHealsTheWoundedShooter(TestContext context) {
    FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_STAND);
    final ServerPlayerEntity shooter =
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND);
    shooter.setHealth(A_WOUNDED_SHOOTER);
    MockPlayerSupport.fireEastStraight(context, shooter, ModArrows.LIFESTEAL_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              shooter.getHealth() > A_WOUNDED_SHOOTER,
              "A lifesteal hit should heal the shooter, health stayed at " + shooter.getHealth());
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aShooterAtFullHealthGainsNothing(TestContext context) {
    FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_STAND);
    final ServerPlayerEntity shooter =
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND);
    final float before = shooter.getHealth();
    MockPlayerSupport.fireEastStraight(context, shooter, ModArrows.LIFESTEAL_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              shooter.getHealth() == before,
              "A shooter already at full health should gain nothing, they went from "
                  + before
                  + " to "
                  + shooter.getHealth());
          context.assertTrue(
              shooter.getAbsorptionAmount() == 0.0f,
              "Lifesteal should never hand out absorption as a substitute for health");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aHealStopsExactlyAtTheShootersMaximum(TestContext context) {
    FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_STAND);
    final ServerPlayerEntity shooter =
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND);
    shooter.setHealth(shooter.getMaxHealth() - A_SINGLE_HEART);
    MockPlayerSupport.fireEastStraight(context, shooter, ModArrows.LIFESTEAL_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              shooter.getHealth() == shooter.getMaxHealth(),
              "A shooter one heart down should be topped up to exactly their maximum, they are at "
                  + shooter.getHealth()
                  + " of "
                  + shooter.getMaxHealth());
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aDispensedArrowHasNoShooterToHealAndDoesNotError(TestContext context) {
    final CowEntity target = FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_STAND);
    final float before = target.getHealth();
    FiringRangeSupport.dispenseEast(context, DISPENSER_STAND, ModArrows.LIFESTEAL_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              target.getHealth() < before,
              "A dispensed lifesteal arrow should still hurt a target");
          context.complete();
        });
  }
}
