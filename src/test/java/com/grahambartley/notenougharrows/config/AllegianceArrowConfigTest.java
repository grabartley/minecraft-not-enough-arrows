package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AllegianceArrowConfigTest {

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "400, 400", "6000, 6000", "99999, 6000"})
  void clampsDuration(final int given, final int expected) {
    assertEquals(expected, new AllegianceArrowConfig(given, 16.0f).durationTicks());
  }

  @ParameterizedTest
  @CsvSource({"-1.0, 0.0", "0.0, 0.0", "16.0, 16.0", "32.0, 32.0", "99.0, 32.0"})
  void clampsDefendRadius(final float given, final float expected) {
    assertEquals(expected, new AllegianceArrowConfig(400, given).defendRadius());
  }

  @Test
  void turnsNobodyAtZeroDuration() {
    assertFalse(new AllegianceArrowConfig(0, 16.0f).turns());
  }

  @Test
  void turnsWhenADurationIsConfigured() {
    assertTrue(new AllegianceArrowConfig(1, 16.0f).turns());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(
        AllegianceArrowConfig.defaults(), AllegianceArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final AllegianceArrowConfig parsed =
        AllegianceArrowConfig.fromJson(
            JsonParser.parseString("{\"durationTicks\":40}").getAsJsonObject());

    assertEquals(40, parsed.durationTicks());
    assertEquals(AllegianceArrowConfig.DEFAULT_DEFEND_RADIUS, parsed.defendRadius());
  }

  @Test
  void roundTripsThroughJson() {
    final AllegianceArrowConfig original = new AllegianceArrowConfig(123, 7.5f);

    assertEquals(original, AllegianceArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestAlone() {
    final AllegianceArrowConfig updated = AllegianceArrowConfig.defaults().withDefendRadius(3.0f);

    assertEquals(3.0f, updated.defendRadius());
    assertEquals(AllegianceArrowConfig.DEFAULT_DURATION_TICKS, updated.durationTicks());
  }
}
