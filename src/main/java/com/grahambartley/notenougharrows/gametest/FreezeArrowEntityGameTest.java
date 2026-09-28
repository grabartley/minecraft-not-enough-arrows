package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class FreezeArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "freeze-arrow";

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowFreezesTheWaterWhereItLands(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    context.setBlockState(FiringRangeSupport.IMPACT_FACE, Blocks.WATER);
    TerrainArrowTestSupport.fireFromBow(context, ModArrows.FREEZE_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.ICE, FiringRangeSupport.IMPACT_FACE);
          context.expectBlock(Blocks.STONE, FiringRangeSupport.BACKSTOP);
          context.assertTrue(FiringRangeSupport.arrowWasSpent(context), "The arrow is spent");
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aDispensedArrowFreezesLavaToObsidian(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    context.setBlockState(FiringRangeSupport.IMPACT_FACE.up(), Blocks.LAVA);
    FiringRangeSupport.dispenseEast(
        context, TerrainArrowTestSupport.DISPENSER_STAND, ModArrows.FREEZE_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.OBSIDIAN, FiringRangeSupport.IMPACT_FACE.up());
          context.complete();
        });
  }
}
