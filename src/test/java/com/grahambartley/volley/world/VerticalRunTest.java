package com.grahambartley.volley.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class VerticalRunTest {
  private static final BlockPos START = new BlockPos(4, 64, -2);

  @Test
  void aRunStartsAtItsFirstBlockAndClimbsOneBlockAtATime() {
    assertEquals(List.of(START, START.up(), START.up(2)), VerticalRun.upFrom(START, 3));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, -4})
  void aRunOfNoLengthIsEmpty(final int length) {
    assertTrue(VerticalRun.upFrom(START, length).isEmpty());
  }

  @Test
  void aRunFromNowhereIsEmpty() {
    assertTrue(VerticalRun.upFrom(null, 3).isEmpty());
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 8, 32})
  void aRunHoldsOneBlockPerLengthEndingStraightAboveTheStart(final int length) {
    final List<BlockPos> run = VerticalRun.upFrom(START, length);

    assertEquals(length, run.size());
    assertEquals(START.up(length - 1), run.get(length - 1));
  }
}
