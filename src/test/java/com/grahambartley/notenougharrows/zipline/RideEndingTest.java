package com.grahambartley.notenougharrows.zipline;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RideEndingTest {

  @Test
  void theModAnswersForTheFallWhenItStoppedTheRideItself() {
    assertTrue(RideEnding.ARRIVED.ownsTheFall());
    assertTrue(RideEnding.OBSTRUCTED.ownsTheFall());
    assertTrue(RideEnding.OUT_OF_TIME.ownsTheFall());
  }

  @Test
  void aRiderWhoLetGoFallsUnderVanillasRules() {
    assertFalse(RideEnding.LET_GO.ownsTheFall());
    assertFalse(RideEnding.THROWN_OFF.ownsTheFall());
  }

  @Test
  void aRideEndedByTheWorldOrAnotherMoveOwnsNoFall() {
    assertFalse(RideEnding.SPAN_LOST.ownsTheFall());
    assertFalse(RideEnding.REPLACED.ownsTheFall());
    assertFalse(RideEnding.RIDER_GONE.ownsTheFall());
  }
}
