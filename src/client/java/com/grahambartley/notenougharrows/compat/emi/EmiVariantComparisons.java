package com.grahambartley.notenougharrows.compat.emi;

import com.grahambartley.notenougharrows.item.TintedArrowItems;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.Comparison;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.item.Item;

public final class EmiVariantComparisons {
  private final Supplier<? extends List<? extends Item>> items;

  public EmiVariantComparisons(final Supplier<? extends List<? extends Item>> items) {
    this.items = Objects.requireNonNull(items, "items");
  }

  public static EmiVariantComparisons forTintedArrows() {
    return new EmiVariantComparisons(TintedArrowItems::registered);
  }

  public void register(final EmiRegistry registry) {
    Objects.requireNonNull(registry, "registry");
    for (final Item item : items.get()) {
      registry.setDefaultComparison(item, Comparison.compareComponents());
    }
  }
}
