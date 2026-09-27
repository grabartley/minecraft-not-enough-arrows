package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ScaffoldArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final ScaffoldArrowConfig defaults = ScaffoldArrowConfig.defaults();

    assertEquals(ScaffoldArrowConfig.DEFAULT_HEIGHT_BLOCKS, defaults.heightBlocks());
    assertEquals(ScaffoldArrowConfig.DEFAULT_LIFETIME_TICKS, defaults.lifetimeTicks());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final ScaffoldArrowConfig defaults = ScaffoldArrowConfig.defaults();

    assertEquals(
        defaults, new ScaffoldArrowConfig(defaults.heightBlocks(), defaults.lifetimeTicks()));
  }

  @ParameterizedTest
  @CsvSource({"0, 1", "1, 1", "64, 64", "65, 64"})
  void clampsHeightBlocks(final int given, final int expected) {
    assertEquals(expected, ScaffoldArrowConfig.defaults().withHeightBlocks(given).heightBlocks());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "12000, 12000", "12001, 12000"})
  void clampsLifetimeTicks(final int given, final int expected) {
    assertEquals(expected, ScaffoldArrowConfig.defaults().withLifetimeTicks(given).lifetimeTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(ScaffoldArrowConfig.defaults(), ScaffoldArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(ScaffoldArrowConfig.defaults(), ScaffoldArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final ScaffoldArrowConfig parsed =
        ScaffoldArrowConfig.fromJson(
            JsonParser.parseString("{\"heightBlocks\":63}").getAsJsonObject());

    assertEquals(63, parsed.heightBlocks());
    assertEquals(ScaffoldArrowConfig.DEFAULT_LIFETIME_TICKS, parsed.lifetimeTicks());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final ScaffoldArrowConfig parsed =
        ScaffoldArrowConfig.fromJson(
            JsonParser.parseString("{\"heightBlocks\":164}").getAsJsonObject());

    assertEquals(ScaffoldArrowConfig.HEIGHT_BLOCKS_MAX, parsed.heightBlocks());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final ScaffoldArrowConfig parsed =
        ScaffoldArrowConfig.fromJson(
            JsonParser.parseString("{\"heightBlocks\":\"lots\"}").getAsJsonObject());

    assertEquals(ScaffoldArrowConfig.DEFAULT_HEIGHT_BLOCKS, parsed.heightBlocks());
  }

  @Test
  void roundTripsThroughJson() {
    final ScaffoldArrowConfig original = new ScaffoldArrowConfig(63, 11999);

    assertEquals(original, ScaffoldArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final ScaffoldArrowConfig original = ScaffoldArrowConfig.defaults();

    final ScaffoldArrowConfig updated = original.withHeightBlocks(63);

    assertEquals(63, updated.heightBlocks());
    assertEquals(original.lifetimeTicks(), updated.lifetimeTicks());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final ScaffoldArrowConfig updated =
        ScaffoldArrowConfig.defaults().withHeightBlocks(63).withLifetimeTicks(11999);

    assertEquals(new ScaffoldArrowConfig(63, 11999), updated);
  }
}
