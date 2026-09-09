package com.grahambartley.morearrows.render;

import com.grahambartley.morearrows.nock.NockedBowArrow;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class NockedArrowLookup {

  private NockedArrowLookup() {}

  public static Optional<NockedArrow> on(
      final ItemStack weapon, @Nullable final LivingEntity holder) {
    if (weapon.getItem() instanceof CrossbowItem) {
      return chargedInto(weapon);
    }
    if (weapon.getItem() instanceof BowItem) {
      return drawnOn(weapon, holder);
    }
    return Optional.empty();
  }

  private static Optional<NockedArrow> chargedInto(final ItemStack crossbow) {
    final ChargedProjectilesComponent charged =
        crossbow.get(DataComponentTypes.CHARGED_PROJECTILES);
    if (charged == null || charged.isEmpty()) {
      return Optional.empty();
    }
    return modArrow(charged.getProjectiles().getFirst())
        .map(arrow -> new NockedArrow(arrow, NockPlacement.forChargedCrossbow()));
  }

  private static Optional<NockedArrow> drawnOn(
      final ItemStack bow, @Nullable final LivingEntity holder) {
    if (holder == null || !holder.isUsingItem() || holder.getActiveItem() != bow) {
      return Optional.empty();
    }
    final ItemStack arrow = arrowFor(bow, holder);
    if (arrow.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(
        new NockedArrow(
            arrow,
            NockPlacement.forBowPull(bow.getMaxUseTime(holder), holder.getItemUseTimeLeft())));
  }

  private static ItemStack arrowFor(final ItemStack bow, final LivingEntity holder) {
    return holder == MinecraftClient.getInstance().player
        ? NockedBowArrow.on(bow, holder)
        : NockedArrowSync.drawnBy(holder.getId());
  }

  private static Optional<ItemStack> modArrow(final ItemStack stack) {
    return NockedBowArrow.isModArrow(stack) ? Optional.of(stack) : Optional.empty();
  }
}
