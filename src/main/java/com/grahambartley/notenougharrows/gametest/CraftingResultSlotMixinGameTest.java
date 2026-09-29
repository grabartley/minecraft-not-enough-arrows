package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ChaosArrows;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.grahambartley.notenougharrows.social.CourierPayloads;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class CraftingResultSlotMixinGameTest implements FabricGameTest {
  private static final String BATCH = "crafting-result-slot";
  private static final String CAPPED_BATCH = "crafting-result-slot-capped";
  private static final BlockPos TABLE = new BlockPos(3, 2, 3);
  private static final int RESULT = 0;
  private static final int FIRST_GRID_SLOT = 1;
  private static final int SECOND_GRID_SLOT = 2;
  private static final int SMALL_CAP = 16;

  @BeforeBatch(batchId = CAPPED_BATCH)
  public void lowerTheCapBeforeBatch(ServerWorld world) {
    SocialTestSupport.useSocial(
        social -> social.withCourier(social.courier().withMaxPayload(SMALL_CAP)));
  }

  @AfterBatch(batchId = CAPPED_BATCH)
  public void restoreDefaultsAfterBatch(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void takingALoadedArrowUsesUpTheWholeStack(TestContext context) {
    final ServerPlayerEntity player = player(context);
    final CraftingScreenHandler table = openTable(context, player);
    table.getSlot(FIRST_GRID_SLOT).setStack(SocialTestSupport.emptyCourier());
    table.getSlot(SECOND_GRID_SLOT).setStack(new ItemStack(Items.DIAMOND, 40));

    table.onSlotClick(RESULT, 0, SlotActionType.PICKUP, player);

    context.assertTrue(
        table.getSlot(SECOND_GRID_SLOT).getStack().isEmpty(), "Every diamond went into the arrow");
    context.assertEquals(
        40,
        CourierPayloads.payloadOf(table.getCursorStack()).orElseThrow().getCount(),
        "Diamonds in the loaded arrow");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void loadingABucketLeavesNoEmptyBucketBehind(TestContext context) {
    final ServerPlayerEntity player = player(context);
    final CraftingScreenHandler table = openTable(context, player);
    table.getSlot(FIRST_GRID_SLOT).setStack(SocialTestSupport.emptyCourier());
    table.getSlot(SECOND_GRID_SLOT).setStack(new ItemStack(Items.WATER_BUCKET));

    table.onSlotClick(RESULT, 0, SlotActionType.PICKUP, player);

    context.assertTrue(
        CourierPayloads.payloadOf(table.getCursorStack()).orElseThrow().isOf(Items.WATER_BUCKET),
        "The arrow carries the full bucket");
    context.assertTrue(
        table.getSlot(SECOND_GRID_SLOT).getStack().isEmpty(),
        "and no empty bucket is left in the grid, held "
            + table.getSlot(SECOND_GRID_SLOT).getStack());
    context.assertEquals(0, player.getInventory().count(Items.BUCKET), "Empty buckets handed over");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void loadingFromAStackOfArrowsLoadsOnlyOne(TestContext context) {
    final ServerPlayerEntity player = player(context);
    final CraftingScreenHandler table = openTable(context, player);
    table.getSlot(FIRST_GRID_SLOT).setStack(SocialTestSupport.emptyCourier().copyWithCount(8));
    table.getSlot(SECOND_GRID_SLOT).setStack(new ItemStack(Items.DIAMOND, 40));

    table.onSlotClick(RESULT, 0, SlotActionType.PICKUP, player);

    context.assertEquals(1, table.getCursorStack().getCount(), "Loaded arrows taken");
    context.assertEquals(
        7, table.getSlot(FIRST_GRID_SLOT).getStack().getCount(), "Empty arrows left in the grid");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void unloadingLeavesTheEmptyArrowInTheGrid(TestContext context) {
    final ServerPlayerEntity player = player(context);
    final CraftingScreenHandler table = openTable(context, player);
    table.getSlot(FIRST_GRID_SLOT).setStack(SocialTestSupport.loadedCourier());

    table.onSlotClick(RESULT, 0, SlotActionType.PICKUP, player);

    context.assertTrue(
        ItemStack.areEqual(
            table.getCursorStack(),
            new ItemStack(SocialTestSupport.PAYLOAD_ITEM, SocialTestSupport.PAYLOAD_COUNT)),
        "The payload comes back out");
    context.assertTrue(
        CourierPayloads.isEmptyCourier(table.getSlot(FIRST_GRID_SLOT).getStack()),
        "The empty courier arrow is left behind");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void anyOtherRecipeStillTakesOneFromEachSlot(TestContext context) {
    final ServerPlayerEntity player = player(context);
    final CraftingScreenHandler table = openTable(context, player);
    for (int slot = 1; slot <= 9; slot++) {
      table.getSlot(slot).setStack(new ItemStack(Items.ARROW, 2));
    }
    table.getSlot(5).setStack(new ItemStack(Items.EGG, 2));

    table.onSlotClick(RESULT, 0, SlotActionType.PICKUP, player);

    context.assertTrue(
        table.getCursorStack().isOf(ChaosArrows.CHICKEN_ARROW.item()),
        "The chicken arrow recipe still crafts");
    for (int slot = 1; slot <= 9; slot++) {
      context.assertEquals(1, table.getSlot(slot).getStack().getCount(), "Left in slot " + slot);
    }
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = CAPPED_BATCH, tickLimit = 10)
  public void aStackOverTheCapLeavesTheRestInTheGrid(TestContext context) {
    final ServerPlayerEntity player = player(context);
    final CraftingScreenHandler table = openTable(context, player);
    table.getSlot(FIRST_GRID_SLOT).setStack(SocialTestSupport.emptyCourier());
    table.getSlot(SECOND_GRID_SLOT).setStack(new ItemStack(Items.DIAMOND, 40));

    table.onSlotClick(RESULT, 0, SlotActionType.PICKUP, player);

    context.assertEquals(
        SMALL_CAP,
        CourierPayloads.payloadOf(table.getCursorStack()).orElseThrow().getCount(),
        "Diamonds loaded");
    context.assertEquals(
        40 - SMALL_CAP,
        table.getSlot(SECOND_GRID_SLOT).getStack().getCount(),
        "Diamonds left in the grid");
    context.complete();
  }

  private static ServerPlayerEntity player(final TestContext context) {
    final ServerPlayerEntity player = ChaosTestSupport.survivalPlayerAt(context, TABLE.west());
    player.getInventory().clear();
    return player;
  }

  private static CraftingScreenHandler openTable(
      final TestContext context, final ServerPlayerEntity player) {
    context.setBlockState(TABLE, Blocks.CRAFTING_TABLE);
    return new CraftingScreenHandler(
        1,
        player.getInventory(),
        ScreenHandlerContext.create(context.getWorld(), context.getAbsolutePos(TABLE)));
  }
}
