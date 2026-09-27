package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class WebArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final WebArrowConfig defaults = WebArrowConfig.defaults();

    assertEquals(WebArrowConfig.DEFAULT_ENABLED, defaults.enabled());
    assertEquals(WebArrowConfig.DEFAULT_PATCH_RADIUS, defaults.patchRadius());
    assertEquals(WebArrowConfig.DEFAULT_LIFETIME_TICKS, defaults.lifetimeTicks());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final WebArrowConfig defaults = WebArrowConfig.defaults();

    assertEquals(
        defaults,
        new WebArrowConfig(defaults.enabled(), defaults.patchRadius(), defaults.lifetimeTicks()));
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "3, 3", "4, 3"})
  void clampsPatchRadius(final int given, final int expected) {
    assertEquals(expected, WebArrowConfig.defaults().withPatchRadius(given).patchRadius());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "12000, 12000", "12001, 12000"})
  void clampsLifetimeTicks(final int given, final int expected) {
    assertEquals(expected, WebArrowConfig.defaults().withLifetimeTicks(given).lifetimeTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(WebArrowConfig.defaults(), WebArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(WebArrowConfig.defaults(), WebArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final WebArrowConfig parsed =
        WebArrowConfig.fromJson(JsonParser.parseString("{\"patchRadius\":2}").getAsJsonObject());

    assertEquals(2, parsed.patchRadius());
    assertEquals(WebArrowConfig.DEFAULT_ENABLED, parsed.enabled());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final WebArrowConfig parsed =
        WebArrowConfig.fromJson(JsonParser.parseString("{\"patchRadius\":103}").getAsJsonObject());

    assertEquals(WebArrowConfig.PATCH_RADIUS_MAX, parsed.patchRadius());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final WebArrowConfig parsed =
        WebArrowConfig.fromJson(
            JsonParser.parseString("{\"patchRadius\":\"lots\"}").getAsJsonObject());

    assertEquals(WebArrowConfig.DEFAULT_PATCH_RADIUS, parsed.patchRadius());
  }

  @Test
  void roundTripsThroughJson() {
    final WebArrowConfig original = new WebArrowConfig(false, 2, 11999);

    assertEquals(original, WebArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final WebArrowConfig original = WebArrowConfig.defaults();

    final WebArrowConfig updated = original.withEnabled(false);

    assertEquals(false, updated.enabled());
    assertEquals(original.patchRadius(), updated.patchRadius());
    assertEquals(original.lifetimeTicks(), updated.lifetimeTicks());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final WebArrowConfig updated =
        WebArrowConfig.defaults().withEnabled(false).withPatchRadius(2).withLifetimeTicks(11999);

    assertEquals(new WebArrowConfig(false, 2, 11999), updated);
  }
}
