package com.grahambartley.notenougharrows.grapple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class GrappleProgressTest {
  private static final double STARTING_DISTANCE = 20.0;
  private static final double TOLERANCE = 1.0e-6;

  @Test
  void aPullStartsAtTheDistanceItSetOutToCover() {
    final GrappleProgress progress = GrappleProgress.startingAt(STARTING_DISTANCE);

    assertEquals(STARTING_DISTANCE, progress.closestApproach(), TOLERANCE);
    assertEquals(0, progress.idleTicks());
  }

  @ParameterizedTest
  @ValueSource(doubles = {-1.0, -20.0})
  void aPullCanNeverBeALessThanZeroDistanceAway(final double distance) {
    assertEquals(0.0, GrappleProgress.startingAt(distance).closestApproach(), TOLERANCE);
  }

  @Test
  void closingOnTheAnchorMovesTheMarkAndClearsTheIdleCount() {
    final GrappleProgress closed =
        GrappleProgress.startingAt(STARTING_DISTANCE).closedTo(STARTING_DISTANCE).closedTo(18.0);

    assertEquals(18.0, closed.closestApproach(), TOLERANCE);
    assertEquals(0, closed.idleTicks());
  }

  @ParameterizedTest
  @CsvSource({"20.0", "19.96", "24.0"})
  void aTickThatFailsToCloseMeaningfullyCountsAsIdle(final double distance) {
    final GrappleProgress idled = GrappleProgress.startingAt(STARTING_DISTANCE).closedTo(distance);

    assertEquals(STARTING_DISTANCE, idled.closestApproach(), TOLERANCE);
    assertEquals(1, idled.idleTicks());
  }

  @Test
  void driftingBackOutNeverLoosensTheMarkAPullAlreadyReached() {
    final GrappleProgress drifted =
        GrappleProgress.startingAt(STARTING_DISTANCE).closedTo(10.0).closedTo(40.0);

    assertEquals(10.0, drifted.closestApproach(), TOLERANCE);
  }

  @Test
  void aPullStoppedForTheWholeIdleWindowHasStopped() {
    GrappleProgress progress = GrappleProgress.startingAt(STARTING_DISTANCE);
    for (int tick = 0; tick < GrappleProgress.IDLE_TICKS_LIMIT - 1; tick++) {
      progress = progress.closedTo(STARTING_DISTANCE);
      assertFalse(progress.hasStopped(), "A pull should get the whole window before it is stopped");
    }

    assertTrue(progress.closedTo(STARTING_DISTANCE).hasStopped());
  }

  @Test
  void oneTickOfRealClosingBuysTheWholeWindowBack() {
    GrappleProgress progress = GrappleProgress.startingAt(STARTING_DISTANCE);
    for (int tick = 0; tick < GrappleProgress.IDLE_TICKS_LIMIT; tick++) {
      progress = progress.closedTo(STARTING_DISTANCE);
    }

    assertFalse(progress.closedTo(STARTING_DISTANCE - 1.0).hasStopped());
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, -20})
  void aPullCanNeverHaveIdledForLessThanNoTicks(final int idleTicks) {
    assertEquals(0, new GrappleProgress(STARTING_DISTANCE, idleTicks).idleTicks());
  }
}
