package com.grahambartley.morearrows.fire;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.math.BlockPos;

public final class FirePatchTracker {
  private final List<FirePatch> patches = new ArrayList<>();

  public void add(final FirePatch patch) {
    if (patch == null || patch.isEmpty()) {
      return;
    }
    patches.add(patch);
  }

  public List<BlockPos> takeExpired(final long tick) {
    final List<BlockPos> expired = new ArrayList<>();
    final Iterator<FirePatch> remaining = patches.iterator();
    while (remaining.hasNext()) {
      final FirePatch patch = remaining.next();
      if (patch.hasExpired(tick)) {
        expired.addAll(patch.positions());
        remaining.remove();
      }
    }
    return List.copyOf(expired);
  }

  public int size() {
    return patches.size();
  }

  public boolean isEmpty() {
    return patches.isEmpty();
  }
}
