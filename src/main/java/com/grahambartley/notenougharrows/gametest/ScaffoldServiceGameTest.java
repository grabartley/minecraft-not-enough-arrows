package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.ScaffoldArrowConfig;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import com.grahambartley.notenougharrows.traversal.ScaffoldService;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class ScaffoldServiceGameTest implements FabricGameTest {
  private static final String BATCH = "scaffold";
  private static final BlockPos GROUND = new BlockPos(3, 3, 3);
  private static final int HEIGHT = 3;
  private static final int LONG_LIFETIME_TICKS = 400;
  private static final int SHORT_LIFETIME_TICKS = 10;

  @BeforeBatch(batchId = BATCH)
  public void forgetStructuresBeforeBatch(ServerWorld world) {
    TimedStructureService.forget();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aScaffoldRisesFromTheGroundToItsHeight(TestContext context) {
    final List<BlockPos> column = raise(context, GROUND, HEIGHT, LONG_LIFETIME_TICKS);

    context.assertEquals(HEIGHT, column.size(), "Scaffolds raised");
    for (int up = 0; up < HEIGHT; up++) {
      context.checkBlockState(
          GROUND.up(up),
          state -> state.isOf(Blocks.SCAFFOLDING) && state.get(ScaffoldingBlock.DISTANCE) == 0,
          () -> "Every scaffold should stand on the one beneath it");
    }
    context.expectBlock(Blocks.AIR, GROUND.up(HEIGHT));
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aScaffoldShotIntoAWallMidAirSettlesToTheGroundBeneath(TestContext context) {
    raise(context, GROUND.up(2), HEIGHT, LONG_LIFETIME_TICKS);

    context.runAtTick(
        3,
        () -> {
          context.expectBlock(Blocks.SCAFFOLDING, GROUND);
          context.expectBlock(Blocks.SCAFFOLDING, GROUND.up(2));
          context.assertTrue(
              context
                  .getWorld()
                  .getEntitiesByClass(ItemEntity.class, context.getTestBox(), drop -> true)
                  .isEmpty(),
              "No scaffold should have fallen or broken for want of support");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aScaffoldStopsAtTheFirstThingAboveIt(TestContext context) {
    context.setBlockState(GROUND.up(2), Blocks.STONE);

    context.assertEquals(
        2, raise(context, GROUND, HEIGHT, LONG_LIFETIME_TICKS).size(), "Scaffolds raised");
    context.expectBlock(Blocks.STONE, GROUND.up(2));
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aScaffoldWithNoGroundInReachRaisesNothing(TestContext context) {
    context.assertTrue(
        raise(context, GROUND.up(3), 2, LONG_LIFETIME_TICKS).isEmpty(),
        "Nothing within reach below to stand a scaffold on");
    context.expectBlock(Blocks.AIR, GROUND.up(3));
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aScaffoldClearsAwayWithoutDroppingAnything(TestContext context) {
    raise(context, GROUND, HEIGHT, SHORT_LIFETIME_TICKS);

    context.runAtTick(
        SHORT_LIFETIME_TICKS + 5,
        () -> {
          for (int up = 0; up < HEIGHT; up++) {
            context.expectBlock(Blocks.AIR, GROUND.up(up));
          }
          context.assertTrue(
              context
                  .getWorld()
                  .getEntitiesByClass(ItemEntity.class, context.getTestBox(), drop -> true)
                  .isEmpty(),
              "An expiring scaffold leaves no scaffolding to pick up");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aScaffoldNeverRisesWhereTheShooterMayNotBuild(TestContext context) {
    final ServerPlayerEntity shooter = context.createMockCreativeServerPlayerInWorld();
    final List<BlockPos>[] column = new List[1];

    TraversalTestSupport.withTheBorderEastEdgeAt(
        context,
        GROUND.getX(),
        () ->
            column[0] =
                ScaffoldService.raise(
                    context.getWorld(),
                    context.getAbsolutePos(GROUND),
                    shooter,
                    new ScaffoldArrowConfig(HEIGHT, LONG_LIFETIME_TICKS)));

    context.assertTrue(column[0].isEmpty(), "Nothing rises across the boundary");
    context.expectBlock(Blocks.AIR, GROUND);
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aScaffoldWithNoLifetimeRaisesNothing(TestContext context) {
    context.assertTrue(raise(context, GROUND, HEIGHT, 0).isEmpty(), "Zero lifetime");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aScaffoldIsAClimbableColumnAPlayerCanStandIn(TestContext context) {
    raise(context, GROUND, HEIGHT, LONG_LIFETIME_TICKS);

    context.checkBlockState(
        GROUND.up(),
        state -> state.isIn(BlockTags.CLIMBABLE),
        () -> "Scaffolding is climbable, which is what separates it from a pillar");
    context.complete();
  }

  private static List<BlockPos> raise(
      final TestContext context, final BlockPos impact, final int height, final int lifetime) {
    return ScaffoldService.raise(
        context.getWorld(),
        context.getAbsolutePos(impact),
        null,
        new ScaffoldArrowConfig(height, lifetime));
  }
}
