package com.grahambartley.notenougharrows.reveal;

import java.util.ArrayList;
import java.util.List;

public final class ExpiringReveals<T> {
  private final int capacity;
  private final List<Entry<T>> entries = new ArrayList<>();

  public ExpiringReveals(final int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("Capacity must be positive but was " + capacity);
    }
    this.capacity = capacity;
  }

  public void accept(final T reveal, final int lifetimeTicks) {
    if (reveal == null || lifetimeTicks <= 0) {
      return;
    }
    if (entries.size() >= capacity) {
      entries.remove(0);
    }
    entries.add(new Entry<>(reveal, lifetimeTicks));
  }

  public void tick() {
    entries.replaceAll(entry -> new Entry<>(entry.reveal(), entry.remainingTicks() - 1));
    entries.removeIf(entry -> entry.remainingTicks() <= 0);
  }

  public List<T> live() {
    return entries.stream().map(Entry::reveal).toList();
  }

  public void clear() {
    entries.clear();
  }

  private record Entry<T>(T reveal, int remainingTicks) {}
}
