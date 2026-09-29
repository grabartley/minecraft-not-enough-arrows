package com.grahambartley.notenougharrows.reveal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;

public final class WatcherLedger {
  private final Map<UUID, Watcher> watchers = new LinkedHashMap<>();

  public static final int MAX_PER_OWNER = 16;

  public void add(final Watcher watcher) {
    if (watcher.owner() != null) {
      final List<Watcher> owned =
          watchers.values().stream().filter(held -> watcher.owner().equals(held.owner())).toList();
      owned.stream()
          .limit(Math.max(0, owned.size() - MAX_PER_OWNER + 1))
          .forEach(oldest -> watchers.remove(oldest.id()));
    }
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
}
