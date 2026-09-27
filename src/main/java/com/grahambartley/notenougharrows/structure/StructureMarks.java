package com.grahambartley.notenougharrows.structure;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public record StructureMarks(List<StructureMark> marks) {
  public static final StructureMarks NONE = new StructureMarks(List.of());

  public StructureMarks {
    marks = marks == null ? List.of() : List.copyOf(marks);
  }

  public boolean isEmpty() {
    return marks.isEmpty();
  }

  public Optional<StructureMark> at(final BlockPos pos) {
    return marks.stream().filter(mark -> mark.pos().equals(pos)).findFirst();
  }

  public StructureMarks with(final StructureMark mark) {
    return new StructureMarks(Stream.concat(withoutPos(mark.pos()), Stream.of(mark)).toList());
  }

  public StructureMarks without(final BlockPos pos) {
    return new StructureMarks(withoutPos(pos).toList());
  }

  public List<StructureMark> stale(final Predicate<StructureMark> isLive) {
    return marks.stream().filter(isLive.negate()).toList();
  }

  private Stream<StructureMark> withoutPos(final BlockPos pos) {
    return marks.stream().filter(held -> !held.pos().equals(pos));
  }
}
