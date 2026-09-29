package com.grahambartley.notenougharrows.social;

import com.grahambartley.notenougharrows.config.CourierArrowConfig;
import java.util.Objects;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public final class CourierRefusals {

  private CourierRefusals() {}

  public static boolean refusesToCarry(final ItemStack payload, final CourierArrowConfig config) {
    Objects.requireNonNull(config, "config");
    return payload == null
        || payload.isEmpty()
        || CourierPayloads.isCourier(payload)
        || config.isUndeliverable(Registries.ITEM.getId(payload.getItem()).toString());
  }

  public static boolean refusesToDeliver(final ItemStack payload, final CourierArrowConfig config) {
    return refusesToCarry(payload, config) || config.exceedsPayload(payload.getCount());
  }

  public static int loadableCount(final ItemStack payload, final CourierArrowConfig config) {
    Objects.requireNonNull(config, "config");
    return refusesToCarry(payload, config) ? 0 : Math.min(payload.getCount(), config.maxPayload());
  }
}
