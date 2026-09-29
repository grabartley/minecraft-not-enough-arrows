package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.ProspectorArrowConfig;
import com.grahambartley.notenougharrows.reveal.BlockRevealPulse;
import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class BlockRevealPulseGameTest implements FabricGameTest {
  private static final String BATCH = "block-reveal-pulse";
  private static final BlockPos CENTER = new BlockPos(3, 8, 8);
  private static final int DURATION_TICKS = 100;
  private static final Predicate<BlockState> DIAMONDS = state -> state.isOf(Blocks.DIAMOND_ORE);

  @GameTest(templateName = DiscoveryTestSupport.ARENA, batchId = BATCH, tickLimit = 10)
  public void revealsTheListedBlocksAroundTheImpactAndNothingElse(TestContext context) {
    context.setBlockState(CENTER.east(3), Blocks.DIAMOND_ORE);
    context.setBlockState(CENTER.up(2), Blocks.STONE);

    context.assertEquals(
        List.of(context.getAbsolutePos(CENTER.east(3))),
        scan(context, 8, DURATION_TICKS),
        "Blocks revealed");
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.ARENA, batchId = BATCH, tickLimit = 10)
  public void reachesTheConfiguredMaximumRadiusAndNoFurther(TestContext context) {
    final int radius = ProspectorArrowConfig.RADIUS_MAX;
    context.setBlockState(CENTER.east(radius), Blocks.DIAMOND_ORE);
    context.setBlockState(CENTER.east(radius + 1), Blocks.DIAMOND_ORE);

    context.assertEquals(
        List.of(context.getAbsolutePos(CENTER.east(radius))),
        scan(context, radius, DURATION_TICKS),
        "Blocks revealed at the maximum radius");
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.ARENA, batchId = BATCH, tickLimit = 10)
  public void aZeroDurationRevealsNothing(TestContext context) {
    context.setBlockState(CENTER.east(2), Blocks.DIAMOND_ORE);

    context.assertTrue(scan(context, 8, 0).isEmpty(), "A zero duration reveals nothing");
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.ARENA, batchId = BATCH, tickLimit = 10)
  public void firingTellsTheShooterWhatItFound(TestContext context) {
    context.setBlockState(CENTER.east(2), Blocks.DIAMOND_ORE);
    final ServerPlayerEntity shooter = context.createMockCreativeServerPlayerInWorld();

    final List<BlockPos> found =
        BlockRevealPulse.fire(
            context.getWorld(),
            context.getAbsolutePos(CENTER),
            4,
            DURATION_TICKS,
            DIAMONDS,
            shooter);

    context.assertEquals(
        List.of(context.getAbsolutePos(CENTER.east(2))), found, "Blocks the pulse revealed");
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.ARENA, batchId = BATCH, tickLimit = 10)
  public void revealsTheNearestFirst(TestContext context) {
    context.setBlockState(CENTER.east(5), Blocks.DIAMOND_ORE);
    context.setBlockState(CENTER.east(1), Blocks.DIAMOND_ORE);

    context.assertEquals(
        List.of(context.getAbsolutePos(CENTER.east(1)), context.getAbsolutePos(CENTER.east(5))),
        scan(context, 8, DURATION_TICKS),
        "Blocks revealed, nearest first");
    context.complete();
  }

  private static List<BlockPos> scan(
      final TestContext context, final int radius, final int durationTicks) {
    return BlockRevealPulse.scan(
        context.getWorld(), context.getAbsolutePos(CENTER), radius, durationTicks, DIAMONDS);
  }
}
