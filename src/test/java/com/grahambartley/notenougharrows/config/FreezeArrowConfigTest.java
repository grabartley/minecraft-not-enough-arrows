package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FreezeArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final FreezeArrowConfig defaults = FreezeArrowConfig.defaults();

    assertEquals(FreezeArrowConfig.DEFAULT_ENABLED, defaults.enabled());
    assertEquals(FreezeArrowConfig.DEFAULT_RADIUS, defaults.radius());
    assertEquals(FreezeArrowConfig.DEFAULT_MAX_BLOCKS, defaults.maxBlocks());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final FreezeArrowConfig defaults = FreezeArrowConfig.defaults();

    assertEquals(
        defaults,
        new FreezeArrowConfig(defaults.enabled(), defaults.radius(), defaults.maxBlocks()));
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "8, 8", "9, 8"})
  void clampsRadius(final int given, final int expected) {
    assertEquals(expected, FreezeArrowConfig.defaults().withRadius(given).radius());
  }

  @ParameterizedTest
  @CsvSource({"0, 1", "1, 1", "512, 512", "513, 512"})
  void clampsMaxBlocks(final int given, final int expected) {
    assertEquals(expected, FreezeArrowConfig.defaults().withMaxBlocks(given).maxBlocks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(FreezeArrowConfig.defaults(), FreezeArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(FreezeArrowConfig.defaults(), FreezeArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final FreezeArrowConfig parsed =
        FreezeArrowConfig.fromJson(JsonParser.parseString("{\"radius\":7}").getAsJsonObject());

    assertEquals(7, parsed.radius());
    assertEquals(FreezeArrowConfig.DEFAULT_ENABLED, parsed.enabled());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final FreezeArrowConfig parsed =
        FreezeArrowConfig.fromJson(JsonParser.parseString("{\"radius\":108}").getAsJsonObject());

    assertEquals(FreezeArrowConfig.RADIUS_MAX, parsed.radius());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final FreezeArrowConfig parsed =
        FreezeArrowConfig.fromJson(
            JsonParser.parseString("{\"radius\":\"lots\"}").getAsJsonObject());

    assertEquals(FreezeArrowConfig.DEFAULT_RADIUS, parsed.radius());
  }

  @Test
  void roundTripsThroughJson() {
    final FreezeArrowConfig original = new FreezeArrowConfig(false, 7, 511);

    assertEquals(original, FreezeArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final FreezeArrowConfig original = FreezeArrowConfig.defaults();

    final FreezeArrowConfig updated = original.withEnabled(false);

    assertEquals(false, updated.enabled());
    assertEquals(original.radius(), updated.radius());
    assertEquals(original.maxBlocks(), updated.maxBlocks());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final FreezeArrowConfig updated =
        FreezeArrowConfig.defaults().withEnabled(false).withRadius(7).withMaxBlocks(511);

    assertEquals(new FreezeArrowConfig(false, 7, 511), updated);
  }
}
