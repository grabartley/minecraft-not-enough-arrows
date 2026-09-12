package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.PhysicsArrowConfig;
import com.grahambartley.notenougharrows.gravity.GravityService;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class GravityServiceGameTest implements FabricGameTest {
  private static final String BATCH = "gravity-service";
  private static final String TEMPLATE = "not-enough-arrows:fire_pad";
  private static final BlockPos STRUCK = new BlockPos(3, 4, 3);
  private static final BlockPos NEIGHBOUR = new BlockPos(4, 4, 3);
  private static final String STONE_ID = "minecraft:stone";
  private static final String OBSIDIAN_ID = "minecraft:obsidian";

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void theDefaultRadiusDropsOnlyTheBlockThatWasHit(TestContext context) {
    context.setBlockState(STRUCK, Blocks.STONE);
    context.setBlockState(NEIGHBOUR, Blocks.STONE);

    final List<BlockPos> fallen = collapse(context, PhysicsArrowTestSupport.physics(0, List.of()));

    context.assertEquals(fallen.size(), 1, "Blocks dropped at the default radius");
    context.expectBlock(Blocks.AIR, STRUCK);
    context.expectBlock(Blocks.STONE, NEIGHBOUR);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void raisingTheRadiusDropsTheBlocksAroundItToo(TestContext context) {
    context.setBlockState(STRUCK, Blocks.STONE);
    context.setBlockState(NEIGHBOUR, Blocks.STONE);

    collapse(context, PhysicsArrowTestSupport.physics(1, List.of()));

    context.expectBlock(Blocks.AIR, STRUCK);
    context.expectBlock(Blocks.AIR, NEIGHBOUR);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anExcludedBlockNeverFalls(TestContext context) {
    context.setBlockState(STRUCK, Blocks.STONE);

    final List<BlockPos> fallen =
        collapse(context, PhysicsArrowTestSupport.physics(0, List.of(STONE_ID)));

    context.assertTrue(fallen.isEmpty(), "An excluded block should never fall");
    context.expectBlock(Blocks.STONE, STRUCK);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anExcludedBlockIsSparedWhileItsNeighboursFall(TestContext context) {
    context.setBlockState(STRUCK, Blocks.STONE);
    context.setBlockState(NEIGHBOUR, Blocks.OBSIDIAN);

    collapse(context, PhysicsArrowTestSupport.physics(1, List.of(OBSIDIAN_ID)));

    context.expectBlock(Blocks.AIR, STRUCK);
    context.expectBlock(Blocks.OBSIDIAN, NEIGHBOUR);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anUnbreakableBlockIsNeverMoved(TestContext context) {
    context.setBlockState(STRUCK, Blocks.BEDROCK);

    final List<BlockPos> fallen = collapse(context, PhysicsArrowTestSupport.physics(1, List.of()));

    context.assertTrue(fallen.isEmpty(), "A block no player could break should never fall");
    context.expectBlock(Blocks.BEDROCK, STRUCK);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anEmptySpaceHasNothingToDrop(TestContext context) {
    final List<BlockPos> fallen = collapse(context, PhysicsArrowTestSupport.physics(1, List.of()));

    context.assertTrue(fallen.isEmpty(), "Open air should leave nothing to drop");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void nothingFallsWithoutABlockToHit(TestContext context) {
    context.assertTrue(
        GravityService.collapse(
                context.getWorld(), null, null, PhysicsArrowTestSupport.physics(1, List.of()))
            .isEmpty(),
        "A collapse with nowhere to start should drop nothing");
    context.assertTrue(
        GravityService.collapse(context.getWorld(), context.getAbsolutePos(STRUCK), null, null)
            .isEmpty(),
        "A collapse with no settings behind it should drop nothing");
    context.complete();
  }

  private static List<BlockPos> collapse(
      final TestContext context, final PhysicsArrowConfig physics) {
    return GravityService.collapse(
        context.getWorld(), context.getAbsolutePos(STRUCK), null, physics);
  }
}
