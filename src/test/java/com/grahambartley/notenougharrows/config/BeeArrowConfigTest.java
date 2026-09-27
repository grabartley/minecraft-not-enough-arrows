package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class BeeArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final BeeArrowConfig defaults = BeeArrowConfig.defaults();

    assertEquals(BeeArrowConfig.DEFAULT_COUNT, defaults.count());
    assertEquals(BeeArrowConfig.DEFAULT_LIFETIME_TICKS, defaults.lifetimeTicks());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final BeeArrowConfig defaults = BeeArrowConfig.defaults();

    assertEquals(defaults, new BeeArrowConfig(defaults.count(), defaults.lifetimeTicks()));
  }

  @ParameterizedTest
  @CsvSource({"0, 1", "1, 1", "8, 8", "9, 8"})
  void clampsCount(final int given, final int expected) {
    assertEquals(expected, BeeArrowConfig.defaults().withCount(given).count());
  }

  @ParameterizedTest
  @CsvSource({"19, 20", "20, 20", "6000, 6000", "6001, 6000"})
  void clampsLifetimeTicks(final int given, final int expected) {
    assertEquals(expected, BeeArrowConfig.defaults().withLifetimeTicks(given).lifetimeTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(BeeArrowConfig.defaults(), BeeArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(BeeArrowConfig.defaults(), BeeArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final BeeArrowConfig parsed =
        BeeArrowConfig.fromJson(JsonParser.parseString("{\"count\":7}").getAsJsonObject());

    assertEquals(7, parsed.count());
    assertEquals(BeeArrowConfig.DEFAULT_LIFETIME_TICKS, parsed.lifetimeTicks());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final BeeArrowConfig parsed =
        BeeArrowConfig.fromJson(JsonParser.parseString("{\"count\":108}").getAsJsonObject());

    assertEquals(BeeArrowConfig.COUNT_MAX, parsed.count());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final BeeArrowConfig parsed =
        BeeArrowConfig.fromJson(JsonParser.parseString("{\"count\":\"lots\"}").getAsJsonObject());

    assertEquals(BeeArrowConfig.DEFAULT_COUNT, parsed.count());
  }

  @Test
  void roundTripsThroughJson() {
    final BeeArrowConfig original = new BeeArrowConfig(7, 5999);

    assertEquals(original, BeeArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final BeeArrowConfig original = BeeArrowConfig.defaults();

    final BeeArrowConfig updated = original.withCount(7);

    assertEquals(7, updated.count());
    assertEquals(original.lifetimeTicks(), updated.lifetimeTicks());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final BeeArrowConfig updated = BeeArrowConfig.defaults().withCount(7).withLifetimeTicks(5999);

    assertEquals(new BeeArrowConfig(7, 5999), updated);
  }
}
