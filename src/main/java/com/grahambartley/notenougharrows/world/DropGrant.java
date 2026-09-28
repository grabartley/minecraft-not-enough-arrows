package com.grahambartley.notenougharrows.world;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class DropGrant {

  private DropGrant() {}

  public static void grant(
      final ServerWorld world,
      final BlockPos pos,
      final List<ItemStack> drops,
      @Nullable final PlayerEntity receiver) {
    for (final ItemStack drop : drops) {
      final ItemStack remainder = drop.copy();
      if (receiver != null && receiver.isAlive()) {
        receiver.getInventory().insertStack(remainder);
      }
      if (!remainder.isEmpty()) {
        Block.dropStack(world, pos, remainder);
      }
    }
  }

  public static void collect(final List<ItemEntity> drops, @Nullable final PlayerEntity receiver) {
    if (receiver == null || !receiver.isAlive()) {
      return;
    }
    for (final ItemEntity drop : drops) {
      if (!drop.isAlive()) {
        continue;
      }
      final ItemStack remainder = drop.getStack().copy();
      receiver.getInventory().insertStack(remainder);
      if (remainder.isEmpty()) {
        drop.discard();
      } else {
        drop.setStack(remainder);
      }
    }
  }
}
