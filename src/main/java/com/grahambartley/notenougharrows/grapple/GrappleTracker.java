package com.grahambartley.notenougharrows.grapple;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public final class GrappleTracker {
  private final Map<UUID, GrappleSession> sessionsByPlayer = new LinkedHashMap<>();

  public void add(@Nullable final GrappleSession session) {
    if (session == null || session.hasExpired()) {
      return;
    }
    sessionsByPlayer.put(session.playerId(), session);
  }

  @Nullable
  public GrappleSession sessionOf(@Nullable final UUID playerId) {
    return playerId == null ? null : sessionsByPlayer.get(playerId);
  }

  @Nullable
  public GrappleSession remove(@Nullable final UUID playerId) {
    return playerId == null ? null : sessionsByPlayer.remove(playerId);
  }

  public List<GrappleSession> sessions() {
    return List.copyOf(sessionsByPlayer.values());
  }

  public int size() {
    return sessionsByPlayer.size();
  }

  public boolean isEmpty() {
    return sessionsByPlayer.isEmpty();
  }
}
