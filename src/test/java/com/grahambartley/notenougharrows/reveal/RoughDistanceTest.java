package com.grahambartley.notenougharrows.reveal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RoughDistanceTest {

  @ParameterizedTest
  @CsvSource({"0, 10", "3.2, 10", "14.9, 10", "15, 20", "23.7, 20", "41, 40", "96, 100", "-5, 10"})
  void roundsToTheNearestTenAndNeverSaysZero(final double blocks, final int expected) {
    assertEquals(expected, RoughDistance.of(blocks));
  }
}
