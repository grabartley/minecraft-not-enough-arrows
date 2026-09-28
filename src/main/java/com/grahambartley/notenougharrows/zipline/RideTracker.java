package com.grahambartley.notenougharrows.zipline;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public final class RideTracker {
  private final Map<UUID, RideSession> ridesByRider = new LinkedHashMap<>();

  public void add(@Nullable final RideSession session) {
    if (session == null || session.hasExpired()) {
      return;
    }
    ridesByRider.put(session.riderId(), session);
  }

  @Nullable
  public RideSession rideOf(@Nullable final UUID riderId) {
    return riderId == null ? null : ridesByRider.get(riderId);
  }

  @Nullable
  public RideSession remove(@Nullable final UUID riderId) {
    return riderId == null ? null : ridesByRider.remove(riderId);
  }

  public List<RideSession> rides() {
    return List.copyOf(ridesByRider.values());
  }

  public int size() {
    return ridesByRider.size();
  }

  public boolean isEmpty() {
    return ridesByRider.isEmpty();
  }
}
