package com.grahambartley.notenougharrows.nock;

import com.grahambartley.notenougharrows.item.BaseArrowItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class NockedBowArrow {

  private NockedBowArrow() {}

  public static ItemStack drawnBy(@Nullable final LivingEntity holder) {
    return holder == null ? ItemStack.EMPTY : on(holder.getActiveItem(), holder);
  }

  public static ItemStack on(final ItemStack bow, @Nullable final LivingEntity holder) {
    if (!isDrawing(bow, holder)) {
      return ItemStack.EMPTY;
    }
    final ItemStack arrow = holder.getProjectileType(bow);
    return BaseArrowItem.isModArrow(arrow) ? arrow : ItemStack.EMPTY;
  }

  public static boolean isDrawing(final ItemStack bow, @Nullable final LivingEntity holder) {
    return bow.getItem() instanceof BowItem
        && holder != null
        && holder.isUsingItem()
        && holder.getActiveItem() == bow;
  }
}
