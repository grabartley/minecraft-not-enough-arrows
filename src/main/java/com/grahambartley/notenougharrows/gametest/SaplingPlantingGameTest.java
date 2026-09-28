package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.agriculture.SaplingPlanting;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PropaguleBlock;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class SaplingPlantingGameTest implements FabricGameTest {
  private static final String BATCH = "sapling-planting";
  private static final BlockPos GROUND = TerrainTestSupport.CENTER;
  private static final BlockPos ABOVE = GROUND.up();

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aSaplingIsPlantedOnTopOfGrass(TestContext context) {
    context.setBlockState(GROUND, Blocks.GRASS_BLOCK);

    context.assertTrue(plantOnTop(context, Items.OAK_SAPLING), "The sapling should be planted");
    context.expectBlock(Blocks.OAK_SAPLING, ABOVE);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aSaplingReplacesShortGrassAsAHandPlacedOneWould(TestContext context) {
    context.setBlockState(GROUND, Blocks.GRASS_BLOCK);
    context.setBlockState(ABOVE, Blocks.SHORT_GRASS);

    context.assertTrue(plantOnTop(context, Items.BIRCH_SAPLING), "Planted over short grass");
    context.expectBlock(Blocks.BIRCH_SAPLING, ABOVE);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void nothingIsPlantedOnStone(TestContext context) {
    context.setBlockState(GROUND, Blocks.STONE);

    context.assertFalse(plantOnTop(context, Items.OAK_SAPLING), "Stone takes no sapling");
    context.expectBlock(Blocks.AIR, ABOVE);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void nothingIsPlantedInWater(TestContext context) {
    context.setBlockState(GROUND, Blocks.DIRT);
    context.setBlockState(ABOVE, Blocks.WATER);

    context.assertFalse(plantOnTop(context, Items.OAK_SAPLING), "A sapling cannot go in water");
    context.expectBlock(Blocks.WATER, ABOVE);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aPropaguleIsPlantedUnderwaterAndKeepsTheWater(TestContext context) {
    context.setBlockState(GROUND, Blocks.MUD);
    context.setBlockState(ABOVE, Blocks.WATER);

    context.assertTrue(plantOnTop(context, Items.MANGROVE_PROPAGULE), "Planted underwater");
    context.checkBlockState(
        ABOVE,
        state -> state.isOf(Blocks.MANGROVE_PROPAGULE) && state.get(Properties.WATERLOGGED),
        () -> "The propagule should hold the water it was planted in");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void nothingIsPlantedWhereSomethingAlreadyGrows(TestContext context) {
    context.setBlockState(GROUND, Blocks.GRASS_BLOCK);
    context.setBlockState(ABOVE, Blocks.POPPY);

    context.assertFalse(plantOnTop(context, Items.OAK_SAPLING), "The poppy is in the way");
    context.expectBlock(Blocks.POPPY, ABOVE);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aSideHitPlantsBesideTheWallWhenThereIsGroundThere(TestContext context) {
    context.setBlockState(GROUND, Blocks.STONE);
    context.setBlockState(GROUND.west().down(), Blocks.DIRT);

    context.assertTrue(
        plant(context, GROUND, Direction.WEST, Items.SPRUCE_SAPLING), "Planted beside the wall");
    context.expectBlock(Blocks.SPRUCE_SAPLING, GROUND.west());
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void everySaplingInThePalettePlantsOnDirt(TestContext context) {
    final Item[] saplings = {
      Items.OAK_SAPLING,
      Items.SPRUCE_SAPLING,
      Items.BIRCH_SAPLING,
      Items.JUNGLE_SAPLING,
      Items.ACACIA_SAPLING,
      Items.DARK_OAK_SAPLING,
      Items.CHERRY_SAPLING,
      Items.MANGROVE_PROPAGULE,
      Items.AZALEA,
      Items.FLOWERING_AZALEA
    };
    for (final Item sapling : saplings) {
      context.setBlockState(GROUND, Blocks.DIRT);
      context.setBlockState(ABOVE, Blocks.AIR);
      context.assertTrue(plantOnTop(context, sapling), sapling + " should plant on dirt");
      context.expectBlock(Block.getBlockFromItem(sapling), ABOVE);
    }
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aMangrovePropaguleIsPlantedAsAGrownPropagule(TestContext context) {
    context.setBlockState(GROUND, Blocks.MUD);

    context.assertTrue(plantOnTop(context, Items.MANGROVE_PROPAGULE), "Planted on mud");
    context.checkBlockState(
        ABOVE,
        state -> state.isOf(Blocks.MANGROVE_PROPAGULE) && !state.get(PropaguleBlock.HANGING),
        () -> "A placed propagule stands on the ground rather than hanging");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void nothingIsPlantedWhereTheShooterMayNotBuild(TestContext context) {
    context.setBlockState(GROUND, Blocks.GRASS_BLOCK);

    TerrainTestSupport.withTheBorderElsewhere(
        context,
        () ->
            context.assertFalse(
                plantOnTop(context, Items.OAK_SAPLING), "Nothing planted beyond the border"));
    context.expectBlock(Blocks.AIR, ABOVE);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void anItemThatIsNotABlockPlantsNothing(TestContext context) {
    context.setBlockState(GROUND, Blocks.GRASS_BLOCK);

    context.assertFalse(plantOnTop(context, Items.STICK), "A stick is not a sapling");
    context.expectBlock(Blocks.AIR, ABOVE);
    context.complete();
  }

  private static boolean plantOnTop(final TestContext context, final Item sapling) {
    return plant(context, GROUND, Direction.UP, sapling);
  }

  private static boolean plant(
      final TestContext context, final BlockPos struck, final Direction face, final Item sapling) {
    final BlockPos absolute = context.getAbsolutePos(struck);
    final BlockHitResult hit =
        new BlockHitResult(
            Vec3d.ofCenter(absolute).add(Vec3d.of(face.getVector()).multiply(0.5)),
            face,
            absolute,
            false);
    return SaplingPlanting.plant(context.getWorld(), hit, sapling, null);
  }
}
