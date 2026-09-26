package com.grahambartley.notenougharrows.control;

import java.util.Objects;
import java.util.UUID;

public record DisarmFetch(UUID mobId, float mainHandDropChance, long expiryTick) {

  public DisarmFetch {
    Objects.requireNonNull(mobId, "mobId");
  }

  public boolean hasExpired(final long tick) {
    return tick >= expiryTick;
  }
}
