package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.gravity.FallingBlocks;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class FallingBlocksGameTest implements FabricGameTest {
  private static final String BATCH = "falling-blocks";
  private static final String TEMPLATE = "not-enough-arrows:fire_pad";
  private static final BlockPos CANDIDATE = new BlockPos(3, 4, 3);
  private static final int TICK_LIMIT = 20;

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void aDroppedBlockLeavesItsPositionEmptyAndTakesItsStateWithIt(TestContext context) {
    context.setBlockState(CANDIDATE, Blocks.STONE);
    final BlockPos target = context.getAbsolutePos(CANDIDATE);

    final FallingBlockEntity falling =
        FallingBlocks.drop(context.getWorld(), target, context.getWorld().getBlockState(target));

    context.assertTrue(
        falling.getBlockState().isOf(Blocks.STONE),
        "The falling block should carry the state it replaced");
    context.expectBlock(Blocks.AIR, CANDIDATE);
    context.expectEntity(EntityType.FALLING_BLOCK);
    context.complete();
  }
}
