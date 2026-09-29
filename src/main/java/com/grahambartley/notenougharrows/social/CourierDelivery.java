package com.grahambartley.notenougharrows.social;

import java.util.Objects;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public final class CourierDelivery {
  private static final int NO_SLOT = -1;

  private CourierDelivery() {}

  public static void handTo(final PlayerEntity recipient, final ItemStack stack) {
    Objects.requireNonNull(recipient, "recipient");
    final ItemStack remaining = grant(recipient.getInventory(), stack.copy());
    if (!remaining.isEmpty() && recipient.getWorld() instanceof ServerWorld world) {
      drop(world, recipient.getPos(), remaining);
    }
  }

  public static ItemStack grant(final PlayerInventory inventory, final ItemStack stack) {
    final ItemStack remaining = stack.copy();
    while (!remaining.isEmpty()) {
      int slot = inventory.getOccupiedSlotWithRoomForStack(remaining);
      if (slot == NO_SLOT) {
        slot = inventory.getEmptySlot();
      }
      if (slot == NO_SLOT) {
        break;
      }
      final ItemStack held = inventory.getStack(slot);
      final int room = inventory.getMaxCount(remaining) - held.getCount();
      if (held.isEmpty()) {
        inventory.setStack(slot, remaining.split(room));
      } else {
        held.increment(remaining.split(room).getCount());
      }
    }
    inventory.markDirty();
    return remaining;
  }

  public static void drop(final ServerWorld world, final Vec3d at, final ItemStack stack) {
    Objects.requireNonNull(world, "world");
    Objects.requireNonNull(at, "at");
    if (stack.isEmpty()) {
      return;
    }
    final ItemEntity dropped = new ItemEntity(world, at.x, at.y, at.z, stack.copy(), 0, 0, 0);
    dropped.setToDefaultPickupDelay();
    world.spawnEntity(dropped);
  }
}
