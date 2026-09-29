package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.DiscoveryArrows;
import com.grahambartley.notenougharrows.entity.ProspectorArrowEntity;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class ProspectorArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "prospector-arrow";

  @GameTest(
      templateName = DiscoveryTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aProspectorArrowIsSpentAndLeavesTheRockAlone(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    context.setBlockState(FiringRangeSupport.BACKSTOP.up(), Blocks.DIAMOND_ORE);
    TerrainArrowTestSupport.fireFromBow(context, DiscoveryArrows.PROSPECTOR_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, ProspectorArrowEntity.class) == null,
              "A prospector arrow is spent on impact");
          context.expectBlock(Blocks.DIAMOND_ORE, FiringRangeSupport.BACKSTOP.up());
          context.expectBlock(Blocks.STONE, FiringRangeSupport.BACKSTOP);
          context.complete();
        });
  }
}
