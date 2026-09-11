package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.world.BlockPlacement;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class BlockPlacementGameTest implements FabricGameTest {
  private static final String BATCH = "block-placement";
  private static final String TEMPLATE = "not-enough-arrows:fire_pad";
  private static final BlockPos OPEN_AIR = new BlockPos(3, 4, 3);
  private static final BlockPos FAR_OUTSIDE_THE_BORDER =
      new BlockPos(Integer.MAX_VALUE, 4, Integer.MAX_VALUE);

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aBlockMayBePlacedWhereSomethingHoldsItUp(TestContext context) {
    context.setBlockState(OPEN_AIR.down(), Blocks.STONE);

    context.assertTrue(
        canPlaceTorchAt(context, context.getAbsolutePos(OPEN_AIR)),
        "A torch standing on a block in an unprotected world should be placeable");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void nothingMayBePlacedWhereSomethingAlreadyStands(TestContext context) {
    context.setBlockState(OPEN_AIR.down(), Blocks.STONE);
    context.setBlockState(OPEN_AIR, Blocks.STONE);

    context.assertFalse(
        canPlaceTorchAt(context, context.getAbsolutePos(OPEN_AIR)),
        "An occupied position should refuse a placement rather than overwrite what is there");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aBlockThatCannotHoldItselfUpIsRefused(TestContext context) {
    context.assertFalse(
        canPlaceTorchAt(context, context.getAbsolutePos(OPEN_AIR)),
        "A torch needs something under it, so open air should refuse one");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void nothingMayBePlacedOutsideTheWorld(TestContext context) {
    context.assertFalse(
        canPlaceTorchAt(context, FAR_OUTSIDE_THE_BORDER),
        "A position outside the build limit should refuse every placement");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void nothingIsPlacedWithoutAWorldAPositionOrAState(TestContext context) {
    final BlockPos pos = context.getAbsolutePos(OPEN_AIR);

    context.assertFalse(
        BlockPlacement.canPlace(null, pos, Blocks.TORCH.getDefaultState(), null),
        "A placement with no world behind it should be refused rather than guessed at");
    context.assertFalse(
        BlockPlacement.canPlace(context.getWorld(), null, Blocks.TORCH.getDefaultState(), null),
        "A placement with no position behind it should be refused rather than guessed at");
    context.assertFalse(
        BlockPlacement.canPlace(context.getWorld(), pos, null, null),
        "A placement with no state behind it should be refused rather than guessed at");
    context.complete();
  }

  private static boolean canPlaceTorchAt(final TestContext context, final BlockPos absolutePos) {
    return BlockPlacement.canPlace(
        context.getWorld(), absolutePos, Blocks.TORCH.getDefaultState(), null);
  }
}
