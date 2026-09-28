package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.DrainArrowConfig;
import com.grahambartley.notenougharrows.terrain.DrainService;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

public final class DrainServiceGameTest implements FabricGameTest {
  private static final String BATCH = "drain-service";
  private static final BlockPos CENTER = TerrainTestSupport.CENTER;
  private static final BlockPos[] POOL = {
    CENTER, CENTER.east(), CENTER.west(), CENTER.north(), CENTER.south()
  };
  private static final DrainArrowConfig RADIUS_ONE = new DrainArrowConfig(true, 1, 65);

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDrainSoaksUpTheWaterAroundIt(TestContext context) {
    TerrainTestSupport.fill(context, Blocks.WATER.getDefaultState(), POOL);

    context.assertEquals(drain(context, null, RADIUS_ONE).size(), POOL.length, "Blocks drained");
    for (final BlockPos pos : POOL) {
      context.expectBlock(Blocks.AIR, pos);
    }
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDrainStopsAtItsVolumeCap(TestContext context) {
    TerrainTestSupport.fill(context, Blocks.WATER.getDefaultState(), POOL);

    context.assertEquals(
        drain(context, null, new DrainArrowConfig(true, 1, 2)).size(), 2, "Blocks drained");
    context.expectBlock(Blocks.AIR, CENTER);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aRadiusOfZeroDrainsTheCentreAlone(TestContext context) {
    TerrainTestSupport.fill(context, Blocks.WATER.getDefaultState(), POOL);

    drain(context, null, new DrainArrowConfig(true, 0, 65));

    context.expectBlock(Blocks.AIR, CENTER);
    context.expectBlock(Blocks.WATER, CENTER.east());
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDrainLeavesLavaAlone(TestContext context) {
    context.setBlockState(CENTER, Blocks.LAVA);

    context.assertTrue(drain(context, null, RADIUS_ONE).isEmpty(), "Lava drained");
    context.expectBlock(Blocks.LAVA, CENTER);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDisabledDrainSoaksUpNothing(TestContext context) {
    context.setBlockState(CENTER, Blocks.WATER);

    context.assertTrue(drain(context, null, RADIUS_ONE.withEnabled(false)).isEmpty(), "Drained");
    context.expectBlock(Blocks.WATER, CENTER);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aShooterCannotDrainWhereTheyMayNotBuild(TestContext context) {
    context.setBlockState(CENTER, Blocks.WATER);
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);

    TerrainTestSupport.withTheBorderElsewhere(
        context,
        () ->
            context.assertTrue(
                drain(context, shooter, RADIUS_ONE).isEmpty(), "Drained past the border"));
    context.expectBlock(Blocks.WATER, CENTER);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDispensedDrainStopsAtTheWorldBorder(TestContext context) {
    context.setBlockState(CENTER, Blocks.WATER);

    TerrainTestSupport.withTheBorderElsewhere(
        context,
        () ->
            context.assertTrue(
                drain(context, null, RADIUS_ONE).isEmpty(), "Drained past the border"));
    context.expectBlock(Blocks.WATER, CENTER);
    context.complete();
  }

  private static List<BlockPos> drain(
      final TestContext context, final PlayerEntity shooter, final DrainArrowConfig drain) {
    return DrainService.drain(context.getWorld(), context.getAbsolutePos(CENTER), shooter, drain);
  }
}
