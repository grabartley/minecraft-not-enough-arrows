package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ChaosArrows;
import com.grahambartley.notenougharrows.chaos.StinkCloudService;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.grahambartley.notenougharrows.entity.StinkArrowEntity;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.Vec3d;

public final class StinkArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "stink-arrow";
  private static final String DISABLED_BATCH = "stink-arrow-disabled";

  @BeforeBatch(batchId = DISABLED_BATCH)
  public void switchTheStinkOffBeforeBatch(ServerWorld world) {
    ChaosTestSupport.useChaos(chaos -> chaos.withStink(chaos.stink().withEnabled(false)));
  }

  @AfterBatch(batchId = DISABLED_BATCH)
  public void restoreDefaultConfigAfterDisabledBatch(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aStinkArrowOpensACloudPlacesNoBlockAndIsSpent(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    TerrainArrowTestSupport.fireFromBow(context, ChaosArrows.STINK_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertTrue(
              StinkCloudService.isInCloud(context.getWorld(), impact(context)),
              "A cloud should hang where the arrow struck");
          context.expectBlock(Blocks.AIR, FiringRangeSupport.IMPACT_FACE);
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, StinkArrowEntity.class) == null,
              "A stink arrow that opened its cloud is spent");
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = DISABLED_BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aDisabledStinkArrowOpensNothingAndCanBeRecovered(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    TerrainArrowTestSupport.fireFromBow(context, ChaosArrows.STINK_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertFalse(
              StinkCloudService.isInCloud(context.getWorld(), impact(context)),
              "No cloud should open");
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, StinkArrowEntity.class) != null,
              "A switched off stink arrow stays in the wall to be picked up");
          context.complete();
        });
  }

  private static Vec3d impact(final TestContext context) {
    return Vec3d.ofCenter(context.getAbsolutePos(FiringRangeSupport.IMPACT_FACE));
  }
}
