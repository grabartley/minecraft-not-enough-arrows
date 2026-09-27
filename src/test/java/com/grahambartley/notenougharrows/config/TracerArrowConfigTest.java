package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TracerArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final TracerArrowConfig defaults = TracerArrowConfig.defaults();

    assertEquals(TracerArrowConfig.DEFAULT_PATH_LIFETIME_TICKS, defaults.pathLifetimeTicks());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final TracerArrowConfig defaults = TracerArrowConfig.defaults();

    assertEquals(defaults, new TracerArrowConfig(defaults.pathLifetimeTicks()));
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "1200, 1200", "1201, 1200"})
  void clampsPathLifetimeTicks(final int given, final int expected) {
    assertEquals(
        expected, TracerArrowConfig.defaults().withPathLifetimeTicks(given).pathLifetimeTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(TracerArrowConfig.defaults(), TracerArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(TracerArrowConfig.defaults(), TracerArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final TracerArrowConfig parsed =
        TracerArrowConfig.fromJson(
            JsonParser.parseString("{\"pathLifetimeTicks\":1199}").getAsJsonObject());

    assertEquals(1199, parsed.pathLifetimeTicks());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final TracerArrowConfig parsed =
        TracerArrowConfig.fromJson(
            JsonParser.parseString("{\"pathLifetimeTicks\":1300}").getAsJsonObject());

    assertEquals(TracerArrowConfig.PATH_LIFETIME_TICKS_MAX, parsed.pathLifetimeTicks());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final TracerArrowConfig parsed =
        TracerArrowConfig.fromJson(
            JsonParser.parseString("{\"pathLifetimeTicks\":\"lots\"}").getAsJsonObject());

    assertEquals(TracerArrowConfig.DEFAULT_PATH_LIFETIME_TICKS, parsed.pathLifetimeTicks());
  }

  @Test
  void roundTripsThroughJson() {
    final TracerArrowConfig original = new TracerArrowConfig(1199);

    assertEquals(original, TracerArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final TracerArrowConfig original = TracerArrowConfig.defaults();

    final TracerArrowConfig updated = original.withPathLifetimeTicks(1199);

    assertEquals(1199, updated.pathLifetimeTicks());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final TracerArrowConfig updated = TracerArrowConfig.defaults().withPathLifetimeTicks(1199);

    assertEquals(new TracerArrowConfig(1199), updated);
  }
}
