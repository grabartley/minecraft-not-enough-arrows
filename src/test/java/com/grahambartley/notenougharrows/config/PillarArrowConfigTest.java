package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PillarArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final PillarArrowConfig defaults = PillarArrowConfig.defaults();

    assertEquals(PillarArrowConfig.DEFAULT_ENABLED, defaults.enabled());
    assertEquals(PillarArrowConfig.DEFAULT_HEIGHT_BLOCKS, defaults.heightBlocks());
    assertEquals(PillarArrowConfig.DEFAULT_LIFETIME_TICKS, defaults.lifetimeTicks());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final PillarArrowConfig defaults = PillarArrowConfig.defaults();

    assertEquals(
        defaults,
        new PillarArrowConfig(
            defaults.enabled(), defaults.heightBlocks(), defaults.lifetimeTicks()));
  }

  @ParameterizedTest
  @CsvSource({"0, 1", "1, 1", "16, 16", "17, 16"})
  void clampsHeightBlocks(final int given, final int expected) {
    assertEquals(expected, PillarArrowConfig.defaults().withHeightBlocks(given).heightBlocks());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "12000, 12000", "12001, 12000"})
  void clampsLifetimeTicks(final int given, final int expected) {
    assertEquals(expected, PillarArrowConfig.defaults().withLifetimeTicks(given).lifetimeTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(PillarArrowConfig.defaults(), PillarArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(PillarArrowConfig.defaults(), PillarArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final PillarArrowConfig parsed =
        PillarArrowConfig.fromJson(
            JsonParser.parseString("{\"heightBlocks\":15}").getAsJsonObject());

    assertEquals(15, parsed.heightBlocks());
    assertEquals(PillarArrowConfig.DEFAULT_ENABLED, parsed.enabled());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final PillarArrowConfig parsed =
        PillarArrowConfig.fromJson(
            JsonParser.parseString("{\"heightBlocks\":116}").getAsJsonObject());

    assertEquals(PillarArrowConfig.HEIGHT_BLOCKS_MAX, parsed.heightBlocks());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final PillarArrowConfig parsed =
        PillarArrowConfig.fromJson(
            JsonParser.parseString("{\"heightBlocks\":\"lots\"}").getAsJsonObject());

    assertEquals(PillarArrowConfig.DEFAULT_HEIGHT_BLOCKS, parsed.heightBlocks());
  }

  @Test
  void roundTripsThroughJson() {
    final PillarArrowConfig original = new PillarArrowConfig(false, 15, 11999);

    assertEquals(original, PillarArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final PillarArrowConfig original = PillarArrowConfig.defaults();

    final PillarArrowConfig updated = original.withEnabled(false);

    assertEquals(false, updated.enabled());
    assertEquals(original.heightBlocks(), updated.heightBlocks());
    assertEquals(original.lifetimeTicks(), updated.lifetimeTicks());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final PillarArrowConfig updated =
        PillarArrowConfig.defaults()
            .withEnabled(false)
            .withHeightBlocks(15)
            .withLifetimeTicks(11999);

    assertEquals(new PillarArrowConfig(false, 15, 11999), updated);
  }
}
