package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.AgricultureArrows;
import com.grahambartley.notenougharrows.entity.BlossomArrowEntity;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class BlossomArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "blossom-arrow";

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowFromABowGrowsTheCropAtTheFootOfTheWall(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    AgricultureTestSupport.plantWheat(context, FiringRangeSupport.IMPACT_FACE, 0);
    TerrainArrowTestSupport.fireFromBow(context, AgricultureArrows.BLOSSOM_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.checkBlockState(
              FiringRangeSupport.IMPACT_FACE,
              state -> state.get(CropBlock.AGE) > 0,
              () -> "The wheat should have grown");
          context.assertTrue(FiringRangeSupport.arrowWasSpent(context), "The arrow is spent");
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowWithNothingToGrowStaysRecoverable(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    TerrainArrowTestSupport.fireFromBow(context, AgricultureArrows.BLOSSOM_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, BlossomArrowEntity.class) != null,
              "An arrow that grew nothing should embed and stay recoverable");
          context.complete();
        });
  }
}
