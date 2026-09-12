package com.grahambartley.notenougharrows.grapple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.grahambartley.notenougharrows.config.GrappleArrowConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;

class GrappleArrivalTest {

  @ParameterizedTest
  @CsvSource({
    "ARRIVED, true",
    "OBSTRUCTED, true",
    "OUT_OF_TIME, true",
    "CANCELLED, true",
    "ANCHOR_LOST, false",
    "SHOOTER_GONE, false"
  })
  void aPullThatPutThePlayerWhereTheyAreOwnsTheirLanding(
      final GrappleEnding ending, final boolean cancelled) {
    assertEquals(cancelled, GrappleArrival.cancelsFallDamage(ending, configCancellingFalls(true)));
  }

  @ParameterizedTest
  @EnumSource(GrappleEnding.class)
  void anOperatorWhoTurnsFallCancellationOffTakesItForEveryEnding(final GrappleEnding ending) {
    assertFalse(GrappleArrival.cancelsFallDamage(ending, configCancellingFalls(false)));
  }

  @ParameterizedTest
  @CsvSource({
    "ARRIVED, true",
    "OBSTRUCTED, false",
    "OUT_OF_TIME, false",
    "CANCELLED, false",
    "ANCHOR_LOST, false",
    "SHOOTER_GONE, false"
  })
  void onlyAPullThatReachedItsAnchorHandsTheArrowBack(
      final GrappleEnding ending, final boolean returned) {
    assertEquals(returned, GrappleArrival.returnsArrow(ending, configReturningArrows(true)));
  }

  @ParameterizedTest
  @EnumSource(GrappleEnding.class)
  void anOperatorWhoTurnsArrowReturnOffTakesItForEveryEnding(final GrappleEnding ending) {
    assertFalse(GrappleArrival.returnsArrow(ending, configReturningArrows(false)));
  }

  @ParameterizedTest
  @NullSource
  void anEndingNobodyNamedSettlesNothing(final GrappleEnding ending) {
    assertFalse(GrappleArrival.cancelsFallDamage(ending, configCancellingFalls(true)));
    assertFalse(GrappleArrival.returnsArrow(ending, configReturningArrows(true)));
  }

  @Test
  void aSettlementWithNoConfigBehindItSettlesNothing() {
    assertFalse(GrappleArrival.cancelsFallDamage(GrappleEnding.ARRIVED, null));
    assertFalse(GrappleArrival.returnsArrow(GrappleEnding.ARRIVED, null));
  }

  private static GrappleArrowConfig configCancellingFalls(final boolean cancels) {
    return GrappleArrowConfig.defaults().withCancelFallDamageOnArrival(cancels);
  }

  private static GrappleArrowConfig configReturningArrows(final boolean returns) {
    return GrappleArrowConfig.defaults().withReturnArrowOnArrival(returns);
  }
}
