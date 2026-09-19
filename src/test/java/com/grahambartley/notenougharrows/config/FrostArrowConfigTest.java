package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FrostArrowConfigTest {

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "140, 140", "1200, 1200", "99999, 1200"})
  void clampsFreezeTicksPerHit(final int given, final int expected) {
    assertEquals(expected, new FrostArrowConfig(given).freezeTicksPerHit());
  }

  @Test
  void buildsNothingAtZero() {
    assertFalse(new FrostArrowConfig(0).builds());
  }

  @Test
  void buildsWhenAnyTicksAreConfigured() {
    assertTrue(new FrostArrowConfig(1).builds());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(FrostArrowConfig.defaults(), FrostArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAHandEditedFile() {
    assertEquals(
        FrostArrowConfig.FREEZE_TICKS_PER_HIT_MAX,
        FrostArrowConfig.fromJson(
                JsonParser.parseString("{\"freezeTicksPerHit\":50000}").getAsJsonObject())
            .freezeTicksPerHit());
  }

  @Test
  void roundTripsThroughJson() {
    final FrostArrowConfig original = new FrostArrowConfig(77);

    assertEquals(original, FrostArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingTheValueReturnsANewRecord() {
    assertEquals(11, FrostArrowConfig.defaults().withFreezeTicksPerHit(11).freezeTicksPerHit());
  }
}
