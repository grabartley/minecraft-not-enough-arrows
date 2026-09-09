package com.grahambartley.morearrows.countdown;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class CountdownChanges {
  private final Map<UUID, Announced> lastSent = new HashMap<>();

  public record Announced(int carrierId, int delayTicks) {}

  public boolean record(final UUID carrier, final int carrierId, final int delayTicks) {
    if (carrier == null || delayTicks <= 0) {
      return false;
    }

    final Announced announced = new Announced(carrierId, delayTicks);
    return !announced.equals(lastSent.put(carrier, announced));
  }

  public Set<UUID> announced() {
    return Set.copyOf(lastSent.keySet());
  }

  public Announced forget(final UUID carrier) {
    return carrier == null ? null : lastSent.remove(carrier);
  }

  public boolean knows(final UUID carrier) {
    return lastSent.containsKey(carrier);
  }
}
