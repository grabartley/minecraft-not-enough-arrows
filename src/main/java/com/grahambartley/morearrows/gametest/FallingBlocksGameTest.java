package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.gravity.FallingBlocks;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class FallingBlocksGameTest implements FabricGameTest {
  private static final String BATCH = "falling-blocks";
  private static final String TEMPLATE = "more-arrows:fire_pad";
  private static final BlockPos CANDIDATE = new BlockPos(3, 4, 3);
  private static final int FAR_ABOVE_THE_BUILD_LIMIT = 40_000;

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aSolidBreakableBlockCanFall(TestContext context) {
    assertCanFall(context, Blocks.STONE, true, "an ordinary solid block");
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void openAirHasNothingToDrop(TestContext context) {
    assertCanFall(context, Blocks.AIR, false, "air");
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aFluidIsNeverDropped(TestContext context) {
    assertCanFall(context, Blocks.WATER, false, "a fluid");
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aWaterloggedBlockIsTreatedAsAFluidAndStaysPut(TestContext context) {
    context.setBlockState(
        CANDIDATE, Blocks.OAK_FENCE.getDefaultState().with(Properties.WATERLOGGED, true));

    assertCurrentBlock(context, false, "a block holding water");
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aReplaceablePlantIsNeverDropped(TestContext context) {
    assertCanFall(context, Blocks.SHORT_GRASS, false, "a replaceable plant");
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aBlockWithNothingToItIsNeverDropped(TestContext context) {
    assertCanFall(context, Blocks.TORCH, false, "a block with no collision shape");
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anUnbreakableBlockIsNeverDropped(TestContext context) {
    assertCanFall(context, Blocks.BEDROCK, false, "an unbreakable block");
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aBlockHoldingSomebodysItemsIsNeverDropped(TestContext context) {
    assertCanFall(context, Blocks.CHEST, false, "a block that holds items");
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPositionOutsideTheBuildLimitIsNeverDropped(TestContext context) {
    final ServerWorld world = context.getWorld();
    final BlockPos aboveTheWorld =
        context.getAbsolutePos(CANDIDATE).withY(FAR_ABOVE_THE_BUILD_LIMIT);

    context.assertFalse(
        FallingBlocks.canFall(world, aboveTheWorld, Blocks.STONE.getDefaultState(), null),
        "A position outside the build limit should never be dropped");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
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

  private static void assertCanFall(
      final TestContext context,
      final Block block,
      final boolean expected,
      final String description) {
    context.setBlockState(CANDIDATE, block);
    assertCurrentBlock(context, expected, description);
  }

  private static void assertCurrentBlock(
      final TestContext context, final boolean expected, final String description) {
    final ServerWorld world = context.getWorld();
    final BlockPos target = context.getAbsolutePos(CANDIDATE);

    context.assertTrue(
        FallingBlocks.canFall(world, target, world.getBlockState(target), null) == expected,
        "Whether " + description + " may be dropped should be " + expected);
    context.complete();
  }
}
