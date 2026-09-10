package com.grahambartley.morearrows.fletching;

import java.util.Objects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public final class FletchingStationResultSlot extends Slot {
  private final Runnable onTaken;

  public FletchingStationResultSlot(
      final Inventory inventory,
      final int index,
      final int x,
      final int y,
      final Runnable onTaken) {
    super(inventory, index, x, y);
    this.onTaken = Objects.requireNonNull(onTaken, "onTaken");
  }

  @Override
  public boolean canInsert(final ItemStack stack) {
    return false;
  }

  @Override
  public void onTakeItem(final PlayerEntity player, final ItemStack stack) {
    stack.onCraftByPlayer(player.getWorld(), player, stack.getCount());
    onTaken.run();
    super.onTakeItem(player, stack);
  }
}
