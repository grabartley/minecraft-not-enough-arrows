package com.grahambartley.notenougharrows.reveal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;

public final class WatcherLedger {
  private final Map<UUID, Watcher> watchers = new LinkedHashMap<>();

  public void add(final Watcher watcher) {
    watchers.put(watcher.id(), watcher);
  }

  public void replace(final Watcher watcher) {
    watchers.computeIfPresent(watcher.id(), (id, previous) -> watcher);
  }

  public int dropWhere(final long tick, final Predicate<BlockPos> isLoaded) {
    final int before = watchers.size();
    watchers
        .values()
        .removeIf(watcher -> watcher.hasExpired(tick) || !isLoaded.test(watcher.pos()));
    return before - watchers.size();
  }

  public List<Watcher> all() {
    return List.copyOf(watchers.values());
  }

  public boolean isEmpty() {
    return watchers.isEmpty();
  }

  public void clear() {
    watchers.clear();
  }
}
