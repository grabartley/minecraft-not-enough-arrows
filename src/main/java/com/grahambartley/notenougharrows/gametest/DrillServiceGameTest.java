package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.DrillArrowConfig;
import com.grahambartley.notenougharrows.terrain.DrillService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

public final class DrillServiceGameTest implements FabricGameTest {
  private static final String BATCH = "drill-service";
  private static final BlockPos STRUCK = TerrainTestSupport.CENTER;
  private static final BlockPos NEIGHBOUR = STRUCK.east();
  private static final DrillArrowConfig IRON =
      new DrillArrowConfig(true, DrillArrowConfig.TOOL_TIER_IRON);

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDrillBreaksTheBlockAndHandsTheShooterItsDrop(TestContext context) {
    context.setBlockState(STRUCK, Blocks.STONE);
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);

    context.assertTrue(bore(context, shooter, IRON), "Stone should be drilled");
    context.expectBlock(Blocks.AIR, STRUCK);
    context.assertEquals(
        shooter.getInventory().count(Items.COBBLESTONE),
        1,
        "Stone should drop cobblestone, as a pickaxe breaking it would");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDispensedDrillDropsWhatItBreaksAtTheBlock(TestContext context) {
    context.setBlockState(STRUCK, Blocks.STONE);

    context.assertTrue(bore(context, null, IRON), "Stone should be drilled");
    context.assertEquals(
        TerrainTestSupport.droppedCount(context, Items.COBBLESTONE), 1, "Cobblestone dropped");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDrillHonoursTheBlocksLootTable(TestContext context) {
    context.setBlockState(STRUCK, Blocks.DIAMOND_ORE);
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);

    bore(context, shooter, IRON);

    context.assertEquals(
        shooter.getInventory().count(Items.DIAMOND), 1, "Diamond ore should drop one diamond");
    context.assertEquals(
        shooter.getInventory().count(Items.DIAMOND_ORE), 0, "The ore block itself is not granted");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDrillBreaksExactlyOneBlock(TestContext context) {
    context.setBlockState(STRUCK, Blocks.STONE);
    context.setBlockState(NEIGHBOUR, Blocks.STONE);

    bore(context, null, IRON);

    context.expectBlock(Blocks.STONE, NEIGHBOUR);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aChestAndEverythingInItIsLeftStanding(TestContext context) {
    context.setBlockState(STRUCK, Blocks.CHEST);
    final ChestBlockEntity chest = context.getBlockEntity(STRUCK);
    chest.setStack(0, new ItemStack(Items.DIAMOND, 5));

    context.assertFalse(bore(context, null, IRON), "A chest should not be drilled");
    context.expectBlock(Blocks.CHEST, STRUCK);
    context.assertEquals(
        ((ChestBlockEntity) context.getBlockEntity(STRUCK)).getStack(0).getCount(),
        5,
        "The chest's contents");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void bedrockIsLeftStanding(TestContext context) {
    assertRefused(context, Blocks.BEDROCK, IRON);
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aBlockAboveTheConfiguredTierIsLeftStanding(TestContext context) {
    assertRefused(context, Blocks.OBSIDIAN, IRON);
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void loweringTheTierLeavesWhatThatPickaxeCouldNotHarvest(TestContext context) {
    assertRefused(
        context, Blocks.DIAMOND_ORE, new DrillArrowConfig(true, DrillArrowConfig.TOOL_TIER_STONE));
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void iceIsLeftStandingRatherThanVanishingWithoutItsWater(TestContext context) {
    assertRefused(context, Blocks.ICE, IRON);
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDisabledDrillBreaksNothing(TestContext context) {
    assertRefused(context, Blocks.STONE, IRON.withEnabled(false));
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aShooterCannotDrillWhereTheyMayNotBuild(TestContext context) {
    context.setBlockState(STRUCK, Blocks.STONE);
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);

    TerrainTestSupport.withTheBorderElsewhere(
        context,
        () -> context.assertFalse(bore(context, shooter, IRON), "Drilled past the world border"));
    context.expectBlock(Blocks.STONE, STRUCK);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDispensedDrillStopsAtTheWorldBorder(TestContext context) {
    context.setBlockState(STRUCK, Blocks.STONE);

    TerrainTestSupport.withTheBorderElsewhere(
        context,
        () -> context.assertFalse(bore(context, null, IRON), "Drilled past the world border"));
    context.expectBlock(Blocks.STONE, STRUCK);
    context.complete();
  }

  private static void assertRefused(
      final TestContext context, final Block block, final DrillArrowConfig drill) {
    context.setBlockState(STRUCK, block);
    context.assertFalse(bore(context, null, drill), block + " should not be drilled");
    context.expectBlock(block, STRUCK);
    context.complete();
  }

  private static boolean bore(
      final TestContext context, final PlayerEntity shooter, final DrillArrowConfig drill) {
    return DrillService.bore(context.getWorld(), context.getAbsolutePos(STRUCK), shooter, drill);
  }
}
