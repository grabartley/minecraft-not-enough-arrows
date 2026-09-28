package com.grahambartley.notenougharrows.updraft;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class UpdraftRiders {
  private final Set<UUID> carried = new HashSet<>();
  private final Set<UUID> departed = new HashSet<>();

  public boolean mayLift(final UUID id) {
    return id != null && !departed.contains(id);
  }

  public void carry(final UUID id) {
    if (mayLift(id)) {
      carried.add(id);
    }
  }

  public void settle(final Collection<UUID> insideNow) {
    final Set<UUID> left = new HashSet<>(carried);
    left.removeAll(insideNow);
    carried.removeAll(left);
    departed.addAll(left);
  }

  public boolean isCarrying(final UUID id) {
    return carried.contains(id);
  }

  public boolean hasLeft(final UUID id) {
    return departed.contains(id);
  }
}
