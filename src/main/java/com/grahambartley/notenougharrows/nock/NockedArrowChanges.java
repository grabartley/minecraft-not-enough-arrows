package com.grahambartley.notenougharrows.nock;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public final class NockedArrowChanges {
  private final Map<UUID, Item> lastSent = new HashMap<>();

  public boolean record(final UUID player, final ItemStack arrow) {
    final Item nocked = arrow.getItem();
    if (lastSent.getOrDefault(player, Items.AIR) == nocked) {
      return false;
    }
    if (arrow.isEmpty()) {
      lastSent.remove(player);
    } else {
      lastSent.put(player, nocked);
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
