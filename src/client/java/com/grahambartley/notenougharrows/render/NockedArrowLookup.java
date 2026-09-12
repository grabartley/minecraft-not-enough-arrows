package com.grahambartley.notenougharrows.render;

import com.grahambartley.notenougharrows.nock.ChargedCrossbowArrow;
import com.grahambartley.notenougharrows.nock.NockedBowArrow;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class NockedArrowLookup {

  private NockedArrowLookup() {}

  public static Optional<NockedArrow> chargedInto(final ItemStack weapon) {
    final ItemStack arrow = ChargedCrossbowArrow.loadedInto(weapon);
    return arrow.isEmpty()
        ? Optional.empty()
        : Optional.of(new NockedArrow(arrow, NockPlacement.forChargedCrossbow()));
  }

  public static Optional<NockedArrow> drawnOn(
      final ItemStack weapon, @Nullable final LivingEntity holder) {
    if (!NockedBowArrow.isDrawing(weapon, holder)) {
      return Optional.empty();
    }
    final ItemStack arrow = arrowFor(weapon, holder);
    return arrow.isEmpty()
        ? Optional.empty()
        : Optional.of(
            new NockedArrow(
                arrow,
                NockPlacement.forBowPull(
                    weapon.getMaxUseTime(holder), holder.getItemUseTimeLeft())));
  }

  private static ItemStack arrowFor(final ItemStack bow, final LivingEntity holder) {
    return holder == MinecraftClient.getInstance().player
        ? NockedBowArrow.on(bow, holder)
        : NockedArrowSync.drawnBy(holder.getId());
  }
}
