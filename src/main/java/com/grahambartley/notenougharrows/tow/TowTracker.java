package com.grahambartley.notenougharrows.tow;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public final class TowTracker {
  private final Map<UUID, TowSession> towsByTarget = new LinkedHashMap<>();

  public void add(@Nullable final TowSession session) {
    if (session == null || session.hasExpired()) {
      return;
    }
    towsByTarget.put(session.targetId(), session);
  }

  @Nullable
  public TowSession towOf(@Nullable final UUID targetId) {
    return targetId == null ? null : towsByTarget.get(targetId);
  }

  @Nullable
  public TowSession remove(@Nullable final UUID targetId) {
    return targetId == null ? null : towsByTarget.remove(targetId);
  }

  public List<TowSession> removeInvolving(@Nullable final UUID entityId) {
    if (entityId == null) {
      return List.of();
    }
    final List<TowSession> involved =
        towsByTarget.values().stream().filter(tow -> tow.involves(entityId)).toList();
    involved.forEach(tow -> towsByTarget.remove(tow.targetId()));
    return involved;
  }

  public List<TowSession> tows() {
    return List.copyOf(towsByTarget.values());
  }

  public int size() {
    return towsByTarget.size();
  }

  public boolean isEmpty() {
    return towsByTarget.isEmpty();
  }
}
