package com.grahambartley.notenougharrows.control;

import java.util.Objects;
import java.util.UUID;

public record DisarmFetch(UUID mobId, UUID itemId, float mainHandDropChance, long expiryTick) {
  public static final int OWNER_SETTLE_TICKS = 20;

  public DisarmFetch {
    Objects.requireNonNull(mobId, "mobId");
    Objects.requireNonNull(itemId, "itemId");
  }

  public boolean hasExpired(final long tick) {
    return tick >= expiryTick;
  }

  public static boolean canGrab(final boolean withinPickupRange, final int itemAge) {
    return withinPickupRange && itemAge >= OWNER_SETTLE_TICKS;
  }
}
