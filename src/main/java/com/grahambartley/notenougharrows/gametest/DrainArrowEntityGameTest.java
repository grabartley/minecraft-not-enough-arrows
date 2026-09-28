package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class DrainArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "drain-arrow";

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowSoaksUpTheWaterWhereItLands(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    context.setBlockState(FiringRangeSupport.IMPACT_FACE, Blocks.WATER);
    TerrainArrowTestSupport.fireFromBow(context, ModArrows.DRAIN_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertTrue(FiringRangeSupport.arrowWasSpent(context), "The arrow is spent");
          context.assertFalse(
              context.getBlockState(FiringRangeSupport.IMPACT_FACE).isOf(Blocks.WATER),
              "The water where it landed should be gone");
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowIsSpentEvenWhereThereWasNothingToSoakUp(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    TerrainArrowTestSupport.fireFromBow(context, ModArrows.DRAIN_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertTrue(FiringRangeSupport.arrowWasSpent(context), "The arrow is spent");
          context.complete();
        });
  }
}
