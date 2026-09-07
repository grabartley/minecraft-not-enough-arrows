package com.grahambartley.morearrows.redstone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.util.math.BlockPos;

public final class RedstoneChargeTracker {
  private final Map<BlockPos, RedstoneCharge> charges = new HashMap<>();

  public void add(final RedstoneCharge charge) {
    if (charge == null) {
      return;
    }
    charges.put(charge.pos(), charge);
  }

  public List<BlockPos> takeExpired(final long tick) {
    final List<BlockPos> expired = new ArrayList<>();
    final Iterator<RedstoneCharge> remaining = charges.values().iterator();
    while (remaining.hasNext()) {
      final RedstoneCharge charge = remaining.next();
      if (charge.hasExpired(tick)) {
        expired.add(charge.pos());
        remaining.remove();
      }
    }
    return List.copyOf(expired);
  }

  public List<BlockPos> takeAll() {
    final List<BlockPos> all = List.copyOf(charges.keySet());
    charges.clear();
    return all;
  }

  public boolean isLiveAt(final BlockPos pos, final long tick) {
    final RedstoneCharge charge = pos == null ? null : charges.get(pos);
    return charge != null && !charge.hasExpired(tick);
  }

  public int size() {
    return charges.size();
  }

  public boolean isEmpty() {
    return charges.isEmpty();
  }
}
