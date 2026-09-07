package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModBlocks;
import com.grahambartley.morearrows.redstone.RedstoneChargePlacer;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class RedstoneChargePlacerGameTest implements FabricGameTest {
  private static final String BATCH = "redstone-charge-placement";
  private static final String TEMPLATE = "more-arrows:fire_pad";
  private static final BlockPos OPEN_AIR = new BlockPos(3, 3, 3);
  private static final BlockPos OCCUPIED = new BlockPos(3, 3, 4);
  private static final int STRENGTH = 12;

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aChargeIsPlacedIntoOpenAir(TestContext context) {
    context.assertTrue(
        RedstoneChargePlacer.place(
            context.getWorld(), context.getAbsolutePos(OPEN_AIR), STRENGTH, null),
        "An open air position should take a charge");
    context.expectBlock(ModBlocks.REDSTONE_CHARGE, OPEN_AIR);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anOccupiedPositionIsNeverReplaced(TestContext context) {
    context.setBlockState(OCCUPIED, Blocks.STONE);

    context.assertFalse(
        RedstoneChargePlacer.place(
            context.getWorld(), context.getAbsolutePos(OCCUPIED), STRENGTH, null),
        "A charge should never replace a block a player put there");
    context.expectBlock(Blocks.STONE, OCCUPIED);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aStrengthOfZeroPlacesNothing(TestContext context) {
    context.assertFalse(
        RedstoneChargePlacer.place(context.getWorld(), context.getAbsolutePos(OPEN_AIR), 0, null),
        "A charge with no strength should not be placed");
    context.expectBlock(Blocks.AIR, OPEN_AIR);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void clearingRemovesAChargeAndLeavesAir(TestContext context) {
    final BlockPos absolute = context.getAbsolutePos(OPEN_AIR);
    RedstoneChargePlacer.place(context.getWorld(), absolute, STRENGTH, null);

    context.assertTrue(
        RedstoneChargePlacer.clear(context.getWorld(), absolute),
        "Clearing a charge should remove it");
    context.expectBlock(Blocks.AIR, OPEN_AIR);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void clearingNeverTouchesABlockThatIsNotACharge(TestContext context) {
    context.setBlockState(OCCUPIED, Blocks.STONE);

    context.assertFalse(
        RedstoneChargePlacer.clear(context.getWorld(), context.getAbsolutePos(OCCUPIED)),
        "Clearing should leave a block that is not a charge alone");
    context.expectBlock(Blocks.STONE, OCCUPIED);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void onlyAChargeIsRecognisedAsOne(TestContext context) {
    final BlockPos absoluteAir = context.getAbsolutePos(OPEN_AIR);
    context.setBlockState(OCCUPIED, Blocks.STONE);

    context.assertFalse(
        RedstoneChargePlacer.isCharge(context.getWorld(), absoluteAir),
        "Air should not be recognised as a charge");
    context.assertFalse(
        RedstoneChargePlacer.isCharge(context.getWorld(), context.getAbsolutePos(OCCUPIED)),
        "Stone should not be recognised as a charge");

    RedstoneChargePlacer.place(context.getWorld(), absoluteAir, STRENGTH, null);

    context.assertTrue(
        RedstoneChargePlacer.isCharge(context.getWorld(), absoluteAir),
        "A placed charge should be recognised as one");
    context.complete();
  }
}
