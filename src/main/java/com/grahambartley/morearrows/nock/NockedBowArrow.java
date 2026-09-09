package com.grahambartley.morearrows.nock;

import com.grahambartley.morearrows.item.BaseArrowItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class NockedBowArrow {

  private NockedBowArrow() {}

  public static ItemStack drawnBy(@Nullable final LivingEntity holder) {
    if (holder == null) {
      return ItemStack.EMPTY;
    }
    final ItemStack active = holder.getActiveItem();
    return active.getItem() instanceof BowItem ? on(active, holder) : ItemStack.EMPTY;
  }

  public static ItemStack on(final ItemStack bow, @Nullable final LivingEntity holder) {
    if (holder == null || !holder.isUsingItem() || holder.getActiveItem() != bow) {
      return ItemStack.EMPTY;
    }
    final ItemStack arrow = holder.getProjectileType(bow);
    return isModArrow(arrow) ? arrow : ItemStack.EMPTY;
  }

  public static boolean isModArrow(final ItemStack stack) {
    return stack.getItem() instanceof BaseArrowItem;
  }
}
