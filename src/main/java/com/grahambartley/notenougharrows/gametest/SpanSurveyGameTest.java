package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModBlocks;
import com.grahambartley.notenougharrows.structure.StructureBlockSource;
import com.grahambartley.notenougharrows.structure.StructureBudget;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import com.grahambartley.notenougharrows.zipline.SpanLine;
import com.grahambartley.notenougharrows.zipline.SpanRefusal;
import com.grahambartley.notenougharrows.zipline.SpanSurvey;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class SpanSurveyGameTest implements FabricGameTest {
  private static final String BATCH = "zipline-survey";
  private static final int ROOMY = 32;

  @BeforeBatch(batchId = BATCH)
  public void forgetStructuresBeforeBatch(ServerWorld world) {
    TimedStructureService.forget();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aClearPathBetweenTwoAnchorsIsNotRefused(TestContext context) {
    ZiplineTestSupport.raiseAnchors(context);

    context.assertTrue(survey(context, ROOMY).isEmpty(), "A clear path should be accepted");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anEndThatIsNotAnAnchorSiteIsRefused(TestContext context) {
    ZiplineTestSupport.raiseAnchors(context);
    context.setBlockState(ZiplineTestSupport.EAST_ANCHOR, Blocks.WATER);

    context.assertEquals(
        Optional.of(SpanRefusal.NOT_AN_ANCHOR), survey(context, ROOMY), "Water is no anchor");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void endsFurtherApartThanTheMaximumAreRefused(TestContext context) {
    ZiplineTestSupport.raiseAnchors(context);

    context.assertEquals(Optional.of(SpanRefusal.TOO_FAR), survey(context, 5), "Refusal");
    context.assertTrue(survey(context, 6).isEmpty(), "Exactly the maximum is allowed");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void adjacentAnchorsLeaveNothingToRide(TestContext context) {
    final BlockPos west = new BlockPos(2, 5, 3);
    final BlockPos east = new BlockPos(3, 5, 3);
    TraversalTestSupport.stone(context, west, east);

    context.assertEquals(
        Optional.of(SpanRefusal.TOO_SHORT),
        SpanSurvey.refusal(
            context.getWorld(),
            context.getAbsolutePos(west),
            context.getAbsolutePos(east),
            List.of(),
            cable(),
            null,
            ROOMY),
        "Refusal");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPathThroughAnotherStructureIsRefused(TestContext context) {
    ZiplineTestSupport.raiseAnchors(context);
    TimedStructureService.build(
        context.getWorld(),
        null,
        List.of(context.getAbsolutePos(ZiplineTestSupport.MIDDLE_CABLE)),
        StructureBlockSource.of(Blocks.COBWEB.getDefaultState()),
        StructureBudget.of(1),
        ZiplineTestSupport.LONG_LIFETIME_TICKS);

    context.assertEquals(Optional.of(SpanRefusal.BLOCKED), survey(context, ROOMY), "Refusal");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPathThroughGrassIsAccepted(TestContext context) {
    ZiplineTestSupport.raiseAnchors(context);
    context.setBlockState(ZiplineTestSupport.MIDDLE_CABLE.down(), Blocks.DIRT);
    context.setBlockState(ZiplineTestSupport.MIDDLE_CABLE, Blocks.SHORT_GRASS);

    context.assertTrue(
        survey(context, ROOMY).isEmpty(), "Grass is replaceable, so a span may string through it");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPathOutsideTheBorderIsRefusedForADispensedShot(TestContext context) {
    ZiplineTestSupport.raiseAnchors(context);
    final Optional<SpanRefusal>[] refusal = new Optional[1];

    TerrainTestSupport.withTheBorderElsewhere(context, () -> refusal[0] = survey(context, ROOMY));

    context.assertEquals(Optional.of(SpanRefusal.BLOCKED), refusal[0], "Refusal");
    context.complete();
  }

  private static Optional<SpanRefusal> survey(final TestContext context, final int maxSpan) {
    final BlockPos west = context.getAbsolutePos(ZiplineTestSupport.WEST_ANCHOR);
    final BlockPos east = context.getAbsolutePos(ZiplineTestSupport.EAST_ANCHOR);
    return SpanSurvey.refusal(
        context.getWorld(), west, east, SpanLine.between(west, east), cable(), null, maxSpan);
  }

  private static BlockState cable() {
    return ModBlocks.ZIPLINE_CABLE.getDefaultState();
  }
}
