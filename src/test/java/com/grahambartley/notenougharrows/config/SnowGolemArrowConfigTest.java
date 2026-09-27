package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SnowGolemArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final SnowGolemArrowConfig defaults = SnowGolemArrowConfig.defaults();

    assertEquals(SnowGolemArrowConfig.DEFAULT_LIFETIME_TICKS, defaults.lifetimeTicks());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final SnowGolemArrowConfig defaults = SnowGolemArrowConfig.defaults();

    assertEquals(defaults, new SnowGolemArrowConfig(defaults.lifetimeTicks()));
  }

  @ParameterizedTest
  @CsvSource({"19, 20", "20, 20", "12000, 12000", "12001, 12000"})
  void clampsLifetimeTicks(final int given, final int expected) {
    assertEquals(
        expected, SnowGolemArrowConfig.defaults().withLifetimeTicks(given).lifetimeTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(SnowGolemArrowConfig.defaults(), SnowGolemArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(SnowGolemArrowConfig.defaults(), SnowGolemArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final SnowGolemArrowConfig parsed =
        SnowGolemArrowConfig.fromJson(
            JsonParser.parseString("{\"lifetimeTicks\":11999}").getAsJsonObject());

    assertEquals(11999, parsed.lifetimeTicks());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final SnowGolemArrowConfig parsed =
        SnowGolemArrowConfig.fromJson(
            JsonParser.parseString("{\"lifetimeTicks\":12100}").getAsJsonObject());

    assertEquals(SnowGolemArrowConfig.LIFETIME_TICKS_MAX, parsed.lifetimeTicks());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final SnowGolemArrowConfig parsed =
        SnowGolemArrowConfig.fromJson(
            JsonParser.parseString("{\"lifetimeTicks\":\"lots\"}").getAsJsonObject());

    assertEquals(SnowGolemArrowConfig.DEFAULT_LIFETIME_TICKS, parsed.lifetimeTicks());
  }

  @Test
  void roundTripsThroughJson() {
    final SnowGolemArrowConfig original = new SnowGolemArrowConfig(11999);

    assertEquals(original, SnowGolemArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final SnowGolemArrowConfig original = SnowGolemArrowConfig.defaults();

    final SnowGolemArrowConfig updated = original.withLifetimeTicks(11999);

    assertEquals(11999, updated.lifetimeTicks());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final SnowGolemArrowConfig updated = SnowGolemArrowConfig.defaults().withLifetimeTicks(11999);

    assertEquals(new SnowGolemArrowConfig(11999), updated);
  }
}
