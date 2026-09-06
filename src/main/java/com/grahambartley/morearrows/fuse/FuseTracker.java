package com.grahambartley.morearrows.fuse;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public final class FuseTracker {
  private final Map<UUID, Fuse> fusesByHost = new LinkedHashMap<>();

  public void add(@Nullable final Fuse fuse) {
    if (fuse == null || fuse.hasExpired()) {
      return;
    }
    fusesByHost.put(fuse.hostId(), fuse);
  }

  @Nullable
  public Fuse fuseOn(@Nullable final UUID hostId) {
    return hostId == null ? null : fusesByHost.get(hostId);
  }

  @Nullable
  public Fuse remove(@Nullable final UUID hostId) {
    return hostId == null ? null : fusesByHost.remove(hostId);
  }

  public List<Fuse> fuses() {
    return List.copyOf(fusesByHost.values());
  }

  public int size() {
    return fusesByHost.size();
  }

  public boolean isEmpty() {
    return fusesByHost.isEmpty();
  }
}
