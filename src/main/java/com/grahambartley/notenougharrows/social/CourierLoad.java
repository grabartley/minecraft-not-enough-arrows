package com.grahambartley.notenougharrows.social;

import com.grahambartley.notenougharrows.config.CourierArrowConfig;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.item.ItemStack;

public record CourierLoad(ItemStack arrow, ItemStack payload, int count) {
  private static final int LOADING_STACKS = 2;

  public static Optional<CourierLoad> of(
      final List<ItemStack> stacks, final CourierArrowConfig config) {
    Objects.requireNonNull(config, "config");
    final List<ItemStack> occupied = stacks.stream().filter(it -> !it.isEmpty()).toList();
    if (occupied.size() != LOADING_STACKS) {
      return Optional.empty();
    }
    final ItemStack first = occupied.get(0);
    final ItemStack arrow = CourierPayloads.isEmptyCourier(first) ? first : occupied.get(1);
    final ItemStack payload = arrow == first ? occupied.get(1) : first;
    final int count = CourierRefusals.loadableCount(payload, config);
    if (!CourierPayloads.isEmptyCourier(arrow) || count <= 0) {
      return Optional.empty();
    }
    return Optional.of(new CourierLoad(arrow, payload, count));
  }

  public ItemStack loaded() {
    return CourierPayloads.loaded(arrow, payload.copyWithCount(count));
  }
}
