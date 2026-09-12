package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModBlocks;
import com.grahambartley.notenougharrows.rope.RopePlacer;
import com.grahambartley.notenougharrows.rope.RopeShape;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class RopePlacerGameTest implements FabricGameTest {
  private static final String BATCH = "rope-placement";
  private static final int LONGER_THAN_THE_SHAFT = 32;
  private static final int SHORT_ROPE = 3;
  private static final BlockPos LEDGE = new BlockPos(2, 8, 2);

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRopeHangsFromTheAnchorDownToTheGround(TestContext context) {
    RopeTestSupport.raiseCeiling(context);

    final List<BlockPos> placed = drop(context, LONGER_THAN_THE_SHAFT);

    context.assertEquals(
        placed.size(),
        RopeTestSupport.ROPE_HEAD.getY() - RopeTestSupport.SHAFT_FLOOR_Y + 1,
        "Rope segments hung between the anchor and the floor");
    context.expectBlock(ModBlocks.ROPE, RopeTestSupport.ROPE_HEAD);
    context.expectBlock(ModBlocks.ROPE, new BlockPos(2, RopeTestSupport.SHAFT_FLOOR_Y, 2));
    context.complete();
  }

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRopeStopsAtTheFirstObstruction(TestContext context) {
    RopeTestSupport.raiseCeiling(context);
    context.setBlockState(LEDGE, Blocks.STONE);

    drop(context, LONGER_THAN_THE_SHAFT);

    context.expectBlock(ModBlocks.ROPE, LEDGE.up());
    context.expectBlock(Blocks.STONE, LEDGE);
    context.dontExpectBlock(ModBlocks.ROPE, LEDGE.down());
    context.complete();
  }

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRopeIsNoLongerThanItWasAskedToBe(TestContext context) {
    RopeTestSupport.raiseCeiling(context);

    final List<BlockPos> placed = drop(context, SHORT_ROPE);

    context.assertEquals(placed.size(), SHORT_ROPE, "Rope segments hung for a short rope");
    context.expectBlock(ModBlocks.ROPE, RopeTestSupport.ROPE_HEAD.down(SHORT_ROPE - 1));
    context.dontExpectBlock(ModBlocks.ROPE, RopeTestSupport.ROPE_HEAD.down(SHORT_ROPE));
    context.complete();
  }

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRopeCannotStartWhereSomethingAlreadyStands(TestContext context) {
    RopeTestSupport.raiseCeiling(context);
    context.setBlockState(RopeTestSupport.ROPE_HEAD, Blocks.STONE);

    context.assertTrue(
        drop(context, LONGER_THAN_THE_SHAFT).isEmpty(),
        "A rope should hang nothing when the space beneath its anchor is already taken");
    context.complete();
  }

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRopeCannotHangFromNothing(TestContext context) {
    context.assertFalse(
        RopePlacer.canPlaceAt(
            context.getWorld(), context.getAbsolutePos(RopeTestSupport.ROPE_HEAD), null),
        "Open air holds no rope up");
    context.assertTrue(
        drop(context, LONGER_THAN_THE_SHAFT).isEmpty(),
        "A rope with nothing above it should hang nothing at all");
    context.complete();
  }

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRopeCannotHangOutsideTheWorld(TestContext context) {
    context.assertFalse(
        RopePlacer.canPlaceAt(context.getWorld(), new BlockPos(0, Integer.MAX_VALUE, 0), null),
        "A position outside the build limit holds no rope");
    context.complete();
  }

  private static List<BlockPos> drop(final TestContext context, final int maxLength) {
    return RopePlacer.place(
        context.getWorld(),
        RopeShape.below(context.getAbsolutePos(RopeTestSupport.CEILING), maxLength),
        null);
  }
}
