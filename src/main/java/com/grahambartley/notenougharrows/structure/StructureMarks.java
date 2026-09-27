package com.grahambartley.notenougharrows.structure;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;

public final class StructureMarks {
  public static final StructureMarks NONE = new StructureMarks(List.of());

  private final Map<BlockPos, StructureMark> byPos;

  public StructureMarks(final List<StructureMark> marks) {
    this(indexed(marks));
  }

  private StructureMarks(final Map<BlockPos, StructureMark> byPos) {
    this.byPos = byPos;
  }

  public List<StructureMark> marks() {
    return List.copyOf(byPos.values());
  }

  public boolean isEmpty() {
    return byPos.isEmpty();
  }

  public Optional<StructureMark> at(final BlockPos pos) {
    return Optional.ofNullable(byPos.get(pos));
  }

  public StructureMarks with(final StructureMark mark) {
    final Map<BlockPos, StructureMark> next = new LinkedHashMap<>(byPos);
    next.remove(mark.pos());
    next.put(mark.pos(), mark);
    return new StructureMarks(next);
  }

  public StructureMarks without(final BlockPos pos) {
    if (!byPos.containsKey(pos)) {
      return this;
    }
    final Map<BlockPos, StructureMark> next = new LinkedHashMap<>(byPos);
    next.remove(pos);
    return new StructureMarks(next);
  }

  public List<StructureMark> stale(final Predicate<StructureMark> isLive) {
    return byPos.values().stream().filter(isLive.negate()).toList();
  }

  @Override
  public boolean equals(final Object other) {
    return other instanceof StructureMarks marks && marks().equals(marks.marks());
  }

  @Override
  public int hashCode() {
    return marks().hashCode();
  }

  @Override
  public String toString() {
    return "StructureMarks" + marks();
  }

  private static Map<BlockPos, StructureMark> indexed(final List<StructureMark> marks) {
    final Map<BlockPos, StructureMark> byPos = new LinkedHashMap<>();
    if (marks != null) {
      marks.forEach(
          mark -> {
            byPos.remove(mark.pos());
            byPos.put(mark.pos(), mark);
          });
    }
    return byPos;
  }
}
