package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LevitationArrowConfigTest {

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "60, 60", "1200, 1200", "99999, 1200"})
  void clampsDuration(final int given, final int expected) {
    assertEquals(expected, new LevitationArrowConfig(given).durationTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(
        LevitationArrowConfig.defaults(), LevitationArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void readsADurationFromJson() {
    assertEquals(
        40,
        LevitationArrowConfig.fromJson(
                JsonParser.parseString("{\"durationTicks\":40}").getAsJsonObject())
            .durationTicks());
  }

  @Test
  void roundTripsThroughJson() {
    final LevitationArrowConfig original = new LevitationArrowConfig(123);

    assertEquals(original, LevitationArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingTheValueReturnsANewRecord() {
    assertEquals(9, LevitationArrowConfig.defaults().withDurationTicks(9).durationTicks());
  }
}
