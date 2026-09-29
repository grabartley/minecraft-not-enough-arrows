package com.grahambartley.notenougharrows.reveal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CompassPointTest {

  @ParameterizedTest
  @CsvSource({
    "0, -10, NORTH",
    "0, 10, SOUTH",
    "10, 0, EAST",
    "-10, 0, WEST",
    "10, -10, NORTH_EAST",
    "-10, -10, NORTH_WEST",
    "10, 10, SOUTH_EAST",
    "-10, 10, SOUTH_WEST",
    "3, -40, NORTH",
    "40, -3, EAST",
    "0, 0, SOUTH"
  })
  void pointsTheWayMinecraftsCompassDoes(
      final double deltaX, final double deltaZ, final CompassPoint expected) {
    assertEquals(expected, CompassPoint.toward(deltaX, deltaZ));
  }

  @ParameterizedTest
  @CsvSource({
    "NORTH, direction.not-enough-arrows.north",
    "SOUTH_WEST, direction.not-enough-arrows.south_west"
  })
  void namesItselfThroughATranslationKey(final CompassPoint point, final String key) {
    assertEquals(key, point.translationKey());
  }
}
