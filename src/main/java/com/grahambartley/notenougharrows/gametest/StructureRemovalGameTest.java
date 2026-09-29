package com.grahambartley.notenougharrows.gametest;

import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.FIRST;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.LINE;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.SECOND;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.TEMPLATE;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.absolute;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.chunkAt;

import com.grahambartley.notenougharrows.structure.StructureBlock;
import com.grahambartley.notenougharrows.structure.StructureChunkMarks;
import com.grahambartley.notenougharrows.structure.StructureRemoval;
import com.grahambartley.notenougharrows.structure.TimedStructure;
import com.grahambartley.notenougharrows.world.LoadedGround;
import java.util.UUID;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class StructureRemovalGameTest implements FabricGameTest {
  private static final String BATCH = "structure-removal";

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void clearingRemovesAMarkedBlockAndItsMark(TestContext context) {
    final UUID id = UUID.randomUUID();
    placeMarked(context, FIRST, id);

    context.assertTrue(
        StructureRemoval.clear(context.getWorld(), context.getAbsolutePos(FIRST), id),
        "Clearing a marked block should report it");
    context.expectBlock(Blocks.AIR, FIRST);
    context.assertTrue(
        StructureChunkMarks.at(chunkAt(context, FIRST), context.getAbsolutePos(FIRST)).isEmpty(),
        "The mark should go with the block");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void clearingLeavesABlockMarkedByAnotherStructure(TestContext context) {
    placeMarked(context, FIRST, UUID.randomUUID());

    context.assertFalse(
        StructureRemoval.clear(
            context.getWorld(), context.getAbsolutePos(FIRST), UUID.randomUUID()),
        "Another structure's block should not be cleared");
    context.expectBlock(Blocks.OAK_PLANKS, FIRST);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void clearingLeavesAnUnmarkedBlock(TestContext context) {
    context.setBlockState(FIRST, Blocks.OAK_PLANKS);

    context.assertFalse(
        StructureRemoval.clear(
            context.getWorld(), context.getAbsolutePos(FIRST), UUID.randomUUID()),
        "A block no structure placed should not be cleared");
    context.expectBlock(Blocks.OAK_PLANKS, FIRST);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void removalSkipsEveryPositionWhoseChunkIsNotLoaded(TestContext context) {
    final UUID id = UUID.randomUUID();
    LINE.forEach(pos -> placeMarked(context, pos, id));
    final TimedStructure structure = new TimedStructure(id, null, absolute(context, LINE), 0L);

    final int removed = StructureRemoval.remove(context.getWorld(), structure, pos -> false);

    context.assertEquals(removed, 0, "Blocks removed from chunks that are not loaded");
    LINE.forEach(pos -> context.expectBlock(Blocks.OAK_PLANKS, pos));
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void removalClearsEveryLoadedPosition(TestContext context) {
    final UUID id = UUID.randomUUID();
    LINE.forEach(pos -> placeMarked(context, pos, id));
    final TimedStructure structure = new TimedStructure(id, null, absolute(context, LINE), 0L);

    final int removed =
        StructureRemoval.remove(context.getWorld(), structure, LoadedGround.in(context.getWorld()));

    context.assertEquals(removed, LINE.size(), "Blocks removed from loaded chunks");
    LINE.forEach(pos -> context.expectBlock(Blocks.AIR, pos));
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void theTestAreaCountsAsLoaded(TestContext context) {
    context.assertTrue(
        LoadedGround.in(context.getWorld()).test(context.getAbsolutePos(SECOND)),
        "The chunk a test runs in is loaded");
    context.assertFalse(
        LoadedGround.in(context.getWorld()).test(new BlockPos(20_000_000, 64, 20_000_000)),
        "A chunk nobody visited is not loaded, and asking must not load it");
    context.complete();
  }

  private static void placeMarked(final TestContext context, final BlockPos pos, final UUID id) {
    context.setBlockState(pos, Blocks.OAK_PLANKS);
    StructureChunkMarks.mark(
        chunkAt(context, pos),
        new StructureBlock(context.getAbsolutePos(pos), Blocks.OAK_PLANKS.getDefaultState()),
        id);
  }
}
