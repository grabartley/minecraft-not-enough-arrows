package com.grahambartley.morearrows.render;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.item.ItemStack;

public final class NockedArrowSync {
  private static final Map<Integer, ItemStack> BY_ENTITY_ID = new ConcurrentHashMap<>();

  private NockedArrowSync() {}

  public static void accept(final int entityId, final ItemStack arrow) {
    if (arrow.isEmpty()) {
      BY_ENTITY_ID.remove(entityId);
    } else {
      BY_ENTITY_ID.put(entityId, arrow);
    }
  }

  public static ItemStack drawnBy(final int entityId) {
    return BY_ENTITY_ID.getOrDefault(entityId, ItemStack.EMPTY);
  }

  public static void forget(final int entityId) {
    BY_ENTITY_ID.remove(entityId);
  }

  public static void clear() {
    BY_ENTITY_ID.clear();
  }
}
