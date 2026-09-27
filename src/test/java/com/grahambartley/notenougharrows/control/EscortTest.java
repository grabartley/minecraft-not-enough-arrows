package com.grahambartley.notenougharrows.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EscortTest {

  @ParameterizedTest
  @CsvSource({"36.0, false", "36.01, true", "100.0, true", "0.0, false"})
  void closesInOnlyOnceItHasFallenBehind(final double squaredDistance, final boolean expected) {
    assertEquals(expected, Escort.shouldCloseIn(squaredDistance));
  }

  @ParameterizedTest
  @CsvSource({"0.0, true", "9.0, true", "9.01, false"})
  void settlesAtHeelDistance(final double squaredDistance, final boolean expected) {
    assertEquals(expected, Escort.isCloseEnough(squaredDistance));
  }

  @org.junit.jupiter.api.Test
  void leavesAGapBetweenSettlingAndClosingInSoItDoesNotJitter() {
    assertTrue(Escort.SETTLE_WITHIN < Escort.CLOSE_IN_BEYOND);
    assertFalse(Escort.shouldCloseIn(Escort.SETTLE_WITHIN * Escort.SETTLE_WITHIN));
  }
}
