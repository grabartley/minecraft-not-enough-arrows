package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class GlowInkArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "glow-ink-arrow";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowFiredFromABowMarksTheEntityItHits(TestContext context) {
    final CowEntity target = FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_STAND);
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.GLOW_INK_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.expectEntityHasEffect(target, StatusEffects.GLOWING, 0);
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowThatHitsOnlyABlockMarksNothingAndDoesNotError(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final CowEntity bystander =
        FiringRangeSupport.liveTargetOnPedestalAt(context, new BlockPos(3, 3, 5));
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.GLOW_INK_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              bystander.getStatusEffect(StatusEffects.GLOWING) == null,
              "An arrow that hits a block should mark nobody");
          context.complete();
        });
  }
}
