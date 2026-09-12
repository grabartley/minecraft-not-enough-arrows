package com.grahambartley.notenougharrows.hud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class CountdownArcTest {
  private static final float TOLERANCE = 1.0E-4f;

  @ParameterizedTest
  @CsvSource({"60, 0.0, 60.0", "60, 0.5, 59.5", "60, 1.0, 59.0", "1, 0.5, 0.5"})
  void theTickDeltaIsSubtractedSoTheArcMovesBetweenTicks(
      final int remainingTicks, final float tickDelta, final float expected) {
    assertEquals(expected, CountdownArc.ticksLeft(remainingTicks, tickDelta), TOLERANCE);
  }

  @ParameterizedTest
  @ValueSource(floats = {-1.0f, 2.0f})
  void aTickDeltaOutsideItsRangeIsClamped(final float tickDelta) {
    final float ticks = CountdownArc.ticksLeft(60, tickDelta);

    assertTrue(ticks >= 59.0f && ticks <= 60.0f);
  }

  @Test
  void aCountdownNeverReadsBelowZero() {
    assertEquals(0.0f, CountdownArc.ticksLeft(0, 1.0f), TOLERANCE);
  }

  @ParameterizedTest
  @CsvSource({"60, 60, 0.0, 1.0", "30, 60, 0.0, 0.5", "0, 60, 0.0, 0.0", "60, 60, 0.5, 0.99166"})
  void theFractionShrinksSmoothlyRatherThanInTickSteps(
      final int remainingTicks, final int delayTicks, final float tickDelta, final float expected) {
    assertEquals(
        expected, CountdownArc.fractionLeft(remainingTicks, delayTicks, tickDelta), 1.0E-3f);
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1})
  void aFuseWithNoDelayHasNoArc(final int delayTicks) {
    assertEquals(0.0f, CountdownArc.fractionLeft(30, delayTicks, 0.0f), TOLERANCE);
  }

  @Test
  void aRemainderLongerThanTheDelayIsClampedToAFullCircle() {
    assertEquals(1.0f, CountdownArc.fractionLeft(200, 60, 0.0f), TOLERANCE);
  }

  @Test
  void afullFractionSweepsTheWholeCircle() {
    assertEquals(CountdownArc.FULL_SWEEP, CountdownArc.sweepRadians(1.0f), TOLERANCE);
  }

  @ParameterizedTest
  @ValueSource(floats = {-0.5f, 0.0f})
  void nothingLeftSweepsNothing(final float fraction) {
    assertEquals(0.0f, CountdownArc.sweepRadians(fraction), TOLERANCE);
  }

  @Test
  void aFractionOverOneStillSweepsOnlyOneCircle() {
    assertEquals(CountdownArc.FULL_SWEEP, CountdownArc.sweepRadians(1.5f), TOLERANCE);
  }

  @Test
  void anEmptyArcDrawsNoSegments() {
    assertEquals(0, CountdownArc.segmentsFor(0.0f));
  }

  @Test
  void aFullArcDrawsEverySegment() {
    assertEquals(64, CountdownArc.segmentsFor(CountdownArc.FULL_SWEEP));
  }

  @Test
  void aPartialSegmentStillDrawsSoTheArcNeverJumps() {
    assertEquals(1, CountdownArc.segmentsFor(CountdownArc.SEGMENT_RADIANS / 4.0f));
  }

  @Test
  void theLastSegmentStopsExactlyAtTheSweepRatherThanOvershooting() {
    final float sweep = CountdownArc.SEGMENT_RADIANS * 2.5f;

    assertEquals(sweep, CountdownArc.segmentEnd(2, sweep), TOLERANCE);
  }

  @Test
  void aWholeSegmentEndsOnItsOwnBoundary() {
    final float sweep = CountdownArc.FULL_SWEEP;

    assertEquals(CountdownArc.SEGMENT_RADIANS, CountdownArc.segmentEnd(0, sweep), TOLERANCE);
  }

  @ParameterizedTest
  @CsvSource({"30, 0.0", "10, 0.0", "0, 0.0"})
  void theLastSecondAndAHalfIsUrgent(final int remainingTicks, final float tickDelta) {
    assertTrue(CountdownArc.isUrgent(remainingTicks, tickDelta));
  }

  @ParameterizedTest
  @ValueSource(ints = {31, 60, 200})
  void anythingLongerThanThatIsNotUrgent(final int remainingTicks) {
    assertFalse(CountdownArc.isUrgent(remainingTicks, 0.0f));
  }
}
