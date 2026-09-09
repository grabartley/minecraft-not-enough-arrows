package com.grahambartley.morearrows.hud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class CountdownReadoutTest {

  @ParameterizedTest
  @CsvSource({"0, 0.0", "10, 0.5", "20, 1.0", "60, 3.0", "200, 10.0"})
  void secondsAreTicksOverTwenty(final int remainingTicks, final float expected) {
    assertEquals(expected, CountdownReadout.secondsLeft(remainingTicks));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, -40})
  void aCountdownNeverReadsBelowZero(final int remainingTicks) {
    assertEquals(0f, CountdownReadout.secondsLeft(remainingTicks));
  }

  @ParameterizedTest
  @CsvSource({"0, 0.0s", "10, 0.5s", "48, 2.4s", "60, 3.0s"})
  void theTextCarriesOneDecimalPlace(final int remainingTicks, final String expected) {
    assertEquals(expected, CountdownReadout.secondsText(remainingTicks));
  }

  @ParameterizedTest
  @CsvSource({"60, 60, 1.0", "30, 60, 0.5", "0, 60, 0.0", "15, 60, 0.25"})
  void theFractionIsRemainingOverTheWholeDelay(
      final int remainingTicks, final int delayTicks, final float expected) {
    assertEquals(expected, CountdownReadout.fractionLeft(remainingTicks, delayTicks));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1})
  void aFuseWithNoDelayHasNothingLeftToShow(final int delayTicks) {
    assertEquals(0f, CountdownReadout.fractionLeft(30, delayTicks));
  }

  @Test
  void aRemainderLargerThanTheDelayIsClampedToFull() {
    assertEquals(1.0f, CountdownReadout.fractionLeft(120, 60));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 10, 20, 30})
  void theLastSecondAndAHalfIsUrgent(final int remainingTicks) {
    assertTrue(CountdownReadout.isUrgent(remainingTicks));
  }

  @ParameterizedTest
  @ValueSource(ints = {31, 60, 200})
  void anythingLongerThanThatIsNotUrgent(final int remainingTicks) {
    assertFalse(CountdownReadout.isUrgent(remainingTicks));
  }

  @ParameterizedTest
  @CsvSource({"60, 60, 60, 60", "30, 60, 60, 30", "0, 60, 60, 0", "15, 60, 60, 15"})
  void theBarFillsInProportionToWhatIsLeft(
      final int remainingTicks, final int delayTicks, final int fullWidth, final int expected) {
    assertEquals(expected, CountdownReadout.barWidth(remainingTicks, delayTicks, fullWidth));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -10})
  void aBarWithNoWidthDrawsNothing(final int fullWidth) {
    assertEquals(0, CountdownReadout.barWidth(30, 60, fullWidth));
  }
}
