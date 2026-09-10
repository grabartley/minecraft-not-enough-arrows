package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.fletching.FletchingStationScreenHandler;
import com.grahambartley.morearrows.fletching.FletchingStationSlots;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

final class FletchingStationSupport {
  static final BlockPos TABLE = new BlockPos(3, 2, 3);
  static final int FIRST_SYNC_ID = 1;

  private FletchingStationSupport() {}

  static FletchingStationScreenHandler openStation(
      final TestContext context, final ServerPlayerEntity player) {
    context.setBlockState(TABLE, Blocks.FLETCHING_TABLE);
    return new FletchingStationScreenHandler(
        FIRST_SYNC_ID,
        player.getInventory(),
        ScreenHandlerContext.create(context.getWorld(), context.getAbsolutePos(TABLE)));
  }

  static void loadInputs(final FletchingStationScreenHandler station) {
    station.getSlot(0).setStack(new ItemStack(Items.ARROW, FletchingTestSupport.ARROWS_CONSUMED));
    station.getSlot(1).setStack(new ItemStack(Items.TNT, FletchingTestSupport.TNT_CONSUMED));
  }

  static void shiftClick(
      final FletchingStationScreenHandler station,
      final ServerPlayerEntity player,
      final int slot) {
    station.onSlotClick(slot, 0, SlotActionType.QUICK_MOVE, player);
  }

  static int countHeld(final ServerPlayerEntity player, final Item item) {
    int total = 0;
    for (int slot = 0; slot < player.getInventory().size(); slot++) {
      final ItemStack stack = player.getInventory().getStack(slot);
      if (stack.isOf(item)) {
        total += stack.getCount();
      }
    }
    return total;
  }

  static int countInInputs(final FletchingStationScreenHandler station, final Item item) {
    int total = 0;
    for (int slot = 0; slot < FletchingStationSlots.INPUT_COUNT; slot++) {
      final ItemStack stack = station.getSlot(slot).getStack();
      if (stack.isOf(item)) {
        total += stack.getCount();
      }
    }
    return total;
  }

  static ItemStack result(final FletchingStationScreenHandler station) {
    return station.getSlot(FletchingStationSlots.RESULT_SLOT).getStack();
  }
}
