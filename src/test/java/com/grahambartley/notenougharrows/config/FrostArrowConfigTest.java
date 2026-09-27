package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FrostArrowConfigTest {

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "200, 200", "1200, 1200", "99999, 1200"})
  void clampsDuration(final int given, final int expected) {
    assertEquals(expected, new FrostArrowConfig(given).durationTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(FrostArrowConfig.defaults(), FrostArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAHandEditedFile() {
    assertEquals(
        FrostArrowConfig.DURATION_TICKS_MAX,
        FrostArrowConfig.fromJson(
                JsonParser.parseString("{\"durationTicks\":50000}").getAsJsonObject())
            .durationTicks());
  }

  @Test
  void roundTripsThroughJson() {
    final FrostArrowConfig original = new FrostArrowConfig(77);

    assertEquals(original, FrostArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingTheValueReturnsANewRecord() {
    assertEquals(11, FrostArrowConfig.defaults().withDurationTicks(11).durationTicks());
  }
}
