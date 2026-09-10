package com.grahambartley.morearrows.fletching;

import java.util.Objects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public final class FletchingStationResultSlot extends Slot {
  private final PlayerEntity player;
  private final Runnable onTaken;

  private int crafted;

  public FletchingStationResultSlot(
      final PlayerEntity player,
      final Inventory inventory,
      final int index,
      final int x,
      final int y,
      final Runnable onTaken) {
    super(inventory, index, x, y);
    this.player = Objects.requireNonNull(player, "player");
    this.onTaken = Objects.requireNonNull(onTaken, "onTaken");
  }

  @Override
  public boolean canInsert(final ItemStack stack) {
    return false;
  }

  @Override
  public ItemStack takeStack(final int amount) {
    if (hasStack()) {
      crafted += Math.min(amount, getStack().getCount());
    }
    return super.takeStack(amount);
  }

  @Override
  protected void onCrafted(final ItemStack stack, final int amount) {
    crafted += amount;
    onCrafted(stack);
  }

  @Override
  protected void onCrafted(final ItemStack stack) {
    if (crafted > 0) {
      stack.onCraftByPlayer(player.getWorld(), player, crafted);
    }
    crafted = 0;
  }

  @Override
  public void onTakeItem(final PlayerEntity player, final ItemStack stack) {
    onCrafted(stack);
    onTaken.run();
    super.onTakeItem(player, stack);
  }
}
