package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModBlocks;
import com.grahambartley.notenougharrows.TraversalArrows;
import com.grahambartley.notenougharrows.config.ZiplineArrowConfig;
import com.grahambartley.notenougharrows.entity.ZiplineArrowEntity;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import com.grahambartley.notenougharrows.zipline.PendingAnchorService;
import com.grahambartley.notenougharrows.zipline.SpanService;
import com.grahambartley.notenougharrows.zipline.ZiplineOutcome;
import com.grahambartley.notenougharrows.zipline.ZiplineService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class ZiplineServiceGameTest implements FabricGameTest {
  private static final String BATCH = "zipline-shot";
  private static final BlockPos ARROW_REST = new BlockPos(3, 3, 5);
  private static final int NARROW_WINDOW_TICKS = ZiplineArrowConfig.PENDING_WINDOW_TICKS_MIN;

  @BeforeBatch(batchId = BATCH)
  public void forgetZiplinesBeforeBatch(ServerWorld world) {
    PendingAnchorService.forget();
    SpanService.forget();
    TimedStructureService.forget();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aFirstShotSetsAPendingAnchorAndStringsNothing(TestContext context) {
    ZiplineTestSupport.raiseAnchors(context);
    final ServerPlayerEntity shooter = context.createMockCreativeServerPlayerInWorld();

    final ZiplineOutcome outcome =
        shoot(context, shooter, ZiplineTestSupport.WEST_ANCHOR, arrow(context));

    context.assertEquals(ZiplineOutcome.ANCHOR_SET, outcome, "Outcome");
    context.assertEquals(
        context.getAbsolutePos(ZiplineTestSupport.WEST_ANCHOR),
        PendingAnchorService.pendingFor(shooter.getUuid()).orElseThrow().pos(),
        "Pending anchor");
    ZiplineTestSupport.CABLE.forEach(pos -> context.expectBlock(Blocks.AIR, pos));
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aSecondShotStringsTheSpanAndConsumesTheFirstArrow(TestContext context) {
    ZiplineTestSupport.raiseAnchors(context);
    final ServerPlayerEntity shooter = context.createMockCreativeServerPlayerInWorld();
    final ZiplineArrowEntity first = arrow(context);
    shoot(context, shooter, ZiplineTestSupport.WEST_ANCHOR, first);

    final ZiplineOutcome outcome =
        shoot(context, shooter, ZiplineTestSupport.EAST_ANCHOR, arrow(context));

    context.assertEquals(ZiplineOutcome.STRUNG, outcome, "Outcome");
    ZiplineTestSupport.CABLE.forEach(pos -> context.expectBlock(ModBlocks.ZIPLINE_CABLE, pos));
    context.assertTrue(
        first.isRemoved(), "The first arrow is the span's other end, so it is spent");
    context.assertTrue(
        PendingAnchorService.pendingFor(shooter.getUuid()).isEmpty(),
        "Stringing a span uses the pending anchor up");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aFirstArrowPickedBackUpLeavesNothingToStringTo(TestContext context) {
    ZiplineTestSupport.raiseAnchors(context);
    final ServerPlayerEntity shooter = context.createMockCreativeServerPlayerInWorld();
    final ZiplineArrowEntity first = arrow(context);
    shoot(context, shooter, ZiplineTestSupport.WEST_ANCHOR, first);
    first.discard();

    final ZiplineOutcome outcome =
        shoot(context, shooter, ZiplineTestSupport.EAST_ANCHOR, arrow(context));

    context.assertEquals(ZiplineOutcome.ANCHOR_SET, outcome, "Outcome");
    ZiplineTestSupport.CABLE.forEach(pos -> context.expectBlock(Blocks.AIR, pos));
    context.assertEquals(
        context.getAbsolutePos(ZiplineTestSupport.EAST_ANCHOR),
        PendingAnchorService.pendingFor(shooter.getUuid()).orElseThrow().pos(),
        "The second shot becomes the pending anchor instead");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aSecondShotTooFarAwayReplacesThePendingAnchor(TestContext context) {
    ZiplineTestSupport.raiseAnchors(context);
    final ServerPlayerEntity shooter = context.createMockCreativeServerPlayerInWorld();
    final ZiplineArrowConfig tight =
        ZiplineTestSupport.zipline(ZiplineArrowConfig.MAX_SPAN_BLOCKS_MIN, 2000);
    ZiplineService.shoot(
        context.getWorld(),
        shooter,
        context.getAbsolutePos(ZiplineTestSupport.WEST_ANCHOR),
        arrow(context).getUuid(),
        tight);

    final ZiplineOutcome outcome =
        ZiplineService.shoot(
            context.getWorld(),
            shooter,
            context.getAbsolutePos(ZiplineTestSupport.EAST_ANCHOR),
            arrow(context).getUuid(),
            tight);

    context.assertEquals(ZiplineOutcome.TOO_FAR, outcome, "Outcome");
    context.assertEquals(
        context.getAbsolutePos(ZiplineTestSupport.EAST_ANCHOR),
        PendingAnchorService.pendingFor(shooter.getUuid()).orElseThrow().pos(),
        "A refused second shot replaces the pending anchor rather than queuing");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aShotWithNoShooterAnchorsNothing(TestContext context) {
    ZiplineTestSupport.raiseAnchors(context);

    context.assertEquals(
        ZiplineOutcome.NOTHING,
        shoot(context, null, ZiplineTestSupport.WEST_ANCHOR, arrow(context)),
        "Outcome");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aShotIntoSomethingThatIsNotAnAnchorSiteAnchorsNothing(TestContext context) {
    context.setBlockState(ZiplineTestSupport.WEST_ANCHOR.down(), Blocks.DIRT);
    context.setBlockState(ZiplineTestSupport.WEST_ANCHOR, Blocks.SHORT_GRASS);
    final ServerPlayerEntity shooter = context.createMockCreativeServerPlayerInWorld();

    context.assertEquals(
        ZiplineOutcome.NOTHING,
        shoot(context, shooter, ZiplineTestSupport.WEST_ANCHOR, arrow(context)),
        "Outcome");
    context.assertTrue(
        PendingAnchorService.pendingFor(shooter.getUuid()).isEmpty(), "Nothing is pending");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aPendingAnchorOutsideItsWindowIsDiscardedAndNotPairedWith(TestContext context) {
    ZiplineTestSupport.raiseAnchors(context);
    final ServerPlayerEntity shooter = context.createMockCreativeServerPlayerInWorld();
    final ZiplineArrowConfig narrow =
        new ZiplineArrowConfig(
            ZiplineArrowConfig.DEFAULT_MAX_SPAN_BLOCKS,
            NARROW_WINDOW_TICKS,
            ZiplineArrowConfig.DEFAULT_RIDE_SPEED,
            ZiplineTestSupport.LONG_LIFETIME_TICKS);
    final ZiplineArrowEntity first = arrow(context);
    ZiplineService.shoot(
        context.getWorld(),
        shooter,
        context.getAbsolutePos(ZiplineTestSupport.WEST_ANCHOR),
        first.getUuid(),
        narrow);

    context.runAtTick(
        NARROW_WINDOW_TICKS + 5,
        () -> {
          context.assertTrue(
              PendingAnchorService.pendingFor(shooter.getUuid()).isEmpty(),
              "An expired pending anchor should be discarded");
          context.assertFalse(
              first.isRemoved(), "Discarding a pending anchor does not take the arrow");
          context.assertEquals(
              ZiplineOutcome.ANCHOR_SET,
              ZiplineService.shoot(
                  context.getWorld(),
                  shooter,
                  context.getAbsolutePos(ZiplineTestSupport.EAST_ANCHOR),
                  arrow(context).getUuid(),
                  narrow),
              "Outcome");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPendingAnchorWhoseBlockWasReplacedIsNotPairedWith(TestContext context) {
    ZiplineTestSupport.raiseAnchors(context);
    final ServerPlayerEntity shooter = context.createMockCreativeServerPlayerInWorld();
    shoot(context, shooter, ZiplineTestSupport.WEST_ANCHOR, arrow(context));
    context.setBlockState(ZiplineTestSupport.WEST_ANCHOR, Blocks.COBBLESTONE);

    context.assertEquals(
        ZiplineOutcome.ANCHOR_SET,
        shoot(context, shooter, ZiplineTestSupport.EAST_ANCHOR, arrow(context)),
        "Outcome");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void oneShootersPendingAnchorIsNeverPairedWithAnothersShot(TestContext context) {
    ZiplineTestSupport.raiseAnchors(context);
    final ServerPlayerEntity first = context.createMockCreativeServerPlayerInWorld();
    final ServerPlayerEntity second = context.createMockCreativeServerPlayerInWorld();
    shoot(context, first, ZiplineTestSupport.WEST_ANCHOR, arrow(context));

    context.assertEquals(
        ZiplineOutcome.ANCHOR_SET,
        shoot(context, second, ZiplineTestSupport.EAST_ANCHOR, arrow(context)),
        "Outcome");
    context.assertTrue(
        PendingAnchorService.pendingFor(first.getUuid()).isPresent(),
        "The first shooter's pending anchor is untouched");
    ZiplineTestSupport.CABLE.forEach(pos -> context.expectBlock(Blocks.AIR, pos));
    context.complete();
  }

  private static ZiplineOutcome shoot(
      final TestContext context,
      final ServerPlayerEntity shooter,
      final BlockPos anchor,
      final ZiplineArrowEntity arrow) {
    return ZiplineService.shoot(
        context.getWorld(),
        shooter,
        context.getAbsolutePos(anchor),
        arrow.getUuid(),
        ZiplineTestSupport.longLived());
  }

  private static ZiplineArrowEntity arrow(final TestContext context) {
    final ZiplineArrowEntity arrow =
        context.spawnEntity(TraversalArrows.ZIPLINE_ARROW.entityType(), ARROW_REST);
    arrow.setNoGravity(true);
    return arrow;
  }
}
