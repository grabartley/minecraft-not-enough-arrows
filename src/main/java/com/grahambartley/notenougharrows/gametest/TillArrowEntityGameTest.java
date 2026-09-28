package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.AgricultureArrows;
import com.grahambartley.notenougharrows.entity.TillArrowEntity;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class TillArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "till-arrow";

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowFromABowTillsTheDirtItStrikes(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.DIRT);
    TerrainArrowTestSupport.fireFromBow(context, AgricultureArrows.TILL_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.checkBlockState(
              FiringRangeSupport.BACKSTOP,
              state ->
                  state.isOf(Blocks.FARMLAND)
                      && state.get(FarmlandBlock.MOISTURE) == FarmlandBlock.MAX_MOISTURE,
              () -> "The dirt should be wet farmland");
          context.assertTrue(FiringRangeSupport.arrowWasSpent(context), "The arrow is spent");
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowOnStoneTillsNothingAndStaysRecoverable(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    TerrainArrowTestSupport.fireFromBow(context, AgricultureArrows.TILL_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.STONE, FiringRangeSupport.BACKSTOP);
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, TillArrowEntity.class) != null,
              "An arrow that tilled nothing should stay recoverable");
          context.complete();
        });
  }
}
