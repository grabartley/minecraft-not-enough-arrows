package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.agriculture.TillService;
import com.grahambartley.notenougharrows.config.TillArrowConfig;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class TillServiceGameTest implements FabricGameTest {
  private static final String BATCH = "till-service";
  private static final BlockPos CENTER = TerrainTestSupport.CENTER;

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void grassDirtAndPathBecomeWetFarmland(TestContext context) {
    context.setBlockState(CENTER, Blocks.GRASS_BLOCK);
    context.setBlockState(CENTER.east(), Blocks.DIRT);
    context.setBlockState(CENTER.west(), Blocks.DIRT_PATH);

    final List<BlockPos> tilled = till(context, 1);

    context.assertEquals(tilled.size(), 3, "Blocks tilled");
    for (final BlockPos pos : List.of(CENTER, CENTER.east(), CENTER.west())) {
      assertWetFarmland(context, pos);
    }
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void groundWithSomethingOnTopIsLeftAlone(TestContext context) {
    context.setBlockState(CENTER, Blocks.GRASS_BLOCK);
    context.setBlockState(CENTER.up(), Blocks.SHORT_GRASS);

    context.assertTrue(till(context, 0).isEmpty(), "A hoe will not till under a plant");
    context.expectBlock(Blocks.GRASS_BLOCK, CENTER);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void whatAHoeCannotTillIsLeftAlone(TestContext context) {
    for (final Block untillable :
        List.of(Blocks.STONE, Blocks.COARSE_DIRT, Blocks.ROOTED_DIRT, Blocks.PODZOL, Blocks.SAND)) {
      context.setBlockState(CENTER, untillable);
      context.assertTrue(till(context, 0).isEmpty(), untillable + " should not become farmland");
      context.expectBlock(untillable, CENTER);
    }
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void dryFarmlandIsSoakedEvenUnderACrop(TestContext context) {
    context.setBlockState(
        CENTER, Blocks.FARMLAND.getDefaultState().with(FarmlandBlock.MOISTURE, 0));
    context.setBlockState(CENTER.up(), Blocks.WHEAT);

    context.assertEquals(till(context, 0).size(), 1, "Farmland soaked");
    assertWetFarmland(context, CENTER);
    context.expectBlock(Blocks.WHEAT, CENTER.up());
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void theDiscStaysOnTheStruckLayer(TestContext context) {
    context.setBlockState(CENTER, Blocks.DIRT);
    context.setBlockState(CENTER.down().east(), Blocks.DIRT);

    till(context, 2);

    assertWetFarmland(context, CENTER);
    context.expectBlock(Blocks.DIRT, CENTER.down().east());
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void groundWhereTheShooterMayNotBuildIsLeftAlone(TestContext context) {
    context.setBlockState(CENTER, Blocks.DIRT);

    TerrainTestSupport.withTheBorderElsewhere(
        context, () -> context.assertTrue(till(context, 1).isEmpty(), "Nothing tilled"));
    context.expectBlock(Blocks.DIRT, CENTER);
    context.complete();
  }

  private static void assertWetFarmland(final TestContext context, final BlockPos pos) {
    context.checkBlockState(
        pos,
        state ->
            state.isOf(Blocks.FARMLAND)
                && state.get(FarmlandBlock.MOISTURE) == FarmlandBlock.MAX_MOISTURE,
        () -> pos + " should be wet farmland");
  }

  private static List<BlockPos> till(final TestContext context, final int radius) {
    return TillService.till(
        context.getWorld(), context.getAbsolutePos(CENTER), null, new TillArrowConfig(radius));
  }
}
