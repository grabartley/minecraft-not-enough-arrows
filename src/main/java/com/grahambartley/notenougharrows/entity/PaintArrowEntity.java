package com.grahambartley.notenougharrows.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PaintArrowEntity extends TintedArrowEntity {
  public PaintArrowEntity(
      final EntityType<? extends PaintArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public PaintArrowEntity(
      final EntityType<? extends PaintArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }
}
