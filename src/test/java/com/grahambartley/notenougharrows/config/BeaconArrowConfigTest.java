package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class BeaconArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final BeaconArrowConfig defaults = BeaconArrowConfig.defaults();

    assertEquals(BeaconArrowConfig.DEFAULT_LIFETIME_TICKS, defaults.lifetimeTicks());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final BeaconArrowConfig defaults = BeaconArrowConfig.defaults();

    assertEquals(defaults, new BeaconArrowConfig(defaults.lifetimeTicks()));
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "12000, 12000", "12001, 12000"})
  void clampsLifetimeTicks(final int given, final int expected) {
    assertEquals(expected, BeaconArrowConfig.defaults().withLifetimeTicks(given).lifetimeTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(BeaconArrowConfig.defaults(), BeaconArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(BeaconArrowConfig.defaults(), BeaconArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final BeaconArrowConfig parsed =
        BeaconArrowConfig.fromJson(
            JsonParser.parseString("{\"lifetimeTicks\":11999}").getAsJsonObject());

    assertEquals(11999, parsed.lifetimeTicks());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final BeaconArrowConfig parsed =
        BeaconArrowConfig.fromJson(
            JsonParser.parseString("{\"lifetimeTicks\":12100}").getAsJsonObject());

    assertEquals(BeaconArrowConfig.LIFETIME_TICKS_MAX, parsed.lifetimeTicks());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final BeaconArrowConfig parsed =
        BeaconArrowConfig.fromJson(
            JsonParser.parseString("{\"lifetimeTicks\":\"lots\"}").getAsJsonObject());

    assertEquals(BeaconArrowConfig.DEFAULT_LIFETIME_TICKS, parsed.lifetimeTicks());
  }

  @Test
  void roundTripsThroughJson() {
    final BeaconArrowConfig original = new BeaconArrowConfig(11999);

    assertEquals(original, BeaconArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final BeaconArrowConfig original = BeaconArrowConfig.defaults();

    final BeaconArrowConfig updated = original.withLifetimeTicks(11999);

    assertEquals(11999, updated.lifetimeTicks());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final BeaconArrowConfig updated = BeaconArrowConfig.defaults().withLifetimeTicks(11999);

    assertEquals(new BeaconArrowConfig(11999), updated);
  }
}
