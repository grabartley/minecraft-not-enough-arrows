package com.grahambartley.notenougharrows.social;

import com.grahambartley.notenougharrows.config.CourierArrowConfig;
import java.util.Objects;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public final class CourierRefusals {

  private CourierRefusals() {}

  public static boolean refusesToCarry(final ItemStack payload, final CourierArrowConfig config) {
    Objects.requireNonNull(config, "config");
    return payload == null
        || payload.isEmpty()
        || holdsACourierArrow(payload)
        || config.isUndeliverable(Registries.ITEM.getId(payload.getItem()).toString());
  }

  public static boolean holdsACourierArrow(final ItemStack stack) {
    if (CourierPayloads.isCourier(stack)) {
      return true;
    }
    final ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
    if (container != null && container.stream().anyMatch(CourierRefusals::holdsACourierArrow)) {
      return true;
    }
    final BundleContentsComponent bundle = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
    return bundle != null && bundle.stream().anyMatch(CourierRefusals::holdsACourierArrow);
  }

  public static boolean refusesToDeliver(final ItemStack payload, final CourierArrowConfig config) {
    return refusesToCarry(payload, config) || config.exceedsPayload(payload.getCount());
  }

  public static int loadableCount(final ItemStack payload, final CourierArrowConfig config) {
    Objects.requireNonNull(config, "config");
    return refusesToCarry(payload, config) ? 0 : Math.min(payload.getCount(), config.maxPayload());
  }
}
