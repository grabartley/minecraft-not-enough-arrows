package com.grahambartley.notenougharrows.control;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FrostBuildTest {

  private static final int THRESHOLD = 140;

  @ParameterizedTest
  @CsvSource({"0, 40, 40", "40, 40, 80", "100, 40, 140", "140, 40, 180"})
  void addsOneHitsWorthOfFreeze(final int current, final int perHit, final int expected) {
    assertEquals(expected, FrostBuild.builtTicks(current, perHit, THRESHOLD));
  }

  @Test
  void capsRepeatedHitsOneHitPastTheDamageThreshold() {
    int frozen = 0;
    for (int hit = 0; hit < 20; hit++) {
      frozen = FrostBuild.builtTicks(frozen, 40, THRESHOLD);
    }

    assertEquals(THRESHOLD + 40, frozen);
  }

  @Test
  void aHitThatBuildsNothingLeavesTheTargetWhereItWas() {
    assertEquals(75, FrostBuild.builtTicks(75, 0, THRESHOLD));
    assertEquals(75, FrostBuild.builtTicks(75, -10, THRESHOLD));
  }

  @Test
  void treatsANegativeCurrentAsThawed() {
    assertEquals(40, FrostBuild.builtTicks(-20, 40, THRESHOLD));
  }
}
