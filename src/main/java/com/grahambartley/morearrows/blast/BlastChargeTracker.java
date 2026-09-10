package com.grahambartley.morearrows.blast;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public final class BlastChargeTracker {
  private final Map<UUID, BlastCharge> chargesByCarrier = new LinkedHashMap<>();

  public void add(@Nullable final BlastCharge charge) {
    if (charge == null) {
      return;
    }
    chargesByCarrier.put(charge.carrierId(), charge);
  }

  @Nullable
  public BlastCharge chargeOn(@Nullable final UUID carrierId) {
    return carrierId == null ? null : chargesByCarrier.get(carrierId);
  }

  @Nullable
  public BlastCharge remove(@Nullable final UUID carrierId) {
    return carrierId == null ? null : chargesByCarrier.remove(carrierId);
  }

  public void retainOnly(@Nullable final Collection<UUID> carrierIds) {
    final Set<UUID> kept = carrierIds == null ? Set.of() : new HashSet<>(carrierIds);
    chargesByCarrier.keySet().removeIf(carrierId -> !kept.contains(carrierId));
  }

  public List<BlastCharge> charges() {
    return List.copyOf(chargesByCarrier.values());
  }

  public int size() {
    return chargesByCarrier.size();
  }

  public boolean isEmpty() {
    return chargesByCarrier.isEmpty();
  }
}
