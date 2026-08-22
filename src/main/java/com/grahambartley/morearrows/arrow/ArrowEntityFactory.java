package com.grahambartley.morearrows.arrow;

import com.grahambartley.morearrows.entity.BaseArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ArrowEntityFactory {
  BaseArrowEntity create(
      World world, double x, double y, double z, ItemStack stack, @Nullable ItemStack weapon);
}
