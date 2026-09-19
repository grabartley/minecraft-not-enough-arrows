package com.grahambartley.notenougharrows.control;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;

public final class DisarmDrop {

  private DisarmDrop() {}

  public static boolean reaches(final LivingEntity target, final boolean affectsPlayers) {
    return target != null && (affectsPlayers || !(target instanceof PlayerEntity));
  }

  public static boolean disarm(
      final ServerWorld world, final LivingEntity target, final boolean affectsPlayers) {
    if (world == null || !reaches(target, affectsPlayers)) {
      return false;
    }

    final ItemStack held = target.getStackInHand(Hand.MAIN_HAND);
    if (held.isEmpty()) {
      return false;
    }

    final ItemEntity dropped =
        new ItemEntity(world, target.getX(), target.getY(), target.getZ(), held.copy());
    dropped.setToDefaultPickupDelay();
    if (!world.spawnEntity(dropped)) {
      return false;
    }
    target.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
    return true;
  }
}
