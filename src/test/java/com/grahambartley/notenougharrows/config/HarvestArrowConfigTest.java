package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HarvestArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final HarvestArrowConfig defaults = HarvestArrowConfig.defaults();

    assertEquals(HarvestArrowConfig.DEFAULT_RADIUS, defaults.radius());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final HarvestArrowConfig defaults = HarvestArrowConfig.defaults();

    assertEquals(defaults, new HarvestArrowConfig(defaults.radius()));
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "8, 8", "9, 8"})
  void clampsRadius(final int given, final int expected) {
    assertEquals(expected, HarvestArrowConfig.defaults().withRadius(given).radius());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(HarvestArrowConfig.defaults(), HarvestArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(HarvestArrowConfig.defaults(), HarvestArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final HarvestArrowConfig parsed =
        HarvestArrowConfig.fromJson(JsonParser.parseString("{\"radius\":7}").getAsJsonObject());

    assertEquals(7, parsed.radius());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final HarvestArrowConfig parsed =
        HarvestArrowConfig.fromJson(JsonParser.parseString("{\"radius\":108}").getAsJsonObject());

    assertEquals(HarvestArrowConfig.RADIUS_MAX, parsed.radius());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final HarvestArrowConfig parsed =
        HarvestArrowConfig.fromJson(
            JsonParser.parseString("{\"radius\":\"lots\"}").getAsJsonObject());

    assertEquals(HarvestArrowConfig.DEFAULT_RADIUS, parsed.radius());
  }

  @Test
  void roundTripsThroughJson() {
    final HarvestArrowConfig original = new HarvestArrowConfig(7);

    assertEquals(original, HarvestArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final HarvestArrowConfig original = HarvestArrowConfig.defaults();

    final HarvestArrowConfig updated = original.withRadius(7);

    assertEquals(7, updated.radius());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final HarvestArrowConfig updated = HarvestArrowConfig.defaults().withRadius(7);

    assertEquals(new HarvestArrowConfig(7), updated);
  }
}
