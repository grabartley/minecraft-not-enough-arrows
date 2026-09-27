package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TillArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final TillArrowConfig defaults = TillArrowConfig.defaults();

    assertEquals(TillArrowConfig.DEFAULT_RADIUS, defaults.radius());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final TillArrowConfig defaults = TillArrowConfig.defaults();

    assertEquals(defaults, new TillArrowConfig(defaults.radius()));
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "8, 8", "9, 8"})
  void clampsRadius(final int given, final int expected) {
    assertEquals(expected, TillArrowConfig.defaults().withRadius(given).radius());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(TillArrowConfig.defaults(), TillArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(TillArrowConfig.defaults(), TillArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final TillArrowConfig parsed =
        TillArrowConfig.fromJson(JsonParser.parseString("{\"radius\":7}").getAsJsonObject());

    assertEquals(7, parsed.radius());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final TillArrowConfig parsed =
        TillArrowConfig.fromJson(JsonParser.parseString("{\"radius\":108}").getAsJsonObject());

    assertEquals(TillArrowConfig.RADIUS_MAX, parsed.radius());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final TillArrowConfig parsed =
        TillArrowConfig.fromJson(JsonParser.parseString("{\"radius\":\"lots\"}").getAsJsonObject());

    assertEquals(TillArrowConfig.DEFAULT_RADIUS, parsed.radius());
  }

  @Test
  void roundTripsThroughJson() {
    final TillArrowConfig original = new TillArrowConfig(7);

    assertEquals(original, TillArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final TillArrowConfig original = TillArrowConfig.defaults();

    final TillArrowConfig updated = original.withRadius(7);

    assertEquals(7, updated.radius());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final TillArrowConfig updated = TillArrowConfig.defaults().withRadius(7);

    assertEquals(new TillArrowConfig(7), updated);
  }
}
