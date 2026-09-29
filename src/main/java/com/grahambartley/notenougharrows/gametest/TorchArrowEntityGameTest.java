package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.DiscoveryArrows;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.grahambartley.notenougharrows.entity.TorchArrowEntity;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.Direction;

public final class TorchArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "torch-arrow";
  private static final String DISABLED_BATCH = "torch-arrow-disabled";

  @BeforeBatch(batchId = DISABLED_BATCH)
  public void switchTheTorchOffBeforeBatch(ServerWorld world) {
    DiscoveryTestSupport.useDiscovery(
        discovery -> discovery.withTorch(discovery.torch().withEnabled(false)));
  }

  @AfterBatch(batchId = DISABLED_BATCH)
  public void restoreDefaultConfigAfterDisabledBatch(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  @GameTest(
      templateName = DiscoveryTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aTorchArrowHangsATorchOnTheWallItHitsAndIsSpent(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    TerrainArrowTestSupport.fireFromBow(context, DiscoveryArrows.TORCH_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.checkBlockState(
              FiringRangeSupport.IMPACT_FACE,
              state ->
                  state.isOf(Blocks.WALL_TORCH)
                      && state.get(WallTorchBlock.FACING) == Direction.WEST,
              () -> "A wall torch should hang on the struck face, facing the shooter");
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, TorchArrowEntity.class) == null,
              "A torch arrow that placed its torch is spent");
          context.complete();
        });
  }

  @GameTest(
      templateName = DiscoveryTestSupport.TEMPLATE,
      batchId = DISABLED_BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aDisabledTorchArrowEmbedsAndCanBeRecovered(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    TerrainArrowTestSupport.fireFromBow(context, DiscoveryArrows.TORCH_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.AIR, FiringRangeSupport.IMPACT_FACE);
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, TorchArrowEntity.class) != null,
              "A torch arrow that placed nothing stays in the wall to be picked up");
          context.complete();
        });
  }

  @GameTest(
      templateName = DiscoveryTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aTorchArrowIntoWaterEmbedsAndCanBeRecovered(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    context.setBlockState(FiringRangeSupport.IMPACT_FACE, Blocks.WATER);
    TerrainArrowTestSupport.fireFromBow(context, DiscoveryArrows.TORCH_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.WATER, FiringRangeSupport.IMPACT_FACE);
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, TorchArrowEntity.class) != null,
              "A torch arrow that could not place a torch is left to be picked up");
          context.complete();
        });
  }

  @GameTest(
      templateName = DiscoveryTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aDispensedTorchArrowHangsATorchToo(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    FiringRangeSupport.dispenseEast(
        context, TerrainArrowTestSupport.DISPENSER_STAND, DiscoveryArrows.TORCH_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.WALL_TORCH, FiringRangeSupport.IMPACT_FACE);
          context.complete();
        });
  }
}
