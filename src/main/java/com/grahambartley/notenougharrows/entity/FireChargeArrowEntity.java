package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.explosive.ExplosiveTier;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FireChargeArrowEntity extends ExplosiveArrowEntity {

  public FireChargeArrowEntity(
      final EntityType<? extends FireChargeArrowEntity> entityType, final World world) {
    super(entityType, world, ExplosiveTier.FIRE_CHARGE);
  }

  public FireChargeArrowEntity(
      final EntityType<? extends FireChargeArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon, ExplosiveTier.FIRE_CHARGE);
  }
}
