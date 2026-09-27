package com.grahambartley.notenougharrows.gametest;

import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.FIRST;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.FOURTH;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.LINE;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.PLANKS;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.SECOND;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.TEMPLATE;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.THIRD;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.absolute;

import com.grahambartley.notenougharrows.structure.StructureBlock;
import com.grahambartley.notenougharrows.structure.StructureBudget;
import com.grahambartley.notenougharrows.structure.StructurePlacer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class StructurePlacerGameTest implements FabricGameTest {
  private static final String BATCH = "structure-placer";
  private static final BlockPos FAR_OUTSIDE_THE_BORDER =
      new BlockPos(Integer.MAX_VALUE / 2, 3, Integer.MAX_VALUE / 2);
  private static final Predicate<BlockPos> NOTHING_HELD = pos -> false;

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void everyCandidateInsideTheBudgetIsPlaced(TestContext context) {
    final List<StructureBlock> placed = place(context, absolute(context, LINE), LINE.size());

    context.assertEquals(placed.size(), LINE.size(), "Blocks placed along an open line");
    LINE.forEach(pos -> context.expectBlock(Blocks.OAK_PLANKS, pos));
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void theBudgetStopsPlacementOnceItIsSpent(TestContext context) {
    final List<StructureBlock> placed = place(context, absolute(context, LINE), 2);

    context.assertEquals(placed.size(), 2, "Blocks placed under a budget of two");
    context.expectBlock(Blocks.OAK_PLANKS, FIRST);
    context.expectBlock(Blocks.OAK_PLANKS, SECOND);
    context.expectBlock(Blocks.AIR, THIRD);
    context.expectBlock(Blocks.AIR, FOURTH);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aStructureIsTruncatedAtTheFirstPositionItMayNotUse(TestContext context) {
    final List<BlockPos> candidates = new ArrayList<>(absolute(context, FIRST, SECOND));
    candidates.add(FAR_OUTSIDE_THE_BORDER);
    candidates.addAll(absolute(context, THIRD, FOURTH));

    final List<StructureBlock> placed = place(context, candidates, candidates.size());

    context.assertEquals(placed.size(), 2, "Blocks placed before the refused position");
    context.expectBlock(Blocks.OAK_PLANKS, FIRST);
    context.expectBlock(Blocks.OAK_PLANKS, SECOND);
    context.expectBlock(Blocks.AIR, THIRD);
    context.expectBlock(Blocks.AIR, FOURTH);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aBlockSomebodyBuiltIsNeverReplaced(TestContext context) {
    context.setBlockState(SECOND, Blocks.STONE);

    final List<StructureBlock> placed = place(context, absolute(context, LINE), LINE.size());

    context.assertEquals(placed.size(), LINE.size() - 1, "Blocks placed around a built block");
    context.expectBlock(Blocks.STONE, SECOND);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aReplaceableBlockMakesWay(TestContext context) {
    context.setBlockState(SECOND, Blocks.SNOW);

    final List<StructureBlock> placed = place(context, absolute(context, LINE), LINE.size());

    context.assertEquals(placed.size(), LINE.size(), "Blocks placed over a snow layer");
    context.expectBlock(Blocks.OAK_PLANKS, SECOND);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPositionAnotherStructureHoldsIsSkipped(TestContext context) {
    final BlockPos held = context.getAbsolutePos(SECOND);

    final List<StructureBlock> placed =
        StructurePlacer.place(
            context.getWorld(),
            absolute(context, LINE),
            PLANKS,
            null,
            StructureBudget.of(LINE.size()),
            held::equals);

    context.assertEquals(placed.size(), LINE.size() - 1, "Blocks placed around a held position");
    context.expectBlock(Blocks.AIR, SECOND);
    context.expectBlock(Blocks.OAK_PLANKS, THIRD);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void theSamePositionOfferedTwiceIsPlacedOnce(TestContext context) {
    final List<StructureBlock> placed = place(context, absolute(context, FIRST, FIRST, SECOND), 3);

    context.assertEquals(placed.size(), 2, "Blocks placed for a repeated candidate");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aCandidateTheSourceTurnsDownIsSkippedRatherThanTruncating(TestContext context) {
    final BlockPos declined = context.getAbsolutePos(SECOND);

    final List<StructureBlock> placed =
        StructurePlacer.place(
            context.getWorld(),
            absolute(context, LINE),
            (world, candidate) ->
                candidate.equals(declined) ? null : PLANKS.resolve(world, candidate),
            null,
            StructureBudget.of(LINE.size()),
            NOTHING_HELD);

    context.assertEquals(placed.size(), LINE.size() - 1, "Blocks placed around a declined one");
    context.expectBlock(Blocks.OAK_PLANKS, FOURTH);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aBlockThatCannotStandThereIsSkipped(TestContext context) {
    final List<StructureBlock> placed =
        StructurePlacer.place(
            context.getWorld(),
            absolute(context, new BlockPos(1, 4, 3)),
            (world, candidate) -> new StructureBlock(candidate, Blocks.TORCH.getDefaultState()),
            null,
            StructureBudget.of(1),
            NOTHING_HELD);

    context.assertTrue(placed.isEmpty(), "A torch in mid air has nothing to stand on");
    context.complete();
  }

  private static List<StructureBlock> place(
      final TestContext context, final List<BlockPos> candidates, final int budget) {
    return StructurePlacer.place(
        context.getWorld(), candidates, PLANKS, null, StructureBudget.of(budget), NOTHING_HELD);
  }
}
