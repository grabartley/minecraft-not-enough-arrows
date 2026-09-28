package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.agriculture.HarvestService;
import com.grahambartley.notenougharrows.config.HarvestArrowConfig;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;

public final class HarvestServiceGameTest implements FabricGameTest {
  private static final String BATCH = "harvest-service";
  private static final BlockPos CENTER = TerrainTestSupport.CENTER;
  private static final BlockPos FAR = CENTER.east(3);

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aRipeCropIsReplantedAndItsHarvestGoesToTheShooter(TestContext context) {
    AgricultureTestSupport.plantWheat(context, CENTER, CropBlock.MAX_AGE);
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);

    final List<BlockPos> harvested = harvest(context, shooter, 1);

    context.assertEquals(harvested.size(), 1, "Crops harvested");
    context.checkBlockState(
        CENTER,
        state -> state.isOf(Blocks.WHEAT) && state.get(CropBlock.AGE) == 0,
        () -> "The wheat should be replanted as a seedling");
    context.assertEquals(shooter.getInventory().count(Items.WHEAT), 1, "Wheat granted");
    context.assertEquals(
        TerrainTestSupport.droppedCount(context, Items.WHEAT), 0, "Wheat on the ground");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aHalfGrownCropIsLeftExactlyAsItWas(TestContext context) {
    AgricultureTestSupport.plantWheat(context, CENTER, 3);
    final BlockState before = context.getBlockState(CENTER);
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);

    context.assertTrue(harvest(context, shooter, 2).isEmpty(), "Nothing ripe to harvest");
    context.assertTrue(context.getBlockState(CENTER) == before, "The wheat is untouched");
    context.assertEquals(shooter.getInventory().count(Items.WHEAT_SEEDS), 0, "Seeds granted");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aRipeCropOutsideTheRadiusIsLeftAlone(TestContext context) {
    AgricultureTestSupport.plantWheat(context, FAR, CropBlock.MAX_AGE);

    context.assertTrue(
        harvest(context, context.createMockPlayer(GameMode.SURVIVAL), 2).isEmpty(),
        "The ripe wheat is three blocks away");
    context.checkBlockState(
        FAR, state -> state.get(CropBlock.AGE) == CropBlock.MAX_AGE, () -> "Still ripe");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aFullInventoryLosesNoPartOfTheHarvest(TestContext context) {
    AgricultureTestSupport.plantWheat(context, CENTER, CropBlock.MAX_AGE);
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);
    AgricultureTestSupport.fillInventory(shooter);

    harvest(context, shooter, 0);

    context.assertEquals(
        TerrainTestSupport.droppedCount(context, Items.WHEAT),
        1,
        "The wheat with nowhere to go should land by the crop");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aHarvestWithNoShooterDropsItsHarvestAtTheCrop(TestContext context) {
    AgricultureTestSupport.plantWheat(context, CENTER, CropBlock.MAX_AGE);

    harvest(context, null, 0);

    context.assertEquals(
        TerrainTestSupport.droppedCount(context, Items.WHEAT), 1, "Wheat on the ground");
    context.checkBlockState(
        CENTER, state -> state.get(CropBlock.AGE) == 0, () -> "Replanted all the same");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void ripeNetherWartIsReplantedOnItsSoulSand(TestContext context) {
    context.setBlockState(CENTER.down(), Blocks.SOUL_SAND);
    context.setBlockState(
        CENTER, Blocks.NETHER_WART.getDefaultState().with(NetherWartBlock.AGE, 3));
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);

    harvest(context, shooter, 0);

    context.checkBlockState(
        CENTER,
        state -> state.isOf(Blocks.NETHER_WART) && state.get(NetherWartBlock.AGE) == 0,
        () -> "The nether wart should be replanted");
    context.assertTrue(shooter.getInventory().count(Items.NETHER_WART) > 0, "Nether wart granted");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void ripeCocoaIsReplantedFacingItsLog(TestContext context) {
    context.setBlockState(CENTER.north(), Blocks.JUNGLE_LOG);
    context.setBlockState(
        CENTER,
        Blocks.COCOA
            .getDefaultState()
            .with(CocoaBlock.FACING, Direction.NORTH)
            .with(CocoaBlock.AGE, CocoaBlock.MAX_AGE));
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);

    harvest(context, shooter, 0);

    context.checkBlockState(
        CENTER,
        state ->
            state.isOf(Blocks.COCOA)
                && state.get(CocoaBlock.AGE) == 0
                && state.get(CocoaBlock.FACING) == Direction.NORTH,
        () -> "The cocoa should be replanted on the same log");
    context.assertEquals(
        shooter.getInventory().count(Items.COCOA_BEANS),
        2,
        "Ripe cocoa always drops three beans, and one of them is planted back");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aRipeCropWhereTheShooterMayNotBuildIsLeftAlone(TestContext context) {
    AgricultureTestSupport.plantWheat(context, CENTER, CropBlock.MAX_AGE);

    TerrainTestSupport.withTheBorderElsewhere(
        context,
        () -> context.assertTrue(harvest(context, null, 1).isEmpty(), "Nothing harvested"));
    context.checkBlockState(
        CENTER, state -> state.get(CropBlock.AGE) == CropBlock.MAX_AGE, () -> "Still ripe");
    context.complete();
  }

  private static List<BlockPos> harvest(
      final TestContext context, final PlayerEntity shooter, final int radius) {
    return HarvestService.harvest(
        context.getWorld(),
        context.getAbsolutePos(CENTER),
        shooter,
        new HarvestArrowConfig(radius));
  }
}
