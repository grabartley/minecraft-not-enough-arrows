package com.grahambartley.notenougharrows.hud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CountdownTest {
  private static final int CARRIER_ID = 7;

  @Test
  void aLitFuseIsBurning() {
    assertTrue(new Countdown(CARRIER_ID, 60, 60).isBurning());
  }

  @ParameterizedTest
  @CsvSource({"60, 0", "0, 0", "0, 60"})
  void aFuseWithNoDelayOrNothingLeftIsNotBurning(final int delayTicks, final int remainingTicks) {
    assertFalse(new Countdown(CARRIER_ID, delayTicks, remainingTicks).isBurning());
  }

  @Test
  void burningDownTakesOneTickOff() {
    assertEquals(59, new Countdown(CARRIER_ID, 60, 60).burned().remainingTicks());
  }

  @Test
  void burningDownStopsAtZeroRatherThanGoingNegative() {
    assertEquals(0, new Countdown(CARRIER_ID, 60, 0).burned().remainingTicks());
  }

  @ParameterizedTest
  @CsvSource({"-60, 0", "60, -1"})
  void negativeInputIsClampedAway(final int delayTicks, final int remainingTicks) {
    final Countdown countdown = new Countdown(CARRIER_ID, delayTicks, remainingTicks);

    assertTrue(countdown.delayTicks() >= 0);
    assertTrue(countdown.remainingTicks() >= 0);
  }

  @Test
  void aRemainderLongerThanTheDelayIsClampedToTheDelay() {
    assertEquals(60, new Countdown(CARRIER_ID, 60, 200).remainingTicks());
  }
}
