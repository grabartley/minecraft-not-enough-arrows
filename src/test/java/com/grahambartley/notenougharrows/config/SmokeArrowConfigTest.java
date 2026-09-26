package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SmokeArrowConfigTest {

  @ParameterizedTest
  @CsvSource({"-1.0, 0.0", "0.0, 0.0", "3.0, 3.0", "16.0, 16.0", "99.0, 16.0"})
  void clampsRadius(final float given, final float expected) {
    assertEquals(expected, new SmokeArrowConfig(given, 200).radius());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "200, 200", "6000, 6000", "99999, 6000"})
  void clampsDuration(final int given, final int expected) {
    assertEquals(expected, new SmokeArrowConfig(3.0f, given).durationTicks());
  }

  @Test
  void cloudsOnlyWithBothAReachAndADuration() {
    assertTrue(new SmokeArrowConfig(3.0f, 200).clouds());
    assertFalse(new SmokeArrowConfig(0.0f, 200).clouds());
    assertFalse(new SmokeArrowConfig(3.0f, 0).clouds());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(SmokeArrowConfig.defaults(), SmokeArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final SmokeArrowConfig parsed =
        SmokeArrowConfig.fromJson(JsonParser.parseString("{\"radius\":5.0}").getAsJsonObject());

    assertEquals(5.0f, parsed.radius());
    assertEquals(SmokeArrowConfig.DEFAULT_DURATION_TICKS, parsed.durationTicks());
  }

  @Test
  void roundTripsThroughJson() {
    final SmokeArrowConfig original = new SmokeArrowConfig(4.5f, 321);

    assertEquals(original, SmokeArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestAlone() {
    final SmokeArrowConfig updated = SmokeArrowConfig.defaults().withDurationTicks(7);

    assertEquals(7, updated.durationTicks());
    assertEquals(SmokeArrowConfig.DEFAULT_RADIUS, updated.radius());
  }
}
