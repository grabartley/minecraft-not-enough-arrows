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

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aHitHealsTheWoundedShooter(TestContext context) {
    CombatTestSupport.stillCowAt(context, TARGET_STAND);
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
    CombatTestSupport.stillCowAt(context, TARGET_STAND);
    final ServerPlayerEntity shooter =
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND);
    MockPlayerSupport.fireEastFromBow(context, shooter, ModArrows.LIFESTEAL_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              shooter.getHealth() <= shooter.getMaxHealth(),
              "Lifesteal should never heal a shooter past their own maximum");
          context.assertTrue(
              shooter.getAbsorptionAmount() == 0.0f,
              "Lifesteal should never hand out absorption as a substitute");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aDispensedArrowHasNoShooterToHealAndDoesNotError(TestContext context) {
    final CowEntity target = CombatTestSupport.stillCowAt(context, TARGET_STAND);
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
