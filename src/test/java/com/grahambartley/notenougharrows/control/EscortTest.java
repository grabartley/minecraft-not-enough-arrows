package com.grahambartley.notenougharrows.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EscortTest {
  private static final double NO_WIDTH = 0.0;

  @ParameterizedTest
  @CsvSource({"36.0, false", "36.01, true", "100.0, true", "0.0, false"})
  void closesInOnlyOnceItHasFallenBehind(final double squaredDistance, final boolean expected) {
    assertEquals(expected, Escort.shouldCloseIn(squaredDistance, NO_WIDTH));
  }

  @ParameterizedTest
  @CsvSource({"0.0, true", "9.0, true", "9.01, false"})
  void settlesAtHeelDistance(final double squaredDistance, final boolean expected) {
    assertEquals(expected, Escort.isCloseEnough(squaredDistance, NO_WIDTH));
  }

  @Test
  void aBiggerBodySettlesFurtherOut() {
    final double ravagerWidth = 1.95;
    assertFalse(Escort.isCloseEnough(15.0, NO_WIDTH));
    assertTrue(Escort.isCloseEnough(15.0, ravagerWidth));
    assertFalse(Escort.shouldCloseIn(49.0, 16.0));
  }

  @Test
  void leavesAGapBetweenSettlingAndClosingInSoItDoesNotJitter() {
    assertTrue(Escort.SETTLE_WITHIN < Escort.CLOSE_IN_BEYOND);
    assertFalse(Escort.shouldCloseIn(Escort.SETTLE_WITHIN * Escort.SETTLE_WITHIN, NO_WIDTH));
  }

  @ParameterizedTest
  @CsvSource({"256.0, false", "256.01, true"})
  void losesTrackOnlyOnceItIsFarBehind(final double squaredDistance, final boolean expected) {
    assertEquals(expected, Escort.hasLostTrack(squaredDistance));
  }
}
