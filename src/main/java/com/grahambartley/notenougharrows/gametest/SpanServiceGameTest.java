package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModBlocks;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import com.grahambartley.notenougharrows.zipline.Span;
import com.grahambartley.notenougharrows.zipline.SpanRefusal;
import com.grahambartley.notenougharrows.zipline.SpanResult;
import com.grahambartley.notenougharrows.zipline.SpanService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public final class SpanServiceGameTest implements FabricGameTest {
  private static final String BATCH = "zipline-span";
  private static final int SHORT_LIFETIME_TICKS = 10;

  @BeforeBatch(batchId = BATCH)
  public void forgetSpansBeforeBatch(ServerWorld world) {
    SpanService.forget();
    TimedStructureService.forget();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aSpanStringsCableAlongTheLineBetweenItsAnchors(TestContext context) {
    final SpanResult result =
        ZiplineTestSupport.stringAcross(context, null, ZiplineTestSupport.longLived());

    context.assertTrue(result.wasStrung(), "Two anchors in reach should string a span");
    for (final BlockPos pos : ZiplineTestSupport.CABLE) {
      context.checkBlockState(
          pos,
          state ->
              state.isOf(ModBlocks.ZIPLINE_CABLE)
                  && state.get(PillarBlock.AXIS) == Direction.Axis.X,
          () -> "Every block between the anchors should be cable running along the span");
    }
    context.expectBlock(Blocks.STONE, ZiplineTestSupport.WEST_ANCHOR);
    context.expectBlock(Blocks.STONE, ZiplineTestSupport.EAST_ANCHOR);
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aSpanIsFoundFromAnyLengthOfItsCable(TestContext context) {
    final Span span =
        ZiplineTestSupport.stringAcross(context, null, ZiplineTestSupport.longLived())
            .strungSpan()
            .orElseThrow();

    for (final BlockPos pos : ZiplineTestSupport.CABLE) {
      context.assertEquals(
          span,
          SpanService.spanAt(context.getWorld(), context.getAbsolutePos(pos)).orElse(null),
          "The span should be found from " + pos);
    }
    context.assertTrue(
        SpanService.spanAt(context.getWorld(), context.getAbsolutePos(new BlockPos(3, 2, 3)))
            .isEmpty(),
        "Open air under the span is not part of it");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aSpanBeyondTheMaximumSeparationIsRefusedAndPlacesNothing(TestContext context) {
    final SpanResult result =
        ZiplineTestSupport.stringAcross(
            context, null, ZiplineTestSupport.zipline(5, ZiplineTestSupport.LONG_LIFETIME_TICKS));

    context.assertEquals(SpanRefusal.TOO_FAR, result.refusal(), "Refusal");
    ZiplineTestSupport.CABLE.forEach(pos -> context.expectBlock(Blocks.AIR, pos));
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aSpanWithSomethingInTheWayIsRefusedRatherThanShortened(TestContext context) {
    context.setBlockState(ZiplineTestSupport.MIDDLE_CABLE, Blocks.GLASS);

    final SpanResult result =
        ZiplineTestSupport.stringAcross(context, null, ZiplineTestSupport.longLived());

    context.assertEquals(SpanRefusal.BLOCKED, result.refusal(), "Refusal");
    context.expectBlock(Blocks.AIR, ZiplineTestSupport.CABLE.getFirst());
    context.expectBlock(Blocks.AIR, ZiplineTestSupport.CABLE.getLast());
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aSpanCrossingAProtectionBoundaryIsRefusedWhole(TestContext context) {
    final var shooter = context.createMockCreativeServerPlayerInWorld();
    final SpanResult[] result = new SpanResult[1];

    TraversalTestSupport.withTheBorderEastEdgeAt(
        context,
        4,
        () ->
            result[0] =
                ZiplineTestSupport.stringAcross(context, shooter, ZiplineTestSupport.longLived()));

    context.assertEquals(SpanRefusal.BLOCKED, result[0].refusal(), "Refusal");
    ZiplineTestSupport.CABLE.forEach(pos -> context.expectBlock(Blocks.AIR, pos));
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aSpanNeedsBothEndsToBeAnchorSites(TestContext context) {
    ZiplineTestSupport.raiseAnchors(context);
    context.setBlockState(ZiplineTestSupport.WEST_ANCHOR, Blocks.SHORT_GRASS);

    final SpanResult result =
        SpanService.string(
            context.getWorld(),
            null,
            context.getAbsolutePos(ZiplineTestSupport.WEST_ANCHOR),
            context.getAbsolutePos(ZiplineTestSupport.EAST_ANCHOR),
            ZiplineTestSupport.longLived());

    context.assertEquals(SpanRefusal.NOT_AN_ANCHOR, result.refusal(), "Refusal");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aLifetimeOfZeroStringsNothing(TestContext context) {
    final SpanResult result =
        ZiplineTestSupport.stringAcross(
            context, null, ZiplineTestSupport.zipline(ZiplineTestSupport.CABLE.size() + 2, 0));

    context.assertFalse(result.wasStrung(), "A zero lifetime should string nothing");
    ZiplineTestSupport.CABLE.forEach(pos -> context.expectBlock(Blocks.AIR, pos));
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aSpanWithALengthCutOutIsNoLongerRideable(TestContext context) {
    final Span span =
        ZiplineTestSupport.stringAcross(context, null, ZiplineTestSupport.longLived())
            .strungSpan()
            .orElseThrow();

    context.setBlockState(ZiplineTestSupport.MIDDLE_CABLE, Blocks.AIR);

    context.assertFalse(
        SpanService.isIntact(context.getWorld(), span), "A cut span should not be intact");
    context.assertTrue(
        SpanService.spanAt(
                context.getWorld(), context.getAbsolutePos(ZiplineTestSupport.CABLE.getFirst()))
            .isEmpty(),
        "A cut span should not be found for riding");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aSpanComesDownWhenItsLifetimeEnds(TestContext context) {
    final Span span =
        ZiplineTestSupport.stringAcross(
                context,
                null,
                ZiplineTestSupport.zipline(
                    ZiplineTestSupport.CABLE.size() + 2, SHORT_LIFETIME_TICKS))
            .strungSpan()
            .orElseThrow();

    context.runAtTick(
        SHORT_LIFETIME_TICKS + 3,
        () -> {
          ZiplineTestSupport.CABLE.forEach(pos -> context.expectBlock(Blocks.AIR, pos));
          context.assertFalse(
              SpanService.isIntact(context.getWorld(), span), "An expired span is gone");
          context.assertTrue(
              SpanService.find(context.getWorld(), span.id()).isEmpty(),
              "An expired span should be forgotten");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aCableDropsNothingWhenBroken(TestContext context) {
    ZiplineTestSupport.stringAcross(context, null, ZiplineTestSupport.longLived());

    context.getWorld().breakBlock(context.getAbsolutePos(ZiplineTestSupport.MIDDLE_CABLE), true);

    context.assertTrue(
        context
            .getWorld()
            .getEntitiesByClass(ItemEntity.class, context.getTestBox(), drop -> true)
            .isEmpty(),
        "A cable is a route, not a resource, so it should drop nothing");
    context.complete();
  }
}
