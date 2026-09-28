package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.BridgeArrowConfig;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import com.grahambartley.notenougharrows.traversal.BridgeService;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class BridgeServiceGameTest implements FabricGameTest {
  private static final String BATCH = "bridge";
  private static final BlockPos EAST_EDGE = new BlockPos(6, 3, 3);
  private static final BlockPos WEST_EDGE = new BlockPos(0, 3, 3);
  private static final Vec3d WEST_SHOOTER = new Vec3d(0.5, 3.0, 3.5);
  private static final Vec3d EAST_SHOOTER = new Vec3d(6.5, 3.0, 3.5);
  private static final int LONG = 16;
  private static final int LONG_LIFETIME_TICKS = 400;
  private static final int SHORT_LIFETIME_TICKS = 10;

  @BeforeBatch(batchId = BATCH)
  public void forgetStructuresBeforeBatch(ServerWorld world) {
    TimedStructureService.forget();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aBridgeRunsLevelFromTheStruckBlockBackToTheShooter(TestContext context) {
    context.setBlockState(EAST_EDGE, Blocks.STONE);

    final List<BlockPos> walkway =
        lay(context, EAST_EDGE, WEST_SHOOTER, null, bridge(LONG, LONG_LIFETIME_TICKS));

    context.assertEquals(6, walkway.size(), "Planks laid, ending beneath the shooter");
    for (int x = 0; x < 6; x++) {
      context.expectBlock(Blocks.OAK_PLANKS, new BlockPos(x, 3, 3));
    }
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aBridgeStopsAtItsConfiguredLength(TestContext context) {
    context.setBlockState(EAST_EDGE, Blocks.STONE);

    context.assertEquals(
        2,
        lay(context, EAST_EDGE, WEST_SHOOTER, null, bridge(2, LONG_LIFETIME_TICKS)).size(),
        "Planks laid");
    context.expectBlock(Blocks.AIR, new BlockPos(3, 3, 3));
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aBridgeStopsAtTheFirstThingInItsWay(TestContext context) {
    context.setBlockState(EAST_EDGE, Blocks.STONE);
    context.setBlockState(new BlockPos(3, 3, 3), Blocks.GLASS);

    context.assertEquals(
        2,
        lay(context, EAST_EDGE, WEST_SHOOTER, null, bridge(LONG, LONG_LIFETIME_TICKS)).size(),
        "Planks laid up to the glass");
    context.expectBlock(Blocks.GLASS, new BlockPos(3, 3, 3));
    context.expectBlock(Blocks.AIR, new BlockPos(2, 3, 3));
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aBridgeNeverEntombsSomethingStandingInItsPath(TestContext context) {
    context.setBlockState(EAST_EDGE, Blocks.STONE);
    final ArmorStandEntity stand =
        context.spawnEntity(EntityType.ARMOR_STAND, new BlockPos(3, 3, 3));
    stand.setNoGravity(true);

    context.assertEquals(
        2,
        lay(context, EAST_EDGE, WEST_SHOOTER, null, bridge(LONG, LONG_LIFETIME_TICKS)).size(),
        "Planks laid up to the armor stand");
    context.expectBlock(Blocks.AIR, new BlockPos(3, 3, 3));
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aBridgeCrossingAProtectionBoundaryIsTruncatedAtIt(TestContext context) {
    context.setBlockState(WEST_EDGE, Blocks.STONE);
    final PlayerEntity shooter = context.createMockCreativeServerPlayerInWorld();
    final List<BlockPos>[] walkway = new List[1];

    TraversalTestSupport.withTheBorderEastEdgeAt(
        context,
        4,
        () ->
            walkway[0] =
                lay(context, WEST_EDGE, EAST_SHOOTER, shooter, bridge(LONG, LONG_LIFETIME_TICKS)));

    context.assertEquals(3, walkway[0].size(), "Planks laid inside the boundary");
    context.expectBlock(Blocks.OAK_PLANKS, new BlockPos(3, 3, 3));
    context.expectBlock(Blocks.AIR, new BlockPos(4, 3, 3));
    context.expectBlock(Blocks.AIR, new BlockPos(5, 3, 3));
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aBridgeClearsAwayWhenItsLifetimeEnds(TestContext context) {
    context.setBlockState(EAST_EDGE, Blocks.STONE);
    lay(context, EAST_EDGE, WEST_SHOOTER, null, bridge(LONG, SHORT_LIFETIME_TICKS));

    context.runAtTick(
        SHORT_LIFETIME_TICKS + 3,
        () -> {
          for (int x = 0; x < 6; x++) {
            context.expectBlock(Blocks.AIR, new BlockPos(x, 3, 3));
          }
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aBridgeWithNoLifetimeLaysNothing(TestContext context) {
    context.setBlockState(EAST_EDGE, Blocks.STONE);

    context.assertTrue(
        lay(context, EAST_EDGE, WEST_SHOOTER, null, bridge(LONG, 0)).isEmpty(), "Nothing laid");
    context.complete();
  }

  private static BridgeArrowConfig bridge(final int length, final int lifetime) {
    return new BridgeArrowConfig(length, lifetime);
  }

  private static List<BlockPos> lay(
      final TestContext context,
      final BlockPos struck,
      final Vec3d relativeToward,
      final PlayerEntity shooter,
      final BridgeArrowConfig bridge) {
    return BridgeService.lay(
        context.getWorld(),
        context.getAbsolutePos(struck),
        context.getAbsolute(relativeToward),
        shooter,
        bridge);
  }
}
