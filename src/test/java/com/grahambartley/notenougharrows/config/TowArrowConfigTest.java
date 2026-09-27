package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TowArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final TowArrowConfig defaults = TowArrowConfig.defaults();

    assertEquals(TowArrowConfig.DEFAULT_RANGE_BLOCKS, defaults.rangeBlocks());
    assertEquals(TowArrowConfig.DEFAULT_MAX_TICKS, defaults.maxTicks());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final TowArrowConfig defaults = TowArrowConfig.defaults();

    assertEquals(defaults, new TowArrowConfig(defaults.rangeBlocks(), defaults.maxTicks()));
  }

  @ParameterizedTest
  @CsvSource({"0, 1", "1, 1", "128, 128", "129, 128"})
  void clampsRangeBlocks(final int given, final int expected) {
    assertEquals(expected, TowArrowConfig.defaults().withRangeBlocks(given).rangeBlocks());
  }

  @ParameterizedTest
  @CsvSource({"0, 1", "1, 1", "1200, 1200", "1201, 1200"})
  void clampsMaxTicks(final int given, final int expected) {
    assertEquals(expected, TowArrowConfig.defaults().withMaxTicks(given).maxTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(TowArrowConfig.defaults(), TowArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(TowArrowConfig.defaults(), TowArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final TowArrowConfig parsed =
        TowArrowConfig.fromJson(JsonParser.parseString("{\"rangeBlocks\":127}").getAsJsonObject());

    assertEquals(127, parsed.rangeBlocks());
    assertEquals(TowArrowConfig.DEFAULT_MAX_TICKS, parsed.maxTicks());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final TowArrowConfig parsed =
        TowArrowConfig.fromJson(JsonParser.parseString("{\"rangeBlocks\":228}").getAsJsonObject());

    assertEquals(TowArrowConfig.RANGE_BLOCKS_MAX, parsed.rangeBlocks());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final TowArrowConfig parsed =
        TowArrowConfig.fromJson(
            JsonParser.parseString("{\"rangeBlocks\":\"lots\"}").getAsJsonObject());

    assertEquals(TowArrowConfig.DEFAULT_RANGE_BLOCKS, parsed.rangeBlocks());
  }

  @Test
  void roundTripsThroughJson() {
    final TowArrowConfig original = new TowArrowConfig(127, 1199);

    assertEquals(original, TowArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final TowArrowConfig original = TowArrowConfig.defaults();

    final TowArrowConfig updated = original.withRangeBlocks(127);

    assertEquals(127, updated.rangeBlocks());
    assertEquals(original.maxTicks(), updated.maxTicks());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final TowArrowConfig updated =
        TowArrowConfig.defaults().withRangeBlocks(127).withMaxTicks(1199);

    assertEquals(new TowArrowConfig(127, 1199), updated);
  }
}
