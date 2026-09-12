package com.grahambartley.notenougharrows.grapple;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public final class GrappleArrowReturn {

  private GrappleArrowReturn() {}

  public static boolean toShooter(
      @Nullable final ServerPlayerEntity player, @Nullable final Entity arrow) {
    if (player == null
        || !(arrow instanceof PersistentProjectileEntity planted)
        || planted.isRemoved()
        || planted.pickupType != PersistentProjectileEntity.PickupPermission.ALLOWED) {
      return false;
    }

    final ItemStack recovered = planted.getItemStack().copy();
    planted.discard();
    if (!player.getInventory().insertStack(recovered)) {
      player.dropItem(recovered, false);
    }
    return true;
  }
}
