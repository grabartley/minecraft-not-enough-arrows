package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.AgricultureArrows;
import com.grahambartley.notenougharrows.entity.SaplingArrowEntity;
import com.grahambartley.notenougharrows.item.TintedArrowItem;
import com.grahambartley.notenougharrows.tint.ArrowChoice;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class SaplingArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "sapling-arrow";
  private static final BlockPos PLANTED = FiringRangeSupport.IMPACT_FACE;
  private static final int A_QUIVER = 8;

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anOakArrowPlantsAnOakSaplingAtTheFootOfTheWall(TestContext context) {
    wallOver(context, Blocks.GRASS_BLOCK);
    TerrainArrowTestSupport.fireFromBow(context, quiver("oak"));

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.OAK_SAPLING, PLANTED);
          context.assertTrue(FiringRangeSupport.arrowWasSpent(context), "The arrow is spent");
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aCherryArrowPlantsTheSaplingItCarries(TestContext context) {
    wallOver(context, Blocks.DIRT);
    TerrainArrowTestSupport.fireFromBow(context, quiver("cherry"));

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.CHERRY_SAPLING, PLANTED);
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowOverStonePlantsNothingAndStaysRecoverable(TestContext context) {
    wallOver(context, Blocks.STONE);
    TerrainArrowTestSupport.fireFromBow(context, quiver("oak"));

    assertRecoveredLeaving(context, Blocks.AIR);
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowIntoWaterPlantsNothingAndStaysRecoverable(TestContext context) {
    wallOver(context, Blocks.DIRT);
    context.setBlockState(PLANTED, Blocks.WATER);
    TerrainArrowTestSupport.fireFromBow(context, quiver("oak"));

    assertRecoveredLeaving(context, Blocks.WATER);
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowOntoOccupiedGroundPlantsNothingAndStaysRecoverable(TestContext context) {
    wallOver(context, Blocks.GRASS_BLOCK);
    context.setBlockState(PLANTED, Blocks.POPPY);
    TerrainArrowTestSupport.fireFromBow(context, quiver("oak"));

    assertRecoveredLeaving(context, Blocks.POPPY);
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aRecoveredArrowStillCarriesItsSapling(TestContext context) {
    wallOver(context, Blocks.STONE);
    TerrainArrowTestSupport.fireFromBow(context, quiver("birch"));

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          final SaplingArrowEntity arrow =
              FiringRangeSupport.firedArrow(context, SaplingArrowEntity.class);
          context.assertTrue(arrow.sapling() == Items.BIRCH_SAPLING, "It carries birch");
          context.assertTrue(
              "birch"
                  .equals(
                      ((TintedArrowItem) AgricultureArrows.SAPLING_ARROW.item())
                          .choiceOf(arrow.getItemStack())
                          .key()),
              "Picking it up gives back a birch sapling arrow");
          context.complete();
        });
  }

  private static void wallOver(final TestContext context, final Block ground) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    context.setBlockState(PLANTED.down(), ground);
  }

  private static void assertRecoveredLeaving(final TestContext context, final Block left) {
    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(left, PLANTED);
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, SaplingArrowEntity.class) != null,
              "An arrow that planted nothing should stay recoverable");
          context.complete();
        });
  }

  private static ItemStack quiver(final String sapling) {
    final TintedArrowItem item = (TintedArrowItem) AgricultureArrows.SAPLING_ARROW.item();
    final ItemStack quiver = item.stackOf(item.palette().resolve(new ArrowChoice(sapling)));
    quiver.setCount(A_QUIVER);
    return quiver;
  }
}
