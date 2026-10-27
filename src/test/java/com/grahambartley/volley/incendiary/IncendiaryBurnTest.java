package com.grahambartley.volley.incendiary;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class IncendiaryBurnTest {
  @ParameterizedTest
  @CsvSource({"0, 0", "1, 20", "5, 100", "60, 1200"})
  void igniteSecondsBecomeTicks(final int seconds, final int ticks) {
    assertEquals(ticks, IncendiaryBurn.igniteTicks(seconds));
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, -60})
  void aNegativeIgniteTimeNeverBurns(final int seconds) {
    assertEquals(0, IncendiaryBurn.igniteTicks(seconds));
  }
}
