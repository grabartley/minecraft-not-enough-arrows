package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class RustArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "rust-arrow";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);
  private static final BlockPos BYSTANDER_STAND = new BlockPos(3, 3, 5);

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowFiredFromABowAppliesRustToTheEntityItHits(TestContext context) {
    final CowEntity target = FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_STAND);
    MockPlayerSupport.fireEastStraight(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.RUST_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.expectEntityHasEffect(target, StatusEffects.MINING_FATIGUE, 0);
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aDispensedArrowAppliesRustJustTheSame(TestContext context) {
    final CowEntity target = FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_STAND);
    FiringRangeSupport.dispenseEast(context, new BlockPos(1, 3, 3), ModArrows.RUST_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.expectEntityHasEffect(target, StatusEffects.MINING_FATIGUE, 0);
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowThatHitsOnlyABlockAppliesNothing(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final CowEntity bystander = FiringRangeSupport.liveTargetOnPedestalAt(context, BYSTANDER_STAND);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.RUST_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              bystander.getStatusEffect(StatusEffects.MINING_FATIGUE) == null,
              "An arrow that hits a block should apply mining fatigue to nobody");
          context.complete();
        });
  }
}
