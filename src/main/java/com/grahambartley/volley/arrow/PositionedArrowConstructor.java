package com.grahambartley.volley.arrow;

import com.grahambartley.volley.entity.BaseArrowEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface PositionedArrowConstructor<E extends BaseArrowEntity> {
  E create(
      EntityType<E> entityType,
      World world,
      double x,
      double y,
      double z,
      ItemStack stack,
      @Nullable ItemStack weapon);

  default ArrowEntityFactory boundTo(final EntityType<E> entityType) {
    return (world, x, y, z, stack, weapon) -> create(entityType, world, x, y, z, stack, weapon);
  }
}
