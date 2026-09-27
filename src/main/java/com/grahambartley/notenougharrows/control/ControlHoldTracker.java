package com.grahambartley.notenougharrows.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class ControlHoldTracker {
  private final Map<UUID, ControlHold> holds = new LinkedHashMap<>();

  public void hold(final ControlHold hold) {
    holds.put(hold.mobId(), hold);
  }

  public Optional<ControlHold> find(final UUID mobId) {
    return Optional.ofNullable(holds.get(mobId));
  }

  public List<ControlHold> takeExpired(final long tick) {
    final List<ControlHold> expired = new ArrayList<>();
    final Iterator<Map.Entry<UUID, ControlHold>> remaining = holds.entrySet().iterator();
    while (remaining.hasNext()) {
      final ControlHold hold = remaining.next().getValue();
      if (hold.hasExpired(tick)) {
        expired.add(hold);
        remaining.remove();
      }
    }
    return List.copyOf(expired);
  }

  public List<ControlHold> live() {
    return List.copyOf(holds.values());
  }

  public void forget(final UUID mobId) {
    holds.remove(mobId);
  }

  public int size() {
    return holds.size();
  }

  public boolean isEmpty() {
    return holds.isEmpty();
  }
}
