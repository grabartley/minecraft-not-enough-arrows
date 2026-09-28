package com.grahambartley.notenougharrows.traversal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;

class VerticalRunTest {
  private static final BlockPos START = new BlockPos(4, 64, -2);

  @Test
  void aRunStartsAtItsFirstBlockAndClimbsOneBlockAtATime() {
    assertEquals(List.of(START, START.up(), START.up(2)), VerticalRun.upFrom(START, 3));
  }

  @Test
  void aRunOfNothingIsEmpty() {
    assertTrue(VerticalRun.upFrom(START, 0).isEmpty());
    assertTrue(VerticalRun.upFrom(START, -4).isEmpty());
    assertTrue(VerticalRun.upFrom(null, 3).isEmpty());
  }
}
