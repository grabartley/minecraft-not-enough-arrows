package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DrainArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final DrainArrowConfig defaults = DrainArrowConfig.defaults();

    assertEquals(DrainArrowConfig.DEFAULT_ENABLED, defaults.enabled());
    assertEquals(DrainArrowConfig.DEFAULT_RADIUS, defaults.radius());
    assertEquals(DrainArrowConfig.DEFAULT_MAX_BLOCKS, defaults.maxBlocks());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final DrainArrowConfig defaults = DrainArrowConfig.defaults();

    assertEquals(
        defaults,
        new DrainArrowConfig(defaults.enabled(), defaults.radius(), defaults.maxBlocks()));
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "8, 8", "9, 8"})
  void clampsRadius(final int given, final int expected) {
    assertEquals(expected, DrainArrowConfig.defaults().withRadius(given).radius());
  }

  @ParameterizedTest
  @CsvSource({"0, 1", "1, 1", "512, 512", "513, 512"})
  void clampsMaxBlocks(final int given, final int expected) {
    assertEquals(expected, DrainArrowConfig.defaults().withMaxBlocks(given).maxBlocks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(DrainArrowConfig.defaults(), DrainArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(DrainArrowConfig.defaults(), DrainArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final DrainArrowConfig parsed =
        DrainArrowConfig.fromJson(JsonParser.parseString("{\"radius\":7}").getAsJsonObject());

    assertEquals(7, parsed.radius());
    assertEquals(DrainArrowConfig.DEFAULT_ENABLED, parsed.enabled());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final DrainArrowConfig parsed =
        DrainArrowConfig.fromJson(JsonParser.parseString("{\"radius\":108}").getAsJsonObject());

    assertEquals(DrainArrowConfig.RADIUS_MAX, parsed.radius());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final DrainArrowConfig parsed =
        DrainArrowConfig.fromJson(
            JsonParser.parseString("{\"radius\":\"lots\"}").getAsJsonObject());

    assertEquals(DrainArrowConfig.DEFAULT_RADIUS, parsed.radius());
  }

  @Test
  void roundTripsThroughJson() {
    final DrainArrowConfig original = new DrainArrowConfig(false, 7, 511);

    assertEquals(original, DrainArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final DrainArrowConfig original = DrainArrowConfig.defaults();

    final DrainArrowConfig updated = original.withEnabled(false);

    assertEquals(false, updated.enabled());
    assertEquals(original.radius(), updated.radius());
    assertEquals(original.maxBlocks(), updated.maxBlocks());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final DrainArrowConfig updated =
        DrainArrowConfig.defaults().withEnabled(false).withRadius(7).withMaxBlocks(511);

    assertEquals(new DrainArrowConfig(false, 7, 511), updated);
  }
}
