package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PolymorphArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final PolymorphArrowConfig defaults = PolymorphArrowConfig.defaults();

    assertEquals(PolymorphArrowConfig.DEFAULT_ENABLED, defaults.enabled());
    assertEquals(PolymorphArrowConfig.DEFAULT_DURATION_TICKS, defaults.durationTicks());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final PolymorphArrowConfig defaults = PolymorphArrowConfig.defaults();

    assertEquals(defaults, new PolymorphArrowConfig(defaults.enabled(), defaults.durationTicks()));
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "2400, 2400", "2401, 2400"})
  void clampsDurationTicks(final int given, final int expected) {
    assertEquals(
        expected, PolymorphArrowConfig.defaults().withDurationTicks(given).durationTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(PolymorphArrowConfig.defaults(), PolymorphArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(PolymorphArrowConfig.defaults(), PolymorphArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final PolymorphArrowConfig parsed =
        PolymorphArrowConfig.fromJson(
            JsonParser.parseString("{\"durationTicks\":2399}").getAsJsonObject());

    assertEquals(2399, parsed.durationTicks());
    assertEquals(PolymorphArrowConfig.DEFAULT_ENABLED, parsed.enabled());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final PolymorphArrowConfig parsed =
        PolymorphArrowConfig.fromJson(
            JsonParser.parseString("{\"durationTicks\":2500}").getAsJsonObject());

    assertEquals(PolymorphArrowConfig.DURATION_TICKS_MAX, parsed.durationTicks());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final PolymorphArrowConfig parsed =
        PolymorphArrowConfig.fromJson(
            JsonParser.parseString("{\"durationTicks\":\"lots\"}").getAsJsonObject());

    assertEquals(PolymorphArrowConfig.DEFAULT_DURATION_TICKS, parsed.durationTicks());
  }

  @Test
  void roundTripsThroughJson() {
    final PolymorphArrowConfig original = new PolymorphArrowConfig(false, 2399);

    assertEquals(original, PolymorphArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final PolymorphArrowConfig original = PolymorphArrowConfig.defaults();

    final PolymorphArrowConfig updated = original.withEnabled(false);

    assertEquals(false, updated.enabled());
    assertEquals(original.durationTicks(), updated.durationTicks());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final PolymorphArrowConfig updated =
        PolymorphArrowConfig.defaults().withEnabled(false).withDurationTicks(2399);

    assertEquals(new PolymorphArrowConfig(false, 2399), updated);
  }
}
