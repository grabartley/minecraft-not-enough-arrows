package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AgricultureArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final AgricultureArrowConfig defaults = AgricultureArrowConfig.defaults();

    assertEquals(AgricultureArrowConfig.DEFAULT_BLOSSOM_RADIUS, defaults.blossomRadius());
    assertEquals(AgricultureArrowConfig.DEFAULT_TILL_RADIUS, defaults.tillRadius());
    assertEquals(AgricultureArrowConfig.DEFAULT_HARVEST_RADIUS, defaults.harvestRadius());
    assertEquals(AgricultureArrowConfig.DEFAULT_BEE_COUNT, defaults.beeCount());
    assertEquals(AgricultureArrowConfig.DEFAULT_BEE_LIFETIME_TICKS, defaults.beeLifetimeTicks());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final AgricultureArrowConfig defaults = AgricultureArrowConfig.defaults();

    assertEquals(
        defaults,
        new AgricultureArrowConfig(
            defaults.blossomRadius(),
            defaults.tillRadius(),
            defaults.harvestRadius(),
            defaults.beeCount(),
            defaults.beeLifetimeTicks()));
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "8, 8", "9, 8"})
  void clampsBlossomRadius(final int given, final int expected) {
    assertEquals(
        expected, AgricultureArrowConfig.defaults().withBlossomRadius(given).blossomRadius());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "8, 8", "9, 8"})
  void clampsTillRadius(final int given, final int expected) {
    assertEquals(expected, AgricultureArrowConfig.defaults().withTillRadius(given).tillRadius());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "8, 8", "9, 8"})
  void clampsHarvestRadius(final int given, final int expected) {
    assertEquals(
        expected, AgricultureArrowConfig.defaults().withHarvestRadius(given).harvestRadius());
  }

  @ParameterizedTest
  @CsvSource({"0, 1", "1, 1", "8, 8", "9, 8"})
  void clampsBeeCount(final int given, final int expected) {
    assertEquals(expected, AgricultureArrowConfig.defaults().withBeeCount(given).beeCount());
  }

  @ParameterizedTest
  @CsvSource({"19, 20", "20, 20", "6000, 6000", "6001, 6000"})
  void clampsBeeLifetimeTicks(final int given, final int expected) {
    assertEquals(
        expected, AgricultureArrowConfig.defaults().withBeeLifetimeTicks(given).beeLifetimeTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(
        AgricultureArrowConfig.defaults(), AgricultureArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(AgricultureArrowConfig.defaults(), AgricultureArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final AgricultureArrowConfig parsed =
        AgricultureArrowConfig.fromJson(
            JsonParser.parseString("{\"blossomRadius\":7}").getAsJsonObject());

    assertEquals(7, parsed.blossomRadius());
    assertEquals(AgricultureArrowConfig.DEFAULT_TILL_RADIUS, parsed.tillRadius());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final AgricultureArrowConfig parsed =
        AgricultureArrowConfig.fromJson(
            JsonParser.parseString("{\"blossomRadius\":108}").getAsJsonObject());

    assertEquals(AgricultureArrowConfig.BLOSSOM_RADIUS_MAX, parsed.blossomRadius());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final AgricultureArrowConfig parsed =
        AgricultureArrowConfig.fromJson(
            JsonParser.parseString("{\"blossomRadius\":\"lots\"}").getAsJsonObject());

    assertEquals(AgricultureArrowConfig.DEFAULT_BLOSSOM_RADIUS, parsed.blossomRadius());
  }

  @Test
  void roundTripsThroughJson() {
    final AgricultureArrowConfig original = new AgricultureArrowConfig(7, 7, 7, 7, 5999);

    assertEquals(original, AgricultureArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final AgricultureArrowConfig original = AgricultureArrowConfig.defaults();

    final AgricultureArrowConfig updated = original.withBlossomRadius(7);

    assertEquals(7, updated.blossomRadius());
    assertEquals(original.tillRadius(), updated.tillRadius());
    assertEquals(original.harvestRadius(), updated.harvestRadius());
    assertEquals(original.beeCount(), updated.beeCount());
    assertEquals(original.beeLifetimeTicks(), updated.beeLifetimeTicks());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final AgricultureArrowConfig updated =
        AgricultureArrowConfig.defaults()
            .withBlossomRadius(7)
            .withTillRadius(7)
            .withHarvestRadius(7)
            .withBeeCount(7)
            .withBeeLifetimeTicks(5999);

    assertEquals(new AgricultureArrowConfig(7, 7, 7, 7, 5999), updated);
  }
}
