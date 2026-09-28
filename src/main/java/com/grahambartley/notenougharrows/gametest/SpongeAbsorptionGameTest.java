package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.terrain.SpongeAbsorption;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class SpongeAbsorptionGameTest implements FabricGameTest {
  private static final String BATCH = "sponge-absorption";
  private static final BlockPos AT = TerrainTestSupport.CENTER;

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void stillWaterIsSoakedUp(TestContext context) {
    assertAbsorbed(context, Blocks.WATER.getDefaultState(), true);
    context.expectBlock(Blocks.AIR, AT);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void flowingWaterIsSoakedUp(TestContext context) {
    assertAbsorbed(context, Blocks.WATER.getDefaultState().with(FluidBlock.LEVEL, 3), true);
    context.expectBlock(Blocks.AIR, AT);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aBlockHoldingWaterIsDriedOutAndKept(TestContext context) {
    assertAbsorbed(
        context, Blocks.OAK_FENCE.getDefaultState().with(Properties.WATERLOGGED, true), true);
    context.checkBlockState(
        AT,
        state -> state.isOf(Blocks.OAK_FENCE) && !state.get(Properties.WATERLOGGED),
        () -> "The fence should stay, dried out");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void seagrassGoesWithTheWater(TestContext context) {
    assertAbsorbed(context, Blocks.SEAGRASS.getDefaultState(), true);
    context.expectBlock(Blocks.AIR, AT);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void kelpGoesWithTheWater(TestContext context) {
    context.setBlockState(AT.down(), Blocks.SAND);
    assertAbsorbed(context, Blocks.KELP.getDefaultState(), true);
    context.expectBlock(Blocks.AIR, AT);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void lavaIsNeverSoakedUp(TestContext context) {
    assertAbsorbed(context, Blocks.LAVA.getDefaultState(), false);
    context.expectBlock(Blocks.LAVA, AT);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDryBlockIsLeftAlone(TestContext context) {
    assertAbsorbed(context, Blocks.STONE.getDefaultState(), false);
    context.expectBlock(Blocks.STONE, AT);
    context.complete();
  }

  private static void assertAbsorbed(
      final TestContext context, final BlockState state, final boolean absorbed) {
    context.setBlockState(AT, state);
    context.assertTrue(
        SpongeAbsorption.absorbAt(context.getWorld(), context.getAbsolutePos(AT)) == absorbed,
        state + " absorbed should be " + absorbed);
  }
}
