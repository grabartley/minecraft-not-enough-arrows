package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class HasteArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "haste-arrow";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);
  private static final BlockPos BYSTANDER_STAND = new BlockPos(3, 3, 5);

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowFiredFromABowAppliesHasteToTheEntityItHits(TestContext context) {
    final CowEntity target = CombatTestSupport.stillCowAt(context, TARGET_STAND);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.HASTE_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.expectEntityHasEffect(target, StatusEffects.HASTE, 0);
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aDispensedArrowAppliesHasteJustTheSame(TestContext context) {
    final CowEntity target = CombatTestSupport.stillCowAt(context, TARGET_STAND);
    FiringRangeSupport.dispenseEast(context, new BlockPos(1, 3, 3), ModArrows.HASTE_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.expectEntityHasEffect(target, StatusEffects.HASTE, 0);
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowThatHitsOnlyABlockAppliesNothing(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final CowEntity bystander = CombatTestSupport.stillCowAt(context, BYSTANDER_STAND);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.HASTE_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              bystander.getStatusEffect(StatusEffects.HASTE) == null,
              "An arrow that hits a block should apply haste to nobody");
          context.complete();
        });
  }
}
