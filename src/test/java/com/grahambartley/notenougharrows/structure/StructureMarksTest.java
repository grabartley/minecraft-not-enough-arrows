package com.grahambartley.notenougharrows.structure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;

class StructureMarksTest {
  private static final UUID LIVE = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID GONE = UUID.fromString("00000000-0000-0000-0000-000000000002");
  private static final Identifier STONE = Identifier.of("minecraft", "stone");
  private static final Identifier DIRT = Identifier.of("minecraft", "dirt");
  private static final BlockPos FIRST = new BlockPos(0, 64, 0);
  private static final BlockPos SECOND = new BlockPos(1, 64, 0);

  @Test
  void noMarksIsEmpty() {
    assertTrue(StructureMarks.NONE.isEmpty());
    assertTrue(new StructureMarks(null).isEmpty());
  }

  @Test
  void aMarkIsFoundAtItsPosition() {
    final StructureMark mark = new StructureMark(FIRST, STONE, LIVE);

    assertEquals(mark, StructureMarks.NONE.with(mark).at(FIRST).orElseThrow());
    assertTrue(StructureMarks.NONE.with(mark).at(SECOND).isEmpty());
  }

  @Test
  void aNewMarkAtAPositionReplacesTheOldOne() {
    final StructureMark later = new StructureMark(FIRST, DIRT, GONE);
    final StructureMarks marks =
        StructureMarks.NONE.with(new StructureMark(FIRST, STONE, LIVE)).with(later);

    assertEquals(List.of(later), marks.marks());
  }

  @Test
  void removingAMarkKeepsTheOthers() {
    final StructureMark kept = new StructureMark(SECOND, STONE, LIVE);
    final StructureMarks marks =
        StructureMarks.NONE.with(new StructureMark(FIRST, STONE, LIVE)).with(kept);

    assertEquals(List.of(kept), marks.without(FIRST).marks());
  }

  @Test
  void removingAPositionWithNoMarkChangesNothing() {
    final StructureMarks marks = StructureMarks.NONE.with(new StructureMark(FIRST, STONE, LIVE));

    assertEquals(marks, marks.without(SECOND));
  }

  @Test
  void aMarkIsStaleWhenItsStructureIsNoLongerLive() {
    final StructureMark stale = new StructureMark(SECOND, STONE, GONE);
    final StructureMarks marks =
        StructureMarks.NONE.with(new StructureMark(FIRST, STONE, LIVE)).with(stale);

    assertEquals(List.of(stale), marks.stale(mark -> Set.of(LIVE).contains(mark.structure())));
  }

  @Test
  void everyMarkIsStaleWhenNothingIsLive() {
    final StructureMarks marks =
        StructureMarks.NONE
            .with(new StructureMark(FIRST, STONE, LIVE))
            .with(new StructureMark(SECOND, STONE, GONE));

    assertEquals(marks.marks(), marks.stale(mark -> false));
  }
}
