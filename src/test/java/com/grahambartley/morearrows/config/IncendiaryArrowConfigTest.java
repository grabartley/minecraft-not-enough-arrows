package com.grahambartley.morearrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class IncendiaryArrowConfigTest {

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "3, 3", "8, 8", "99, 8"})
  void clampsBurnRadius(final int given, final int expected) {
    assertEquals(expected, new IncendiaryArrowConfig(given, 5, true).burnRadius());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "5, 5", "60, 60", "999, 60"})
  void clampsIgniteSeconds(final int given, final int expected) {
    assertEquals(expected, new IncendiaryArrowConfig(3, given, true).igniteSeconds());
  }

  @Test
  void defaultsBurnAndSetBlocksAlight() {
    final IncendiaryArrowConfig defaults = IncendiaryArrowConfig.defaults();

    assertTrue(defaults.burnRadius() > 0);
    assertTrue(defaults.igniteSeconds() > 0);
    assertTrue(defaults.ignitesBlocks());
  }

  @Test
  void changingOneFieldLeavesTheOthersAlone() {
    final IncendiaryArrowConfig off = IncendiaryArrowConfig.defaults().withIgnitesBlocks(false);

    assertFalse(off.ignitesBlocks());
    assertEquals(IncendiaryArrowConfig.DEFAULT_BURN_RADIUS, off.burnRadius());
    assertEquals(IncendiaryArrowConfig.DEFAULT_IGNITE_SECONDS, off.igniteSeconds());
  }

  @Test
  void roundTripsThroughJson() {
    final IncendiaryArrowConfig original = new IncendiaryArrowConfig(6, 37, false);

    assertEquals(
        original,
        IncendiaryArrowConfig.fromJson(original.toJson(), IncendiaryArrowConfig.defaults()));
  }

  @Test
  void aMissingFieldFallsBackToTheDefaultItWasGiven() {
    final IncendiaryArrowConfig fallback = new IncendiaryArrowConfig(7, 11, false);

    assertEquals(fallback, IncendiaryArrowConfig.fromJson(new JsonObject(), fallback));
  }
}
