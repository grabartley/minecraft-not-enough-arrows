package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class BlossomArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final BlossomArrowConfig defaults = BlossomArrowConfig.defaults();

    assertEquals(BlossomArrowConfig.DEFAULT_RADIUS, defaults.radius());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final BlossomArrowConfig defaults = BlossomArrowConfig.defaults();

    assertEquals(defaults, new BlossomArrowConfig(defaults.radius()));
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "8, 8", "9, 8"})
  void clampsRadius(final int given, final int expected) {
    assertEquals(expected, BlossomArrowConfig.defaults().withRadius(given).radius());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(BlossomArrowConfig.defaults(), BlossomArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(BlossomArrowConfig.defaults(), BlossomArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final BlossomArrowConfig parsed =
        BlossomArrowConfig.fromJson(JsonParser.parseString("{\"radius\":7}").getAsJsonObject());

    assertEquals(7, parsed.radius());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final BlossomArrowConfig parsed =
        BlossomArrowConfig.fromJson(JsonParser.parseString("{\"radius\":108}").getAsJsonObject());

    assertEquals(BlossomArrowConfig.RADIUS_MAX, parsed.radius());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final BlossomArrowConfig parsed =
        BlossomArrowConfig.fromJson(
            JsonParser.parseString("{\"radius\":\"lots\"}").getAsJsonObject());

    assertEquals(BlossomArrowConfig.DEFAULT_RADIUS, parsed.radius());
  }

  @Test
  void roundTripsThroughJson() {
    final BlossomArrowConfig original = new BlossomArrowConfig(7);

    assertEquals(original, BlossomArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final BlossomArrowConfig original = BlossomArrowConfig.defaults();

    final BlossomArrowConfig updated = original.withRadius(7);

    assertEquals(7, updated.radius());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final BlossomArrowConfig updated = BlossomArrowConfig.defaults().withRadius(7);

    assertEquals(new BlossomArrowConfig(7), updated);
  }
}
