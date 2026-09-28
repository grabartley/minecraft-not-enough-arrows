package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.agriculture.BlossomService;
import com.grahambartley.notenougharrows.config.BlossomArrowConfig;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class BlossomServiceGameTest implements FabricGameTest {
  private static final String BATCH = "blossom-service";
  private static final BlockPos CENTER = TerrainTestSupport.CENTER;
  private static final BlockPos NEAR = CENTER.east();
  private static final BlockPos FAR = CENTER.east(3);

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aYoungCropInReachGrows(TestContext context) {
    AgricultureTestSupport.plantWheat(context, CENTER, 0);

    final List<BlockPos> bloomed = bloom(context, 1);

    context.assertEquals(bloomed.size(), 1, "Positions bone mealed");
    context.checkBlockState(
        CENTER,
        state -> ((CropBlock) Blocks.WHEAT).getAge(state) > 0,
        () -> "The wheat should have grown");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aCropOutsideTheRadiusIsLeftAlone(TestContext context) {
    AgricultureTestSupport.plantWheat(context, NEAR, 0);
    AgricultureTestSupport.plantWheat(context, FAR, 0);

    bloom(context, 1);

    context.checkBlockState(
        NEAR, state -> state.get(CropBlock.AGE) > 0, () -> "The wheat in reach should grow");
    context.checkBlockState(
        FAR, state -> state.get(CropBlock.AGE) == 0, () -> "The wheat out of reach should not");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aRipeCropIsNotBoneMealAndReportsNothing(TestContext context) {
    AgricultureTestSupport.plantWheat(context, CENTER, CropBlock.MAX_AGE);

    context.assertTrue(bloom(context, 1).isEmpty(), "Bone meal does nothing to a ripe crop");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void bareStoneGrowsNothing(TestContext context) {
    context.setBlockState(CENTER.down(), Blocks.STONE);

    context.assertTrue(bloom(context, 2).isEmpty(), "Nothing to grow");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void mossSpreadsAsBoneMealWouldSpreadIt(TestContext context) {
    context.setBlockState(CENTER, Blocks.MOSS_BLOCK);
    for (final BlockPos around : List.of(CENTER.east(), CENTER.west(), CENTER.north())) {
      context.setBlockState(around, Blocks.STONE);
    }

    bloom(context, 0);

    final long moss =
        List.of(CENTER.east(), CENTER.west(), CENTER.north()).stream()
            .filter(pos -> context.getBlockState(pos).isOf(Blocks.MOSS_BLOCK))
            .count();
    context.assertTrue(moss > 0, "Bone meal on moss should spread it onto the stone beside it");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void whatOneBoneMealingGrowsIsNotBoneMealedAgainByTheSameShot(TestContext context) {
    context.setBlockState(CENTER, Blocks.MOSS_BLOCK);
    final List<BlockPos> beside =
        List.of(CENTER.east(), CENTER.west(), CENTER.north(), CENTER.south());
    for (final BlockPos around : beside) {
      context.setBlockState(around, Blocks.STONE);
    }

    final List<BlockPos> bloomed = bloom(context, 1);

    context.assertTrue(
        beside.stream().anyMatch(pos -> context.getBlockState(pos).isOf(Blocks.MOSS_BLOCK)),
        "The moss should spread beside it, or this proves nothing");
    context.assertEquals(
        bloomed,
        List.of(context.getAbsolutePos(CENTER)),
        "Only the first moss block was growable when the arrow landed");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aCropWhereTheShooterMayNotBuildIsLeftAlone(TestContext context) {
    AgricultureTestSupport.plantWheat(context, CENTER, 0);

    TerrainTestSupport.withTheBorderElsewhere(
        context,
        () ->
            context.assertTrue(
                BlossomService.bloom(
                        context.getWorld(),
                        context.getAbsolutePos(CENTER),
                        null,
                        new BlossomArrowConfig(1))
                    .isEmpty(),
                "Nothing grows outside the world border"));
    context.checkBlockState(
        CENTER, state -> state.get(CropBlock.AGE) == 0, () -> "The wheat should be untouched");
    context.complete();
  }

  private static List<BlockPos> bloom(final TestContext context, final int radius) {
    return BlossomService.bloom(
        context.getWorld(), context.getAbsolutePos(CENTER), null, new BlossomArrowConfig(radius));
  }
}
