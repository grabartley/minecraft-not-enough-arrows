package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.TripwireArrowConfig;
import com.grahambartley.notenougharrows.discovery.TripwireService;
import com.grahambartley.notenougharrows.reveal.Watcher;
import com.grahambartley.notenougharrows.reveal.WatcherService;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public final class TripwireServiceGameTest implements FabricGameTest {
  private static final String BATCH = "tripwire-service";
  private static final BlockPos WALL = new BlockPos(3, 3, 3);
  private static final TripwireArrowConfig CONFIG = new TripwireArrowConfig(600, 60);

  @BeforeBatch(batchId = BATCH)
  public void forgetWatchersBeforeBatch(ServerWorld world) {
    WatcherService.forget();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void setsAWatcherInTheSpaceInFrontOfTheStruckFace(TestContext context) {
    context.setBlockState(WALL, Blocks.STONE);
    final ServerPlayerEntity shooter = context.createMockCreativeServerPlayerInWorld();

    final Optional<Watcher> watcher = set(context, Direction.WEST, shooter);

    context.assertTrue(watcher.isPresent(), "A watcher should be set");
    context.assertEquals(context.getAbsolutePos(WALL.west()), watcher.get().pos(), "Where");
    context.assertEquals(shooter.getUuid(), watcher.get().owner(), "Whose");
    context.assertEquals(
        context.getWorld().getTime() + 600, watcher.get().expiryTick(), "When it expires");
    context.assertEquals(60, watcher.get().reportIntervalTicks(), "How often it may report");
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void aWatcherTakesUpNoBlock(TestContext context) {
    context.setBlockState(WALL, Blocks.STONE);

    set(context, Direction.WEST, null);

    context.expectBlock(Blocks.AIR, WALL.west());
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void noWatcherIsSetInsideASolidBlock(TestContext context) {
    context.setBlockState(WALL, Blocks.STONE);
    context.setBlockState(WALL.west(), Blocks.STONE);

    context.assertTrue(set(context, Direction.WEST, null).isEmpty(), "Nowhere to set it");
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void aWatcherMaySitInGrassOrWater(TestContext context) {
    context.setBlockState(WALL, Blocks.STONE);
    context.setBlockState(WALL.west(), Blocks.SHORT_GRASS);
    context.setBlockState(WALL.north(), Blocks.WATER);

    context.assertTrue(set(context, Direction.WEST, null).isPresent(), "Grass is open space");
    context.assertTrue(set(context, Direction.NORTH, null).isPresent(), "Water is open space");
    context.complete();
  }

  private static Optional<Watcher> set(
      final TestContext context, final Direction face, final ServerPlayerEntity shooter) {
    return TripwireService.set(
        context.getWorld(), context.getAbsolutePos(WALL), face, shooter, CONFIG);
  }
}
