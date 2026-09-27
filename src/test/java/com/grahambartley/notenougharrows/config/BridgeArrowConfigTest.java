package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class BridgeArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final BridgeArrowConfig defaults = BridgeArrowConfig.defaults();

    assertEquals(BridgeArrowConfig.DEFAULT_LENGTH_BLOCKS, defaults.lengthBlocks());
    assertEquals(BridgeArrowConfig.DEFAULT_LIFETIME_TICKS, defaults.lifetimeTicks());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final BridgeArrowConfig defaults = BridgeArrowConfig.defaults();

    assertEquals(
        defaults, new BridgeArrowConfig(defaults.lengthBlocks(), defaults.lifetimeTicks()));
  }

  @ParameterizedTest
  @CsvSource({"0, 1", "1, 1", "64, 64", "65, 64"})
  void clampsLengthBlocks(final int given, final int expected) {
    assertEquals(expected, BridgeArrowConfig.defaults().withLengthBlocks(given).lengthBlocks());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "12000, 12000", "12001, 12000"})
  void clampsLifetimeTicks(final int given, final int expected) {
    assertEquals(expected, BridgeArrowConfig.defaults().withLifetimeTicks(given).lifetimeTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(BridgeArrowConfig.defaults(), BridgeArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(BridgeArrowConfig.defaults(), BridgeArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final BridgeArrowConfig parsed =
        BridgeArrowConfig.fromJson(
            JsonParser.parseString("{\"lengthBlocks\":63}").getAsJsonObject());

    assertEquals(63, parsed.lengthBlocks());
    assertEquals(BridgeArrowConfig.DEFAULT_LIFETIME_TICKS, parsed.lifetimeTicks());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final BridgeArrowConfig parsed =
        BridgeArrowConfig.fromJson(
            JsonParser.parseString("{\"lengthBlocks\":164}").getAsJsonObject());

    assertEquals(BridgeArrowConfig.LENGTH_BLOCKS_MAX, parsed.lengthBlocks());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final BridgeArrowConfig parsed =
        BridgeArrowConfig.fromJson(
            JsonParser.parseString("{\"lengthBlocks\":\"lots\"}").getAsJsonObject());

    assertEquals(BridgeArrowConfig.DEFAULT_LENGTH_BLOCKS, parsed.lengthBlocks());
  }

  @Test
  void roundTripsThroughJson() {
    final BridgeArrowConfig original = new BridgeArrowConfig(63, 11999);

    assertEquals(original, BridgeArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final BridgeArrowConfig original = BridgeArrowConfig.defaults();

    final BridgeArrowConfig updated = original.withLengthBlocks(63);

    assertEquals(63, updated.lengthBlocks());
    assertEquals(original.lifetimeTicks(), updated.lifetimeTicks());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final BridgeArrowConfig updated =
        BridgeArrowConfig.defaults().withLengthBlocks(63).withLifetimeTicks(11999);

    assertEquals(new BridgeArrowConfig(63, 11999), updated);
  }
}
