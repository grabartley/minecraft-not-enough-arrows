package com.grahambartley.notenougharrows.gametest;

import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.FIRST;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.LONG_LIFETIME_TICKS;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.TEMPLATE;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.absolute;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.chunkAt;

import com.grahambartley.notenougharrows.structure.StructureBlockSource;
import com.grahambartley.notenougharrows.structure.StructureBudget;
import com.grahambartley.notenougharrows.structure.StructureChunkMarks;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class WorldChunkStructureMixinGameTest implements FabricGameTest {
  private static final String BATCH = "world-chunk-structure-mixin";

  @BeforeBatch(batchId = BATCH)
  public void forgetStructuresBeforeBatch(ServerWorld world) {
    TimedStructureService.forget();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void theSameBlockInANewStateKeepsItsMark(TestContext context) {
    buildSnowAt(context);

    context.setBlockState(FIRST, Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 3));

    context.assertTrue(isMarked(context), "A snow layer that grew is still the snow placed");
    context.assertTrue(isHeld(context), "The structure should still hold the position");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aDifferentBlockDropsTheMarkAndReleasesThePosition(TestContext context) {
    buildSnowAt(context);

    context.setBlockState(FIRST, Blocks.STONE);

    context.assertFalse(isMarked(context), "Stone is not what the structure placed");
    context.assertFalse(isHeld(context), "The structure should let go of a replaced position");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aBlockNoStructurePlacedIsIgnored(TestContext context) {
    context.setBlockState(FIRST, Blocks.STONE);

    context.assertFalse(isMarked(context), "An unmarked position should stay unmarked");
    context.complete();
  }

  private static void buildSnowAt(final TestContext context) {
    TimedStructureService.build(
            context.getWorld(),
            null,
            absolute(context, FIRST),
            StructureBlockSource.of(Blocks.SNOW.getDefaultState()),
            StructureBudget.of(1),
            LONG_LIFETIME_TICKS)
        .orElseThrow();
  }

  private static boolean isMarked(final TestContext context) {
    final BlockPos pos = context.getAbsolutePos(FIRST);
    return StructureChunkMarks.at(chunkAt(context, FIRST), pos).isPresent();
  }

  private static boolean isHeld(final TestContext context) {
    return TimedStructureService.holds(context.getWorld(), context.getAbsolutePos(FIRST));
  }
}
