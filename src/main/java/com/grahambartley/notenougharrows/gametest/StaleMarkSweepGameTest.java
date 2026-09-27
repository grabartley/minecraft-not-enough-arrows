package com.grahambartley.notenougharrows.gametest;

import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.FIRST;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.TEMPLATE;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.chunkAt;

import com.grahambartley.notenougharrows.structure.StaleMarkSweep;
import com.grahambartley.notenougharrows.structure.StructureBlock;
import com.grahambartley.notenougharrows.structure.StructureChunkMarks;
import java.util.UUID;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class StaleMarkSweepGameTest implements FabricGameTest {
  private static final String BATCH = "stale-mark-sweep";

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aStaleMarkIsClearedWithItsBlock(TestContext context) {
    final StaleMarkSweep sweep = new StaleMarkSweep();
    final UUID id = placeMarked(context);

    sweep.notice(context.getWorld(), chunkAt(context, FIRST), mark -> !mark.belongsTo(id));

    context.expectBlock(Blocks.OAK_PLANKS, FIRST);
    context.assertTrue(sweep.hasPendingIn(context.getWorld()), "A stale mark should wait");
    context.assertEquals(sweep.clearIn(context.getWorld()), 1, "Stale blocks cleared");
    context.expectBlock(Blocks.AIR, FIRST);
    context.assertFalse(sweep.hasPendingIn(context.getWorld()), "Nothing should be left pending");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aLiveMarkIsLeftStanding(TestContext context) {
    final StaleMarkSweep sweep = new StaleMarkSweep();
    placeMarked(context);

    sweep.notice(context.getWorld(), chunkAt(context, FIRST), mark -> true);

    context.assertFalse(sweep.hasPendingIn(context.getWorld()), "A live mark is not stale");
    context.assertEquals(sweep.clearIn(context.getWorld()), 0, "Live blocks cleared");
    context.expectBlock(Blocks.OAK_PLANKS, FIRST);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aStaleMarkWhoseBlockWasReplacedLeavesTheNewBlock(TestContext context) {
    final StaleMarkSweep sweep = new StaleMarkSweep();
    final UUID id = placeMarked(context);
    sweep.notice(context.getWorld(), chunkAt(context, FIRST), mark -> !mark.belongsTo(id));

    context.setBlockState(FIRST, Blocks.STONE);
    sweep.clearIn(context.getWorld());

    context.expectBlock(Blocks.STONE, FIRST);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void forgettingDropsEveryPendingMark(TestContext context) {
    final StaleMarkSweep sweep = new StaleMarkSweep();
    final UUID id = placeMarked(context);
    sweep.notice(context.getWorld(), chunkAt(context, FIRST), mark -> !mark.belongsTo(id));

    sweep.forget();

    context.assertFalse(sweep.hasPendingIn(context.getWorld()), "Nothing should be pending");
    context.assertEquals(sweep.clearIn(context.getWorld()), 0, "Blocks cleared after forgetting");
    context.expectBlock(Blocks.OAK_PLANKS, FIRST);
    StructureChunkMarks.unmark(chunkAt(context, FIRST), context.getAbsolutePos(FIRST));
    context.complete();
  }

  private static UUID placeMarked(final TestContext context) {
    final UUID id = UUID.randomUUID();
    context.setBlockState(FIRST, Blocks.OAK_PLANKS);
    StructureChunkMarks.mark(
        chunkAt(context, FIRST),
        new StructureBlock(context.getAbsolutePos(FIRST), Blocks.OAK_PLANKS.getDefaultState()),
        id);
    return id;
  }
}
