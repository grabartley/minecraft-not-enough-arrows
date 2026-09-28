package com.grahambartley.notenougharrows.tow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TowEndingTest {

  @Test
  void onlyAnArrivalStopsTheTargetDead() {
    for (final TowEnding ending : TowEnding.values()) {
      assertEquals(ending == TowEnding.ARRIVED, ending.stopsTheTarget(), ending.name());
    }
  }
}
