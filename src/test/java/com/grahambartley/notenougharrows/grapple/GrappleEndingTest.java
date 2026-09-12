package com.grahambartley.notenougharrows.grapple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

class GrappleEndingTest {

  @ParameterizedTest
  @CsvSource({
    "ARRIVED, true",
    "OBSTRUCTED, true",
    "OUT_OF_TIME, true",
    "CANCELLED, true",
    "ANCHOR_LOST, false",
    "SHOOTER_GONE, false"
  })
  void anEndingKnowsWhetherTheModPutThePlayerWhereTheyAre(
      final GrappleEnding ending, final boolean ownsTheFall) {
    assertEquals(ownsTheFall, ending.ownsTheFall());
  }

  @ParameterizedTest
  @EnumSource(
      value = GrappleEnding.class,
      names = {"ARRIVED"},
      mode = EnumSource.Mode.EXCLUDE)
  void onlyReachingTheAnchorHandsTheArrowBack(final GrappleEnding ending) {
    assertFalse(ending.returnsTheArrow());
  }

  @Test
  void reachingTheAnchorHandsTheArrowBack() {
    assertTrue(GrappleEnding.ARRIVED.returnsTheArrow());
  }

  @Test
  void everyEndingThatHandsTheArrowBackAlsoOwnsTheFallItCaused() {
    assertTrue(
        Arrays.stream(GrappleEnding.values())
            .filter(GrappleEnding::returnsTheArrow)
            .allMatch(GrappleEnding::ownsTheFall),
        "An ending that pays the arrow back has carried the player, so it owns their landing");
  }
}
