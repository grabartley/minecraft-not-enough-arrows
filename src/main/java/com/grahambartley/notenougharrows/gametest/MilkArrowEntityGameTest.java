package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class MilkArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "milk-arrow";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);
  private static final BlockPos BYSTANDER_STAND = new BlockPos(3, 3, 5);
  private static final int A_LONG_TIME = 6000;

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowStripsAHarmfulEffectFromWhatItHits(TestContext context) {
    final CowEntity target = CombatTestSupport.stillCowAt(context, TARGET_STAND);
    target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, A_LONG_TIME));
    MockPlayerSupport.fireEastStraight(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.MILK_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              target.getStatusEffect(StatusEffects.POISON) == null,
              "A milk arrow should strip poison");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowStripsAHelpfulEffectJustTheSame(TestContext context) {
    final CowEntity target = CombatTestSupport.stillCowAt(context, TARGET_STAND);
    target.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, A_LONG_TIME));
    MockPlayerSupport.fireEastStraight(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.MILK_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              target.getStatusEffect(StatusEffects.REGENERATION) == null,
              "A milk arrow should strip regeneration too, with no picking and choosing");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowThatHitsOnlyABlockStripsNothing(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final CowEntity bystander = CombatTestSupport.stillCowAt(context, BYSTANDER_STAND);
    bystander.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, A_LONG_TIME));
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.MILK_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              bystander.getStatusEffect(StatusEffects.POISON) != null,
              "An arrow that hits a block should strip nobody's effects");
          context.complete();
        });
  }
}
