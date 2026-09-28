package com.grahambartley.notenougharrows.agriculture;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class ReplantSeed {

  private ReplantSeed() {}

  public static Optional<List<ItemStack>> takenFrom(final List<ItemStack> drops, final Item seed) {
    if (drops == null || seed == null) {
      return Optional.empty();
    }
    final List<ItemStack> remaining = new ArrayList<>();
    boolean taken = false;
    for (final ItemStack drop : drops) {
      final ItemStack copy = drop.copy();
      if (!taken && copy.isOf(seed)) {
        copy.decrement(1);
        taken = true;
      }
      if (!copy.isEmpty()) {
        remaining.add(copy);
      }
    }
    return taken ? Optional.of(List.copyOf(remaining)) : Optional.empty();
  }
}
