package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MagnetArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final MagnetArrowConfig defaults = MagnetArrowConfig.defaults();

    assertEquals(MagnetArrowConfig.DEFAULT_RADIUS, defaults.radius());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final MagnetArrowConfig defaults = MagnetArrowConfig.defaults();

    assertEquals(defaults, new MagnetArrowConfig(defaults.radius()));
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "16, 16", "17, 16"})
  void clampsRadius(final int given, final int expected) {
    assertEquals(expected, MagnetArrowConfig.defaults().withRadius(given).radius());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(MagnetArrowConfig.defaults(), MagnetArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(MagnetArrowConfig.defaults(), MagnetArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final MagnetArrowConfig parsed =
        MagnetArrowConfig.fromJson(JsonParser.parseString("{\"radius\":15}").getAsJsonObject());

    assertEquals(15, parsed.radius());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final MagnetArrowConfig parsed =
        MagnetArrowConfig.fromJson(JsonParser.parseString("{\"radius\":116}").getAsJsonObject());

    assertEquals(MagnetArrowConfig.RADIUS_MAX, parsed.radius());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final MagnetArrowConfig parsed =
        MagnetArrowConfig.fromJson(
            JsonParser.parseString("{\"radius\":\"lots\"}").getAsJsonObject());

    assertEquals(MagnetArrowConfig.DEFAULT_RADIUS, parsed.radius());
  }

  @Test
  void roundTripsThroughJson() {
    final MagnetArrowConfig original = new MagnetArrowConfig(15);

    assertEquals(original, MagnetArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final MagnetArrowConfig original = MagnetArrowConfig.defaults();

    final MagnetArrowConfig updated = original.withRadius(15);

    assertEquals(15, updated.radius());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final MagnetArrowConfig updated = MagnetArrowConfig.defaults().withRadius(15);

    assertEquals(new MagnetArrowConfig(15), updated);
  }
}
