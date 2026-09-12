package com.grahambartley.notenougharrows.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class ReachTest {
  private static final Vec3d ORIGIN = new Vec3d(0.0, 64.0, 0.0);
  private static final double TOLERANCE = 1.0e-6;

  @ParameterizedTest
  @CsvSource({"0.0, 0.0", "3.0, 3.0", "12.5, 12.5"})
  void measuresTheStraightLineDistance(final double offset, final double expected) {
    assertEquals(expected, Reach.between(ORIGIN, ORIGIN.add(offset, 0.0, 0.0)), TOLERANCE);
  }

  @Test
  void measuresDiagonallyRatherThanPerAxis() {
    assertEquals(5.0, Reach.between(ORIGIN, ORIGIN.add(3.0, 4.0, 0.0)), TOLERANCE);
  }

  @ParameterizedTest
  @CsvSource({"31.0, 32, true", "32.0, 32, true", "32.5, 32, false", "64.0, 32, false"})
  void aTargetIsInReachUpToAndIncludingTheLimit(
      final double distance, final int maxBlocks, final boolean within) {
    assertEquals(within, Reach.isWithin(ORIGIN, ORIGIN.add(distance, 0.0, 0.0), maxBlocks));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, -32})
  void aLimitOfZeroOrLessPutsEveryTargetOutOfReach(final int maxBlocks) {
    assertFalse(Reach.isWithin(ORIGIN, ORIGIN.add(1.0, 0.0, 0.0), maxBlocks));
  }

  @Test
  void refusesToMeasureFromNowhere() {
    assertThrows(NullPointerException.class, () -> Reach.between(null, ORIGIN));
  }

  @Test
  void refusesToMeasureToNowhere() {
    assertThrows(NullPointerException.class, () -> Reach.between(ORIGIN, null));
  }
}
