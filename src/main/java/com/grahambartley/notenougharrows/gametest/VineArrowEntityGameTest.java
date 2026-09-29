package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.TraversalArrows;
import com.grahambartley.notenougharrows.entity.VineArrowEntity;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class VineArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "vine-arrow";

  @GameTest(
      templateName = TraversalTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aVineArrowGrowsAVineUpTheFaceItHitsAndStaysInTheWall(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    TerrainArrowTestSupport.fireFromBow(context, TraversalArrows.VINE_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.checkBlockState(
              FiringRangeSupport.IMPACT_FACE,
              state -> state.isOf(Blocks.VINE) && state.get(VineBlock.EAST),
              () -> "A vine should cling to the struck face");
          context.expectBlock(Blocks.VINE, FiringRangeSupport.IMPACT_FACE.up());
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, VineArrowEntity.class) != null,
              "A vine arrow embeds and is recovered");
          context.complete();
        });
  }

  @GameTest(
      templateName = TraversalTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT,
      required = false)
  public void aDispensedVineArrowGrowsAVineToo(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    FiringRangeSupport.dispenseEast(
        context, TerrainArrowTestSupport.DISPENSER_STAND, TraversalArrows.VINE_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.VINE, FiringRangeSupport.IMPACT_FACE);
          context.complete();
        });
  }
}
