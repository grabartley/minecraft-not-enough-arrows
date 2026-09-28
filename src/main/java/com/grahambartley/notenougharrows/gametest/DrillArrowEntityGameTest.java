package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.entity.DrillArrowEntity;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class DrillArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "drill-arrow";

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowFromABowDrillsTheBlockAndHandsTheShooterItsDrop(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    final ServerPlayerEntity shooter =
        TerrainArrowTestSupport.fireFromBow(context, ModArrows.DRILL_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.AIR, FiringRangeSupport.BACKSTOP);
          context.assertEquals(
              shooter.getInventory().count(Items.COBBLESTONE), 1, "Cobblestone granted");
          context.assertTrue(FiringRangeSupport.arrowWasSpent(context), "The arrow is spent");
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aDispensedArrowDrillsTheBlockAndDropsItsLoot(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    FiringRangeSupport.dispenseEast(
        context, TerrainArrowTestSupport.DISPENSER_STAND, ModArrows.DRILL_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.AIR, FiringRangeSupport.BACKSTOP);
          context.assertEquals(
              TerrainTestSupport.droppedCount(context, Items.COBBLESTONE),
              1,
              "Cobblestone dropped");
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowEmbedsInBedrockAndStaysRecoverable(TestContext context) {
    assertEmbedsIn(context, Blocks.BEDROCK);
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowEmbedsInAChestAndStaysRecoverable(TestContext context) {
    assertEmbedsIn(context, Blocks.CHEST);
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowEmbedsInABlockAboveItsTierAndStaysRecoverable(TestContext context) {
    assertEmbedsIn(context, Blocks.OBSIDIAN);
  }

  private static void assertEmbedsIn(final TestContext context, final Block block) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, block);
    TerrainArrowTestSupport.fireFromBow(context, ModArrows.DRILL_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(block, FiringRangeSupport.BACKSTOP);
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, DrillArrowEntity.class) != null,
              "An arrow that broke nothing should embed and stay recoverable");
          context.complete();
        });
  }
}
