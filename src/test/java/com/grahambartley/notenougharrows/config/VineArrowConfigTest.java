package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class VineArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final VineArrowConfig defaults = VineArrowConfig.defaults();

    assertEquals(VineArrowConfig.DEFAULT_LENGTH_BLOCKS, defaults.lengthBlocks());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final VineArrowConfig defaults = VineArrowConfig.defaults();

    assertEquals(defaults, new VineArrowConfig(defaults.lengthBlocks()));
  }

  @ParameterizedTest
  @CsvSource({"0, 1", "1, 1", "64, 64", "65, 64"})
  void clampsLengthBlocks(final int given, final int expected) {
    assertEquals(expected, VineArrowConfig.defaults().withLengthBlocks(given).lengthBlocks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(VineArrowConfig.defaults(), VineArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(VineArrowConfig.defaults(), VineArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final VineArrowConfig parsed =
        VineArrowConfig.fromJson(JsonParser.parseString("{\"lengthBlocks\":63}").getAsJsonObject());

    assertEquals(63, parsed.lengthBlocks());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final VineArrowConfig parsed =
        VineArrowConfig.fromJson(
            JsonParser.parseString("{\"lengthBlocks\":164}").getAsJsonObject());

    assertEquals(VineArrowConfig.LENGTH_BLOCKS_MAX, parsed.lengthBlocks());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final VineArrowConfig parsed =
        VineArrowConfig.fromJson(
            JsonParser.parseString("{\"lengthBlocks\":\"lots\"}").getAsJsonObject());

    assertEquals(VineArrowConfig.DEFAULT_LENGTH_BLOCKS, parsed.lengthBlocks());
  }

  @Test
  void roundTripsThroughJson() {
    final VineArrowConfig original = new VineArrowConfig(63);

    assertEquals(original, VineArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final VineArrowConfig original = VineArrowConfig.defaults();

    final VineArrowConfig updated = original.withLengthBlocks(63);

    assertEquals(63, updated.lengthBlocks());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final VineArrowConfig updated = VineArrowConfig.defaults().withLengthBlocks(63);

    assertEquals(new VineArrowConfig(63), updated);
  }
}
