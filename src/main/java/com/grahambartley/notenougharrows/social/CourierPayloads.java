package com.grahambartley.notenougharrows.social;

import com.grahambartley.notenougharrows.ModDataComponents;
import com.grahambartley.notenougharrows.item.CourierArrowItem;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;

public final class CourierPayloads {

  private CourierPayloads() {}

  public static boolean isCourier(final ItemStack stack) {
    return stack != null && stack.getItem() instanceof CourierArrowItem;
  }

  public static boolean isLoaded(final ItemStack stack) {
    return isCourier(stack) && stack.contains(ModDataComponents.COURIER_PAYLOAD);
  }

  public static boolean isEmptyCourier(final ItemStack stack) {
    return isCourier(stack) && !stack.contains(ModDataComponents.COURIER_PAYLOAD);
  }

  public static Optional<ItemStack> payloadOf(final ItemStack arrow) {
    if (!isCourier(arrow)) {
      return Optional.empty();
    }
    return Optional.ofNullable(arrow.get(ModDataComponents.COURIER_PAYLOAD))
        .map(CourierPayload::stack);
  }

  public static ItemStack loaded(final ItemStack arrow, final ItemStack payload) {
    requireCourier(arrow);
    final ItemStack loaded = arrow.copyWithCount(1);
    loaded.set(ModDataComponents.COURIER_PAYLOAD, new CourierPayload(payload));
    return loaded;
  }

  public static ItemStack emptied(final ItemStack arrow) {
    requireCourier(arrow);
    final ItemStack emptied = arrow.copyWithCount(1);
    emptied.remove(ModDataComponents.COURIER_PAYLOAD);
    return emptied;
  }

  public static ItemStack asFired(final ItemStack arrow, final boolean creativeShooter) {
    final boolean copyOfAnUnspentArrow = arrow.contains(DataComponentTypes.INTANGIBLE_PROJECTILE);
    return copyOfAnUnspentArrow && !creativeShooter && isLoaded(arrow) ? emptied(arrow) : arrow;
  }

  private static void requireCourier(final ItemStack arrow) {
    Objects.requireNonNull(arrow, "arrow");
    if (!isCourier(arrow)) {
      throw new IllegalArgumentException("Not a courier arrow: " + arrow);
    }
  }
}
