package com.grahambartley.notenougharrows.combat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StatusArrowImpactTest {

  @ParameterizedTest
  @ValueSource(ints = {1, 20, 200, 12000})
  void aDurationOfAtLeastOneTickIsWorthApplying(final int durationTicks) {
    assertTrue(StatusArrowImpact.lasts(durationTicks));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, -12000})
  void aDurationOfNothingAppliesNothing(final int durationTicks) {
    assertFalse(StatusArrowImpact.lasts(durationTicks));
  }
}
