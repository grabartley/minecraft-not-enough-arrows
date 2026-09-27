package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class UpdraftArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final UpdraftArrowConfig defaults = UpdraftArrowConfig.defaults();

    assertEquals(UpdraftArrowConfig.DEFAULT_HEIGHT_BLOCKS, defaults.heightBlocks());
    assertEquals(UpdraftArrowConfig.DEFAULT_LIFETIME_TICKS, defaults.lifetimeTicks());
    assertEquals(UpdraftArrowConfig.DEFAULT_STRENGTH, defaults.strength());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final UpdraftArrowConfig defaults = UpdraftArrowConfig.defaults();

    assertEquals(
        defaults,
        new UpdraftArrowConfig(
            defaults.heightBlocks(), defaults.lifetimeTicks(), defaults.strength()));
  }

  @ParameterizedTest
  @CsvSource({"0, 1", "1, 1", "64, 64", "65, 64"})
  void clampsHeightBlocks(final int given, final int expected) {
    assertEquals(expected, UpdraftArrowConfig.defaults().withHeightBlocks(given).heightBlocks());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "1200, 1200", "1201, 1200"})
  void clampsLifetimeTicks(final int given, final int expected) {
    assertEquals(expected, UpdraftArrowConfig.defaults().withLifetimeTicks(given).lifetimeTicks());
  }

  @ParameterizedTest
  @CsvSource({"-1.0, 0.0", "0.0, 0.0", "2.0, 2.0", "3.0, 2.0"})
  void clampsStrength(final float given, final float expected) {
    assertEquals(expected, UpdraftArrowConfig.defaults().withStrength(given).strength());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(UpdraftArrowConfig.defaults(), UpdraftArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(UpdraftArrowConfig.defaults(), UpdraftArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final UpdraftArrowConfig parsed =
        UpdraftArrowConfig.fromJson(
            JsonParser.parseString("{\"heightBlocks\":63}").getAsJsonObject());

    assertEquals(63, parsed.heightBlocks());
    assertEquals(UpdraftArrowConfig.DEFAULT_LIFETIME_TICKS, parsed.lifetimeTicks());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final UpdraftArrowConfig parsed =
        UpdraftArrowConfig.fromJson(
            JsonParser.parseString("{\"heightBlocks\":164}").getAsJsonObject());

    assertEquals(UpdraftArrowConfig.HEIGHT_BLOCKS_MAX, parsed.heightBlocks());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final UpdraftArrowConfig parsed =
        UpdraftArrowConfig.fromJson(
            JsonParser.parseString("{\"heightBlocks\":\"lots\"}").getAsJsonObject());

    assertEquals(UpdraftArrowConfig.DEFAULT_HEIGHT_BLOCKS, parsed.heightBlocks());
  }

  @Test
  void roundTripsThroughJson() {
    final UpdraftArrowConfig original = new UpdraftArrowConfig(63, 1199, 1.5f);

    assertEquals(original, UpdraftArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final UpdraftArrowConfig original = UpdraftArrowConfig.defaults();

    final UpdraftArrowConfig updated = original.withHeightBlocks(63);

    assertEquals(63, updated.heightBlocks());
    assertEquals(original.lifetimeTicks(), updated.lifetimeTicks());
    assertEquals(original.strength(), updated.strength());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final UpdraftArrowConfig updated =
        UpdraftArrowConfig.defaults()
            .withHeightBlocks(63)
            .withLifetimeTicks(1199)
            .withStrength(1.5f);

    assertEquals(new UpdraftArrowConfig(63, 1199, 1.5f), updated);
  }
}
