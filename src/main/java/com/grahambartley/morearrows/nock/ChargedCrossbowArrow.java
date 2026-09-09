package com.grahambartley.morearrows.nock;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;

public final class ChargedCrossbowArrow {

  private ChargedCrossbowArrow() {}

  public static ItemStack loadedInto(final ItemStack crossbow) {
    if (!(crossbow.getItem() instanceof CrossbowItem)) {
      return ItemStack.EMPTY;
    }
    final ChargedProjectilesComponent charged =
        crossbow.get(DataComponentTypes.CHARGED_PROJECTILES);
    if (charged == null || charged.isEmpty()) {
      return ItemStack.EMPTY;
    }
    final ItemStack arrow = charged.getProjectiles().getFirst();
    return NockedBowArrow.isModArrow(arrow) ? arrow : ItemStack.EMPTY;
  }
}
