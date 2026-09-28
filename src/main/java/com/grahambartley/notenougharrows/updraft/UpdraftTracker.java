package com.grahambartley.notenougharrows.updraft;

import java.util.ArrayList;
import java.util.List;

public final class UpdraftTracker {
  private final List<LiveUpdraft> updrafts = new ArrayList<>();

  public void add(final LiveUpdraft updraft) {
    if (updraft != null) {
      updrafts.add(updraft);
    }
  }

  public void removeExpired(final long tick) {
    updrafts.removeIf(updraft -> updraft.column().hasExpired(tick));
  }

  public List<LiveUpdraft> live() {
    return List.copyOf(updrafts);
  }

  public int size() {
    return updrafts.size();
  }

  public boolean isEmpty() {
    return updrafts.isEmpty();
  }
}
