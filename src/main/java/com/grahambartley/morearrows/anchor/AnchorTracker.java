package com.grahambartley.morearrows.anchor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import org.jetbrains.annotations.Nullable;

public final class AnchorTracker {
  private final Map<UUID, BlockAnchor> anchorsByOwner = new LinkedHashMap<>();

  public void add(@Nullable final BlockAnchor anchor) {
    if (anchor == null) {
      return;
    }
    anchorsByOwner.put(anchor.ownerId(), anchor);
  }

  @Nullable
  public BlockAnchor anchorOf(@Nullable final UUID ownerId) {
    return ownerId == null ? null : anchorsByOwner.get(ownerId);
  }

  @Nullable
  public BlockAnchor remove(@Nullable final UUID ownerId) {
    return ownerId == null ? null : anchorsByOwner.remove(ownerId);
  }

  public List<BlockAnchor> takeIf(final Predicate<BlockAnchor> dropped) {
    final List<BlockAnchor> taken = new ArrayList<>();
    final Iterator<BlockAnchor> remaining = anchorsByOwner.values().iterator();
    while (remaining.hasNext()) {
      final BlockAnchor anchor = remaining.next();
      if (dropped.test(anchor)) {
        taken.add(anchor);
        remaining.remove();
      }
    }
    return List.copyOf(taken);
  }

  public List<BlockAnchor> anchors() {
    return List.copyOf(anchorsByOwner.values());
  }

  public int size() {
    return anchorsByOwner.size();
  }

  public boolean isEmpty() {
    return anchorsByOwner.isEmpty();
  }
}
