package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.ExplosiveArrowConfig;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.grahambartley.notenougharrows.fire.FirePatchService;
import com.grahambartley.notenougharrows.fire.FirePatchShape;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class FirePatchServiceGameTest implements FabricGameTest {
  private static final String BATCH = "fire-patch-lifecycle";
  private static final String TEMPLATE = "not-enough-arrows:fire_pad";
  private static final BlockPos CENTRE = new BlockPos(3, 3, 3);
  private static final int RADIUS = 2;
  private static final int SHORT_DURATION_TICKS = 10;
  private static final int LONG_DURATION_TICKS = 400;

  @BeforeBatch(batchId = BATCH)
  public void forgetPatchesBeforeBatch(ServerWorld world) {
    FirePatchService.forget();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aPatchStopsBurningOnceItsDurationElapses(TestContext context) {
    context.runAtTick(
        5,
        () -> {
          final List<BlockPos> placed = ignite(context, RADIUS, SHORT_DURATION_TICKS);
          context.assertFalse(placed.isEmpty(), "Igniting a patch should light at least one block");

          context.runAtTick(
              5 + SHORT_DURATION_TICKS + 5,
              () -> {
                context.assertFalse(placed.isEmpty(), "The patch should have lit blocks");
                for (final BlockPos column : FirePatchShape.columns(CENTRE, RADIUS)) {
                  context.expectBlock(Blocks.AIR, column);
                }
                context.complete();
              });
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aPatchKeepsBurningUntilItsDurationElapses(TestContext context) {
    context.runAtTick(
        5,
        () -> {
          final List<BlockPos> placed = ignite(context, RADIUS, LONG_DURATION_TICKS);

          context.runAtTick(
              40,
              () -> {
                context.assertFalse(placed.isEmpty(), "The patch should have lit blocks");
                for (final BlockPos column : FirePatchShape.columns(CENTRE, RADIUS)) {
                  context.expectBlock(Blocks.FIRE, column);
                }
                context.complete();
              });
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aDurationOfZeroLightsNothingAtAll(TestContext context) {
    context.assertTrue(
        ignite(context, RADIUS, 0).isEmpty(), "A zero duration should light nothing");
    context.expectBlock(Blocks.AIR, CENTRE);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRadiusOfZeroLightsNothingAtAll(TestContext context) {
    context.assertTrue(
        ignite(context, 0, LONG_DURATION_TICKS).isEmpty(), "A zero radius should light nothing");
    context.expectBlock(Blocks.AIR, CENTRE);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void theLiveServerConfigDecidesHowBigAPatchIs(TestContext context) {
    final BlockPos centre = context.getAbsolutePos(CENTRE);
    final NotEnoughArrowsConfig previous = ServerConfigService.get();
    final List<BlockPos> placed;
    try {
      ServerConfigHolder.set(configWithFirePatch(1, LONG_DURATION_TICKS));
      placed = FirePatchService.ignite(context.getWorld(), centre, null);
    } finally {
      ServerConfigHolder.set(previous);
    }

    context.assertEquals(
        placed.size(),
        FirePatchShape.columns(centre, 1).size(),
        "Fire blocks lit for a configured radius of one");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void theLiveServerConfigCanSwitchFirePatchesOff(TestContext context) {
    final NotEnoughArrowsConfig previous = ServerConfigService.get();
    final List<BlockPos> placed;
    try {
      ServerConfigHolder.set(configWithFirePatch(RADIUS, 0));
      placed = FirePatchService.ignite(context.getWorld(), context.getAbsolutePos(CENTRE), null);
    } finally {
      ServerConfigHolder.set(previous);
    }

    context.assertTrue(placed.isEmpty(), "A configured duration of zero should light nothing");
    context.expectBlock(Blocks.AIR, CENTRE);
    context.complete();
  }

  private static List<BlockPos> ignite(
      final TestContext context, final int radius, final int durationTicks) {
    return FirePatchService.ignite(
        context.getWorld(), context.getAbsolutePos(CENTRE), null, radius, durationTicks);
  }

  private static NotEnoughArrowsConfig configWithFirePatch(
      final int radius, final int durationTicks) {
    final ExplosiveArrowConfig explosive = ExplosiveArrowConfig.defaults();
    return NotEnoughArrowsConfig.defaults()
        .withExplosive(
            explosive.withFirePatchRadius(radius).withFirePatchDurationTicks(durationTicks));
  }
}
