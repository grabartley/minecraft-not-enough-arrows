package com.grahambartley.notenougharrows.zipline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SpanLineTest {
  private static final BlockPos ORIGIN = new BlockPos(0, 64, 0);

  @Test
  void aStraightSpanFillsEveryBlockBetweenTheAnchors() {
    assertEquals(
        List.of(new BlockPos(1, 64, 0), new BlockPos(2, 64, 0), new BlockPos(3, 64, 0)),
        SpanLine.between(ORIGIN, new BlockPos(4, 64, 0)));
  }

  @Test
  void theAnchorsThemselvesAreNeverPartOfTheSpan() {
    final BlockPos far = new BlockPos(7, 70, -3);

    final List<BlockPos> line = SpanLine.between(ORIGIN, far);

    assertFalse(line.contains(ORIGIN));
    assertFalse(line.contains(far));
  }

  @Test
  void adjacentAnchorsLeaveNothingToString() {
    assertTrue(SpanLine.between(ORIGIN, ORIGIN.east()).isEmpty());
  }

  @Test
  void theSameBlockTwiceLeavesNothingToString() {
    assertTrue(SpanLine.between(ORIGIN, ORIGIN).isEmpty());
  }

  @Test
  void aMissingAnchorLeavesNothingToString() {
    assertTrue(SpanLine.between(null, ORIGIN).isEmpty());
    assertTrue(SpanLine.between(ORIGIN, null).isEmpty());
  }

  @ParameterizedTest
  @CsvSource({"10, 5, 3", "-6, -9, 2", "3, 3, 3", "0, -12, 5", "12, 1, 0"})
  void everyStepMovesOneBlockAlongOneAxisSoTheCableIsUnbroken(
      final int dx, final int dy, final int dz) {
    final BlockPos far = ORIGIN.add(dx, dy, dz);
    final List<BlockPos> walk = new ArrayList<>();
    walk.add(ORIGIN);
    walk.addAll(SpanLine.between(ORIGIN, far));
    walk.add(far);

    for (int i = 1; i < walk.size(); i++) {
      assertEquals(1, walk.get(i).getManhattanDistance(walk.get(i - 1)), "step " + i);
    }
  }

  @ParameterizedTest
  @CsvSource({"10, 5, 3", "-6, -9, 2", "3, 3, 3"})
  void theSpanIsTheSameWhicheverEndItIsStrungFrom(final int dx, final int dy, final int dz) {
    final BlockPos far = ORIGIN.add(dx, dy, dz);

    assertEquals(SpanLine.between(ORIGIN, far).size(), SpanLine.between(far, ORIGIN).size());
  }

  @Test
  void separationIsMeasuredAnchorToAnchor() {
    assertEquals(5.0, SpanLine.separation(ORIGIN, ORIGIN.add(3, 4, 0)), 1.0E-9);
  }

  @ParameterizedTest
  @CsvSource({"10, 2, 3, X", "1, -8, 3, Y", "2, 1, -9, Z", "4, 4, 4, Y", "5, 0, 5, X"})
  void theCableRunsAlongTheSpansLongestAxis(
      final int dx, final int dy, final int dz, final Direction.Axis expected) {
    assertEquals(expected, SpanLine.axisOf(ORIGIN, ORIGIN.add(dx, dy, dz)));
  }
}
