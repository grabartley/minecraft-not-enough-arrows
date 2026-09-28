package com.grahambartley.notenougharrows.structure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.UUID;
import net.minecraft.util.math.BlockPos;

public final class StructureLedger {
  private final Map<UUID, TimedStructure> structures = new HashMap<>();
  private final Map<BlockPos, UUID> holders = new HashMap<>();
  private final PriorityQueue<TimedStructure> byExpiry =
      new PriorityQueue<>(Comparator.comparingLong(TimedStructure::expiryTick));

  public void add(final TimedStructure structure) {
    if (structure == null || structure.isEmpty() || structures.containsKey(structure.id())) {
      return;
    }
    structures.put(structure.id(), structure);
    structure.positions().forEach(pos -> holders.put(pos, structure.id()));
    byExpiry.add(structure);
  }

  public List<TimedStructure> takeExpired(final long tick) {
    final List<TimedStructure> expired = new ArrayList<>();
    while (!byExpiry.isEmpty() && byExpiry.peek().hasExpired(tick)) {
      final TimedStructure structure = structures.remove(byExpiry.poll().id());
      if (structure != null) {
        forgetPositionsOf(structure);
        expired.add(structure);
      }
    }
    return List.copyOf(expired);
  }

  public List<TimedStructure> takeAll() {
    final List<TimedStructure> all = List.copyOf(structures.values());
    structures.clear();
    holders.clear();
    byExpiry.clear();
    return all;
  }

  public Optional<TimedStructure> find(final UUID id) {
    return Optional.ofNullable(id == null ? null : structures.get(id));
  }

  public Optional<TimedStructure> take(final UUID id) {
    final TimedStructure structure = id == null ? null : structures.remove(id);
    if (structure == null) {
      return Optional.empty();
    }
    byExpiry.removeIf(queued -> queued.id().equals(id));
    forgetPositionsOf(structure);
    return Optional.of(structure);
  }

  public boolean release(final BlockPos pos) {
    final UUID holder = holders.remove(pos);
    if (holder == null) {
      return false;
    }
    structures.computeIfPresent(holder, (id, structure) -> structure.without(pos));
    return true;
  }

  public boolean holds(final BlockPos pos) {
    return holders.containsKey(pos);
  }

  public boolean isLive(final UUID id, final long tick) {
    final TimedStructure structure = structures.get(id);
    return structure != null && !structure.hasExpired(tick);
  }

  public int size() {
    return structures.size();
  }

  public boolean isEmpty() {
    return structures.isEmpty();
  }

  private void forgetPositionsOf(final TimedStructure structure) {
    structure.positions().forEach(pos -> holders.remove(pos, structure.id()));
  }
}
