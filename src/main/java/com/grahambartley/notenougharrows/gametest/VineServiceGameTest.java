package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.traversal.VineService;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public final class VineServiceGameTest implements FabricGameTest {
  private static final String BATCH = "vine";
  private static final BlockPos STRUCK = new BlockPos(4, 3, 3);
  private static final BlockPos LOWEST_VINE = new BlockPos(3, 3, 3);
  private static final int LONG = 12;

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aVineClimbsTheStruckFaceUntilTheWallRunsOut(TestContext context) {
    wall(context, 3);

    final List<BlockPos> grown = grow(context, Direction.WEST, LONG);

    context.assertEquals(3, grown.size(), "Vines grown");
    for (int up = 0; up < 3; up++) {
      context.checkBlockState(
          LOWEST_VINE.up(up),
          state -> state.isOf(Blocks.VINE) && state.get(VineBlock.EAST),
          () -> "A vine should cling to the wall to its east");
    }
    context.expectBlock(Blocks.AIR, LOWEST_VINE.up(3));
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aVineUnderAnOverhangStopsAtTheOverhangRatherThanClippingThroughIt(
      TestContext context) {
    wall(context, 4);
    context.setBlockState(LOWEST_VINE.up(2), Blocks.STONE);

    final List<BlockPos> grown = grow(context, Direction.WEST, LONG);

    context.assertEquals(2, grown.size(), "Vines grown below the overhang");
    context.expectBlock(Blocks.STONE, LOWEST_VINE.up(2));
    context.expectBlock(Blocks.AIR, LOWEST_VINE.up(3));
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aVineStopsAtItsConfiguredLength(TestContext context) {
    wall(context, 4);

    context.assertEquals(2, grow(context, Direction.WEST, 2).size(), "Vines grown");
    context.expectBlock(Blocks.AIR, LOWEST_VINE.up(2));
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aVineStopsAtAVineAlreadyThere(TestContext context) {
    wall(context, 4);
    context.setBlockState(LOWEST_VINE.up(), VineService.vineFacing(Direction.EAST));

    context.assertEquals(1, grow(context, Direction.WEST, LONG).size(), "Vines grown");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aVineGrowsOnlyUpASideFace(TestContext context) {
    wall(context, 4);

    context.assertTrue(grow(context, Direction.UP, LONG).isEmpty(), "Top face");
    context.assertTrue(grow(context, Direction.DOWN, LONG).isEmpty(), "Bottom face");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aVineNeverGrowsWhereTheShooterMayNotBuild(TestContext context) {
    wall(context, 4);
    final ServerPlayerEntity shooter = context.createMockCreativeServerPlayerInWorld();
    final List<BlockPos>[] grown = new List[1];

    TraversalTestSupport.withTheBorderEastEdgeAt(
        context,
        LOWEST_VINE.getX(),
        () ->
            grown[0] =
                VineService.grow(
                    context.getWorld(),
                    context.getAbsolutePos(STRUCK),
                    Direction.WEST,
                    shooter,
                    LONG));

    context.assertTrue(grown[0].isEmpty(), "Nothing grows across the boundary");
    context.expectBlock(Blocks.AIR, LOWEST_VINE);
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aColumnSurvivesOneOfItsVinesBeingBroken(TestContext context) {
    wall(context, 3);
    grow(context, Direction.WEST, LONG);

    context.setBlockState(LOWEST_VINE.up(), Blocks.AIR);

    context.runAtTick(
        2,
        () -> {
          context.expectBlock(Blocks.VINE, LOWEST_VINE);
          context.expectBlock(Blocks.VINE, LOWEST_VINE.up(2));
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void theTopVineFallsAwayWhenTheWallBehindItGoes(TestContext context) {
    wall(context, 3);
    grow(context, Direction.WEST, LONG);

    context.setBlockState(STRUCK.up(2), Blocks.AIR);

    context.runAtTick(
        2,
        () -> {
          context.expectBlock(Blocks.AIR, LOWEST_VINE.up(2));
          context.expectBlock(Blocks.VINE, LOWEST_VINE.up());
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aDispensedVineStillGrows(TestContext context) {
    wall(context, 2);

    context.assertEquals(
        2,
        VineService.grow(
                context.getWorld(), context.getAbsolutePos(STRUCK), Direction.WEST, null, LONG)
            .size(),
        "Vines grown with no shooter");
    context.complete();
  }

  private static void wall(final TestContext context, final int height) {
    for (int up = 0; up < height; up++) {
      context.setBlockState(STRUCK.up(up), Blocks.STONE);
    }
  }

  private static List<BlockPos> grow(
      final TestContext context, final Direction face, final int length) {
    return VineService.grow(
        context.getWorld(),
        context.getAbsolutePos(STRUCK),
        face,
        context.createMockCreativeServerPlayerInWorld(),
        length);
  }
}
