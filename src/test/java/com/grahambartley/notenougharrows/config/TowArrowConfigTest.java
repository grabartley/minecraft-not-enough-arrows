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
    assertEquals(TowArrowConfig.DEFAULT_SPEED, defaults.speed());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final TowArrowConfig defaults = TowArrowConfig.defaults();

    assertEquals(
        defaults,
        new TowArrowConfig(defaults.rangeBlocks(), defaults.maxTicks(), defaults.speed()));
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

  @ParameterizedTest
  @CsvSource({"0.0, 0.1", "0.1, 0.1", "1.5, 1.5", "1.6, 1.5"})
  void clampsSpeed(final float given, final float expected) {
    assertEquals(expected, TowArrowConfig.defaults().withSpeed(given).speed());
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
    final TowArrowConfig original = new TowArrowConfig(127, 1199, 1.25f);

    assertEquals(original, TowArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final TowArrowConfig original = TowArrowConfig.defaults();

    final TowArrowConfig updated = original.withRangeBlocks(127);

    assertEquals(127, updated.rangeBlocks());
    assertEquals(original.maxTicks(), updated.maxTicks());
    assertEquals(original.speed(), updated.speed());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final TowArrowConfig updated =
        TowArrowConfig.defaults().withRangeBlocks(127).withMaxTicks(1199).withSpeed(1.25f);

    assertEquals(new TowArrowConfig(127, 1199, 1.25f), updated);
  }
}
