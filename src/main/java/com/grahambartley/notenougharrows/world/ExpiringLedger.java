package com.grahambartley.notenougharrows.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class ExpiringLedger<V> {
  private final Map<UUID, Entry<V>> entries = new LinkedHashMap<>();

  public void put(final UUID id, final V value, final long expiryTick) {
    entries.put(
        Objects.requireNonNull(id, "id"),
        new Entry<>(Objects.requireNonNull(value, "value"), expiryTick));
  }

  public Optional<V> get(final UUID id) {
    return Optional.ofNullable(entries.get(id)).map(Entry::value);
  }

  public boolean contains(final UUID id) {
    return id != null && entries.containsKey(id);
  }

  public Optional<V> remove(final UUID id) {
    return Optional.ofNullable(entries.remove(id)).map(Entry::value);
  }

  public List<UUID> removeExpired(final long tick) {
    final List<UUID> expired = new ArrayList<>();
    final Iterator<Map.Entry<UUID, Entry<V>>> remaining = entries.entrySet().iterator();
    while (remaining.hasNext()) {
      final Map.Entry<UUID, Entry<V>> entry = remaining.next();
      if (tick >= entry.getValue().expiryTick()) {
        expired.add(entry.getKey());
        remaining.remove();
      }
    }
    return List.copyOf(expired);
  }

  public Set<UUID> ids() {
    return Set.copyOf(entries.keySet());
  }

  public boolean isEmpty() {
    return entries.isEmpty();
  }

  public int size() {
    return entries.size();
  }

  public void clear() {
    entries.clear();
  }

  private record Entry<V>(V value, long expiryTick) {}
}
