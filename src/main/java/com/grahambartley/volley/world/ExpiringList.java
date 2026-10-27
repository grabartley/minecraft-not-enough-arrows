package com.grahambartley.volley.world;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public final class ExpiringList<T> {
  private final List<T> items = new ArrayList<>();
  private final BiPredicate<T, Long> hasExpired;

  public ExpiringList(final BiPredicate<T, Long> hasExpired) {
    this.hasExpired = hasExpired;
  }

  public void add(final T item) {
    if (item != null) {
      items.add(item);
    }
  }

  public void removeExpired(final long tick) {
    items.removeIf(item -> hasExpired.test(item, tick));
  }

  public List<T> live() {
    return List.copyOf(items);
  }

  public int size() {
    return items.size();
  }

  public boolean isEmpty() {
    return items.isEmpty();
  }
}
