package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.TraversalArrows;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class ScaffoldArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "scaffold-arrow";

  @BeforeBatch(batchId = BATCH)
  public void forgetStructuresBeforeBatch(ServerWorld world) {
    TimedStructureService.forget();
  }

  @GameTest(
      templateName = TraversalTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aScaffoldArrowRaisesAColumnFromTheGroundBeneathWhereItHits(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    TerrainArrowTestSupport.fireFromBow(context, TraversalArrows.SCAFFOLD_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.SCAFFOLDING, FiringRangeSupport.IMPACT_FACE);
          context.expectBlock(Blocks.SCAFFOLDING, FiringRangeSupport.IMPACT_FACE.up());
          context.expectBlock(Blocks.SCAFFOLDING, FiringRangeSupport.IMPACT_FACE.up(2));
          context.assertTrue(FiringRangeSupport.arrowWasSpent(context), "The arrow is spent");
          context.complete();
        });
  }
}
