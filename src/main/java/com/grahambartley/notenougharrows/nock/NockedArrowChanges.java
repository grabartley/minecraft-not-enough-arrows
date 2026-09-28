package com.grahambartley.notenougharrows.nock;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.item.ItemStack;

public final class NockedArrowChanges {
  private final Map<UUID, ItemStack> lastSent = new HashMap<>();

  public boolean record(final UUID player, final ItemStack arrow) {
    if (ItemStack.areItemsAndComponentsEqual(
        lastSent.getOrDefault(player, ItemStack.EMPTY), arrow)) {
      return false;
    }
    if (arrow.isEmpty()) {
      lastSent.remove(player);
    } else {
      lastSent.put(player, arrow.copyWithCount(1));
    }
    return true;
  }

  public void forget(final UUID player) {
    lastSent.remove(player);
  }

  public void clear() {
    lastSent.clear();
  }
}
