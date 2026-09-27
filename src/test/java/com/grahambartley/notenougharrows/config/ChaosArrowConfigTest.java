package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ChaosArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final ChaosArrowConfig defaults = ChaosArrowConfig.defaults();

    assertEquals(ChaosArrowConfig.DEFAULT_PARTY_ENABLED, defaults.partyEnabled());
    assertEquals(ChaosArrowConfig.DEFAULT_CHICKEN_ENABLED, defaults.chickenEnabled());
    assertEquals(ChaosArrowConfig.DEFAULT_PUFFER_ENABLED, defaults.pufferEnabled());
    assertEquals(ChaosArrowConfig.DEFAULT_PUFFER_DURATION_TICKS, defaults.pufferDurationTicks());
    assertEquals(ChaosArrowConfig.DEFAULT_STINK_ENABLED, defaults.stinkEnabled());
    assertEquals(
        ChaosArrowConfig.DEFAULT_STINK_CLOUD_LIFETIME_TICKS, defaults.stinkCloudLifetimeTicks());
    assertEquals(ChaosArrowConfig.DEFAULT_BOOMERANG_ENABLED, defaults.boomerangEnabled());
    assertEquals(ChaosArrowConfig.DEFAULT_POLYMORPH_ENABLED, defaults.polymorphEnabled());
    assertEquals(
        ChaosArrowConfig.DEFAULT_POLYMORPH_DURATION_TICKS, defaults.polymorphDurationTicks());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final ChaosArrowConfig defaults = ChaosArrowConfig.defaults();

    assertEquals(
        defaults,
        new ChaosArrowConfig(
            defaults.partyEnabled(),
            defaults.chickenEnabled(),
            defaults.pufferEnabled(),
            defaults.pufferDurationTicks(),
            defaults.stinkEnabled(),
            defaults.stinkCloudLifetimeTicks(),
            defaults.boomerangEnabled(),
            defaults.polymorphEnabled(),
            defaults.polymorphDurationTicks()));
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "1200, 1200", "1201, 1200"})
  void clampsPufferDurationTicks(final int given, final int expected) {
    assertEquals(
        expected, ChaosArrowConfig.defaults().withPufferDurationTicks(given).pufferDurationTicks());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "1200, 1200", "1201, 1200"})
  void clampsStinkCloudLifetimeTicks(final int given, final int expected) {
    assertEquals(
        expected,
        ChaosArrowConfig.defaults().withStinkCloudLifetimeTicks(given).stinkCloudLifetimeTicks());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "2400, 2400", "2401, 2400"})
  void clampsPolymorphDurationTicks(final int given, final int expected) {
    assertEquals(
        expected,
        ChaosArrowConfig.defaults().withPolymorphDurationTicks(given).polymorphDurationTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(ChaosArrowConfig.defaults(), ChaosArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(ChaosArrowConfig.defaults(), ChaosArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final ChaosArrowConfig parsed =
        ChaosArrowConfig.fromJson(
            JsonParser.parseString("{\"pufferDurationTicks\":1199}").getAsJsonObject());

    assertEquals(1199, parsed.pufferDurationTicks());
    assertEquals(ChaosArrowConfig.DEFAULT_PARTY_ENABLED, parsed.partyEnabled());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final ChaosArrowConfig parsed =
        ChaosArrowConfig.fromJson(
            JsonParser.parseString("{\"pufferDurationTicks\":1300}").getAsJsonObject());

    assertEquals(ChaosArrowConfig.PUFFER_DURATION_TICKS_MAX, parsed.pufferDurationTicks());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final ChaosArrowConfig parsed =
        ChaosArrowConfig.fromJson(
            JsonParser.parseString("{\"pufferDurationTicks\":\"lots\"}").getAsJsonObject());

    assertEquals(ChaosArrowConfig.DEFAULT_PUFFER_DURATION_TICKS, parsed.pufferDurationTicks());
  }

  @Test
  void roundTripsThroughJson() {
    final ChaosArrowConfig original =
        new ChaosArrowConfig(false, false, false, 1199, false, 1199, false, false, 2399);

    assertEquals(original, ChaosArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final ChaosArrowConfig original = ChaosArrowConfig.defaults();

    final ChaosArrowConfig updated = original.withPartyEnabled(false);

    assertEquals(false, updated.partyEnabled());
    assertEquals(original.chickenEnabled(), updated.chickenEnabled());
    assertEquals(original.pufferEnabled(), updated.pufferEnabled());
    assertEquals(original.pufferDurationTicks(), updated.pufferDurationTicks());
    assertEquals(original.stinkEnabled(), updated.stinkEnabled());
    assertEquals(original.stinkCloudLifetimeTicks(), updated.stinkCloudLifetimeTicks());
    assertEquals(original.boomerangEnabled(), updated.boomerangEnabled());
    assertEquals(original.polymorphEnabled(), updated.polymorphEnabled());
    assertEquals(original.polymorphDurationTicks(), updated.polymorphDurationTicks());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final ChaosArrowConfig updated =
        ChaosArrowConfig.defaults()
            .withPartyEnabled(false)
            .withChickenEnabled(false)
            .withPufferEnabled(false)
            .withPufferDurationTicks(1199)
            .withStinkEnabled(false)
            .withStinkCloudLifetimeTicks(1199)
            .withBoomerangEnabled(false)
            .withPolymorphEnabled(false)
            .withPolymorphDurationTicks(2399);

    assertEquals(
        new ChaosArrowConfig(false, false, false, 1199, false, 1199, false, false, 2399), updated);
  }
}
