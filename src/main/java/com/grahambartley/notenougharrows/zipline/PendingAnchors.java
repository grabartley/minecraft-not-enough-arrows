package com.grahambartley.notenougharrows.zipline;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.util.Identifier;

public final class PendingAnchors {
  private final Map<UUID, PendingAnchor> anchorsByOwner = new HashMap<>();

  public Optional<PendingAnchor> hold(final UUID ownerId, final PendingAnchor anchor) {
    if (ownerId == null || anchor == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(anchorsByOwner.put(ownerId, anchor));
  }

  public Optional<PendingAnchor> take(final UUID ownerId) {
    return Optional.ofNullable(ownerId == null ? null : anchorsByOwner.remove(ownerId));
  }

  public Optional<PendingAnchor> of(final UUID ownerId) {
    return Optional.ofNullable(ownerId == null ? null : anchorsByOwner.get(ownerId));
  }

  public int dropExpiredIn(final Identifier worldId, final long tick) {
    final int before = anchorsByOwner.size();
    anchorsByOwner.values().removeIf(anchor -> anchor.isIn(worldId) && anchor.hasExpired(tick));
    return before - anchorsByOwner.size();
  }

  public int size() {
    return anchorsByOwner.size();
  }

  public boolean isEmpty() {
    return anchorsByOwner.isEmpty();
  }

  public void clear() {
    anchorsByOwner.clear();
  }
}
