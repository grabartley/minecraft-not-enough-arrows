package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class WebArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "web-arrow";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowSpinsCobwebWhereItLands(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    TerrainArrowTestSupport.fireFromBow(context, ModArrows.WEB_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.COBWEB, FiringRangeSupport.IMPACT_FACE);
          context.expectBlock(Blocks.STONE, FiringRangeSupport.BACKSTOP);
          context.assertTrue(FiringRangeSupport.arrowWasSpent(context), "The arrow is spent");
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowSpinsCobwebAroundTheMobItHits(TestContext context) {
    final CowEntity cow = ControlTestSupport.sturdyStillCowAt(context, TARGET_STAND);
    TerrainArrowTestSupport.fireFromBow(context, ModArrows.WEB_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertTrue(
              context.getWorld().getBlockState(cow.getBlockPos()).isOf(Blocks.COBWEB),
              "The cow should be standing in cobweb");
          context.complete();
        });
  }
}
