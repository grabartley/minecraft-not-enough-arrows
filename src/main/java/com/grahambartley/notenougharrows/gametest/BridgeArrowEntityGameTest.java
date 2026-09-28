package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.TraversalArrows;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class BridgeArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "bridge-arrow";

  @BeforeBatch(batchId = BATCH)
  public void forgetStructuresBeforeBatch(ServerWorld world) {
    TimedStructureService.forget();
  }

  @GameTest(
      templateName = TraversalTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aBridgeArrowLaysPlanksBackTowardTheShooterStoppingShortOfThem(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    final ServerPlayerEntity shooter =
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND);
    MockPlayerSupport.fireEastFromBow(context, shooter, TraversalArrows.BRIDGE_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          for (int x = 2; x <= 5; x++) {
            context.expectBlock(Blocks.OAK_PLANKS, new BlockPos(x, 3, 3));
          }
          context.expectBlock(Blocks.AIR, new BlockPos(1, 3, 3));
          context.assertTrue(FiringRangeSupport.arrowWasSpent(context), "The arrow is spent");
          context.complete();
        });
  }

  @GameTest(
      templateName = TraversalTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aDispensedBridgeArrowLaysPlanksBackTheWayItCame(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    FiringRangeSupport.dispenseEast(
        context, TerrainArrowTestSupport.DISPENSER_STAND, TraversalArrows.BRIDGE_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.OAK_PLANKS, FiringRangeSupport.IMPACT_FACE);
          context.expectBlock(Blocks.OAK_PLANKS, FiringRangeSupport.IMPACT_FACE.west());
          context.complete();
        });
  }
}
