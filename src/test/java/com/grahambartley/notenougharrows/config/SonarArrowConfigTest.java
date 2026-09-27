package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SonarArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final SonarArrowConfig defaults = SonarArrowConfig.defaults();

    assertEquals(SonarArrowConfig.DEFAULT_RADIUS, defaults.radius());
    assertEquals(SonarArrowConfig.DEFAULT_DURATION_TICKS, defaults.durationTicks());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final SonarArrowConfig defaults = SonarArrowConfig.defaults();

    assertEquals(defaults, new SonarArrowConfig(defaults.radius(), defaults.durationTicks()));
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "32, 32", "33, 32"})
  void clampsRadius(final int given, final int expected) {
    assertEquals(expected, SonarArrowConfig.defaults().withRadius(given).radius());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "1200, 1200", "1201, 1200"})
  void clampsDurationTicks(final int given, final int expected) {
    assertEquals(expected, SonarArrowConfig.defaults().withDurationTicks(given).durationTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(SonarArrowConfig.defaults(), SonarArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(SonarArrowConfig.defaults(), SonarArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final SonarArrowConfig parsed =
        SonarArrowConfig.fromJson(JsonParser.parseString("{\"radius\":31}").getAsJsonObject());

    assertEquals(31, parsed.radius());
    assertEquals(SonarArrowConfig.DEFAULT_DURATION_TICKS, parsed.durationTicks());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final SonarArrowConfig parsed =
        SonarArrowConfig.fromJson(JsonParser.parseString("{\"radius\":132}").getAsJsonObject());

    assertEquals(SonarArrowConfig.RADIUS_MAX, parsed.radius());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final SonarArrowConfig parsed =
        SonarArrowConfig.fromJson(
            JsonParser.parseString("{\"radius\":\"lots\"}").getAsJsonObject());

    assertEquals(SonarArrowConfig.DEFAULT_RADIUS, parsed.radius());
  }

  @Test
  void roundTripsThroughJson() {
    final SonarArrowConfig original = new SonarArrowConfig(31, 1199);

    assertEquals(original, SonarArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final SonarArrowConfig original = SonarArrowConfig.defaults();

    final SonarArrowConfig updated = original.withRadius(31);

    assertEquals(31, updated.radius());
    assertEquals(original.durationTicks(), updated.durationTicks());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final SonarArrowConfig updated =
        SonarArrowConfig.defaults().withRadius(31).withDurationTicks(1199);

    assertEquals(new SonarArrowConfig(31, 1199), updated);
  }
}
