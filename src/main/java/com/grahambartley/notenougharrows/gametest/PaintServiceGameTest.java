package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.PaintArrowConfig;
import com.grahambartley.notenougharrows.terrain.PaintService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CandleBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

public final class PaintServiceGameTest implements FabricGameTest {
  private static final String BATCH = "paint-service";
  private static final BlockPos STRUCK = TerrainTestSupport.CENTER;
  private static final BlockPos PASTURE = new BlockPos(3, 3, 3);
  private static final PaintArrowConfig ON = PaintArrowConfig.defaults();
  private static final PaintArrowConfig OFF = new PaintArrowConfig(false);

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void woolTakesTheArrowsColour(TestContext context) {
    assertPainted(context, Blocks.WHITE_WOOL, Blocks.RED_WOOL);
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void carpetTakesTheArrowsColour(TestContext context) {
    assertPainted(context, Blocks.BLUE_CARPET, Blocks.RED_CARPET);
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void undyedGlassIsStained(TestContext context) {
    assertPainted(context, Blocks.GLASS, Blocks.RED_STAINED_GLASS);
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void undyedTerracottaIsDyed(TestContext context) {
    assertPainted(context, Blocks.TERRACOTTA, Blocks.RED_TERRACOTTA);
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aPaintedCandleKeepsItsCandlesAndItsFlame(TestContext context) {
    context.setBlockState(STRUCK.down(), Blocks.STONE);
    context.setBlockState(
        STRUCK,
        Blocks.CANDLE.getDefaultState().with(CandleBlock.CANDLES, 3).with(CandleBlock.LIT, true));

    context.assertTrue(paint(context, null, ON), "The candle should be painted");
    context.checkBlockState(
        STRUCK,
        state ->
            state.isOf(Blocks.RED_CANDLE)
                && state.get(CandleBlock.CANDLES) == 3
                && state.get(CandleBlock.LIT),
        () -> "A painted candle should keep its count and flame");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void concreteIsLeftAlone(TestContext context) {
    assertLeftAlone(context, Blocks.WHITE_CONCRETE, ON);
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void alreadyStainedGlassIsLeftAlone(TestContext context) {
    assertLeftAlone(context, Blocks.BLUE_STAINED_GLASS, ON);
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void airIsLeftAlone(TestContext context) {
    assertLeftAlone(context, Blocks.AIR, ON);
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void woolAlreadyThatColourIsLeftAlone(TestContext context) {
    assertLeftAlone(context, Blocks.RED_WOOL, ON);
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDisabledPaintArrowPaintsNothing(TestContext context) {
    assertLeftAlone(context, Blocks.WHITE_WOOL, OFF);
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aShooterCannotPaintWhereTheyMayNotBuild(TestContext context) {
    context.setBlockState(STRUCK, Blocks.WHITE_WOOL);
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);

    TerrainTestSupport.withTheBorderElsewhere(
        context,
        () -> context.assertFalse(paint(context, shooter, ON), "Painted past the world border"));
    context.expectBlock(Blocks.WHITE_WOOL, STRUCK);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDispensedPaintArrowStopsAtTheWorldBorder(TestContext context) {
    context.setBlockState(STRUCK, Blocks.WHITE_WOOL);

    TerrainTestSupport.withTheBorderElsewhere(
        context,
        () -> context.assertFalse(paint(context, null, ON), "Painted past the world border"));
    context.expectBlock(Blocks.WHITE_WOOL, STRUCK);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aSheepTakesTheArrowsColour(TestContext context) {
    final SheepEntity sheep = sheep(context);

    context.assertTrue(PaintService.paintSheep(sheep, DyeColor.RED, null, ON), "Sheep painted");
    context.assertEquals(sheep.getColor(), DyeColor.RED, "The sheep's colour");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aShearedSheepIsLeftAloneAsVanillaWould(TestContext context) {
    final SheepEntity sheep = sheep(context);
    sheep.setSheared(true);

    context.assertFalse(PaintService.paintSheep(sheep, DyeColor.RED, null, ON), "Painted");
    context.assertEquals(sheep.getColor(), DyeColor.WHITE, "The sheep's colour");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aSheepAlreadyThatColourIsLeftAlone(TestContext context) {
    context.assertFalse(
        PaintService.paintSheep(sheep(context), DyeColor.WHITE, null, ON), "Painted");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDisabledPaintArrowLeavesASheepAlone(TestContext context) {
    final SheepEntity sheep = sheep(context);

    context.assertFalse(PaintService.paintSheep(sheep, DyeColor.RED, null, OFF), "Painted");
    context.assertEquals(sheep.getColor(), DyeColor.WHITE, "The sheep's colour");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aShooterCannotPaintASheepWhereTheyMayNotBuild(TestContext context) {
    final SheepEntity sheep = sheep(context);
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);

    TerrainTestSupport.withTheBorderElsewhere(
        context,
        () ->
            context.assertFalse(
                PaintService.paintSheep(sheep, DyeColor.RED, shooter, ON),
                "Painted past the world border"));
    context.assertEquals(sheep.getColor(), DyeColor.WHITE, "The sheep's colour");
    context.complete();
  }

  private static SheepEntity sheep(final TestContext context) {
    final SheepEntity sheep = context.spawnEntity(EntityType.SHEEP, PASTURE);
    sheep.setAiDisabled(true);
    sheep.setColor(DyeColor.WHITE);
    return sheep;
  }

  private static void assertPainted(final TestContext context, final Block from, final Block to) {
    context.setBlockState(STRUCK, from);
    context.assertTrue(paint(context, null, ON), from + " should be painted");
    context.expectBlock(to, STRUCK);
    context.complete();
  }

  private static void assertLeftAlone(
      final TestContext context, final Block block, final PaintArrowConfig paint) {
    context.setBlockState(STRUCK, block);
    context.assertFalse(paint(context, null, paint), block + " should be left alone");
    context.expectBlock(block, STRUCK);
    context.complete();
  }

  private static boolean paint(
      final TestContext context, final PlayerEntity shooter, final PaintArrowConfig paint) {
    return PaintService.paintBlock(
        context.getWorld(), context.getAbsolutePos(STRUCK), DyeColor.RED, shooter, paint);
  }
}
