package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.TraversalArrows;
import com.grahambartley.notenougharrows.updraft.UpdraftService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.Vec3d;

public final class UpdraftArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "updraft-arrow";

  @BeforeBatch(batchId = BATCH)
  public void forgetUpdraftsBeforeBatch(ServerWorld world) {
    UpdraftService.forget();
  }

  @GameTest(
      templateName = TraversalTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anUpdraftArrowOpensAColumnInFrontOfTheBlockItHits(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    TerrainArrowTestSupport.fireFromBow(context, TraversalArrows.UPDRAFT_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          final Vec3d expected =
              Vec3d.ofBottomCenter(context.getAbsolutePos(FiringRangeSupport.IMPACT_FACE));
          context.assertTrue(
              UpdraftService.liveIn(context.getWorld()).stream()
                  .anyMatch(updraft -> updraft.column().base().equals(expected)),
              "A column should rise from the face the arrow struck");
          context.assertTrue(FiringRangeSupport.arrowWasSpent(context), "The arrow is spent");
          context.complete();
        });
  }
}
