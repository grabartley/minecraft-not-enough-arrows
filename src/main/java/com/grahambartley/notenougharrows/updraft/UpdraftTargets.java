package com.grahambartley.notenougharrows.updraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

public final class UpdraftTargets {

  private UpdraftTargets() {}

  public static boolean canBeLifted(@Nullable final Entity entity) {
    if (entity == null
        || entity.isRemoved()
        || entity.isSpectator()
        || entity.hasVehicle()
        || entity.hasNoGravity()
        || entity instanceof AbstractDecorationEntity) {
      return false;
    }
    return !(entity instanceof PlayerEntity player && player.getAbilities().flying);
  }
}
