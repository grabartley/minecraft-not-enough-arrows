package com.grahambartley.notenougharrows.ender;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import org.jetbrains.annotations.Nullable;

public final class RecallTargets {

  private RecallTargets() {}

  public static boolean isRecallable(@Nullable final Entity entity) {
    if (entity == null || isBoss(entity)) {
      return false;
    }
    return entity instanceof LivingEntity || entity instanceof VehicleEntity;
  }

  public static boolean carriesAPlayer(@Nullable final Entity entity) {
    if (entity == null) {
      return false;
    }
    if (entity instanceof PlayerEntity) {
      return true;
    }
    for (final Entity passenger : entity.getPassengersDeep()) {
      if (passenger instanceof PlayerEntity) {
        return true;
      }
    }
    return false;
  }

  private static boolean isBoss(final Entity entity) {
    return entity instanceof EnderDragonEntity || entity instanceof WitherEntity;
  }
}
