package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModBlocks;
import com.grahambartley.notenougharrows.TraversalArrows;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class TrampolineArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "trampoline-arrow";

  @BeforeBatch(batchId = BATCH)
  public void forgetStructuresBeforeBatch(ServerWorld world) {
    TimedStructureService.forget();
  }

  @GameTest(
      templateName = TraversalTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aTrampolineArrowPutsAPadDownWhereItLands(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    TerrainArrowTestSupport.fireFromBow(context, TraversalArrows.TRAMPOLINE_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(ModBlocks.TRAMPOLINE, FiringRangeSupport.IMPACT_FACE);
          context.expectBlock(ModBlocks.TRAMPOLINE, FiringRangeSupport.IMPACT_FACE.north());
          context.expectBlock(Blocks.STONE, FiringRangeSupport.BACKSTOP);
          context.assertTrue(FiringRangeSupport.arrowWasSpent(context), "The arrow is spent");
          context.complete();
        });
  }
}
