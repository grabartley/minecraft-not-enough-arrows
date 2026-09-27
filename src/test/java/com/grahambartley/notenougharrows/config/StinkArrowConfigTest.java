package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StinkArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final StinkArrowConfig defaults = StinkArrowConfig.defaults();

    assertEquals(StinkArrowConfig.DEFAULT_ENABLED, defaults.enabled());
    assertEquals(StinkArrowConfig.DEFAULT_CLOUD_LIFETIME_TICKS, defaults.cloudLifetimeTicks());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final StinkArrowConfig defaults = StinkArrowConfig.defaults();

    assertEquals(defaults, new StinkArrowConfig(defaults.enabled(), defaults.cloudLifetimeTicks()));
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "1200, 1200", "1201, 1200"})
  void clampsCloudLifetimeTicks(final int given, final int expected) {
    assertEquals(
        expected, StinkArrowConfig.defaults().withCloudLifetimeTicks(given).cloudLifetimeTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(StinkArrowConfig.defaults(), StinkArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(StinkArrowConfig.defaults(), StinkArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final StinkArrowConfig parsed =
        StinkArrowConfig.fromJson(
            JsonParser.parseString("{\"cloudLifetimeTicks\":1199}").getAsJsonObject());

    assertEquals(1199, parsed.cloudLifetimeTicks());
    assertEquals(StinkArrowConfig.DEFAULT_ENABLED, parsed.enabled());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final StinkArrowConfig parsed =
        StinkArrowConfig.fromJson(
            JsonParser.parseString("{\"cloudLifetimeTicks\":1300}").getAsJsonObject());

    assertEquals(StinkArrowConfig.CLOUD_LIFETIME_TICKS_MAX, parsed.cloudLifetimeTicks());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final StinkArrowConfig parsed =
        StinkArrowConfig.fromJson(
            JsonParser.parseString("{\"cloudLifetimeTicks\":\"lots\"}").getAsJsonObject());

    assertEquals(StinkArrowConfig.DEFAULT_CLOUD_LIFETIME_TICKS, parsed.cloudLifetimeTicks());
  }

  @Test
  void roundTripsThroughJson() {
    final StinkArrowConfig original = new StinkArrowConfig(false, 1199);

    assertEquals(original, StinkArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final StinkArrowConfig original = StinkArrowConfig.defaults();

    final StinkArrowConfig updated = original.withEnabled(false);

    assertEquals(false, updated.enabled());
    assertEquals(original.cloudLifetimeTicks(), updated.cloudLifetimeTicks());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final StinkArrowConfig updated =
        StinkArrowConfig.defaults().withEnabled(false).withCloudLifetimeTicks(1199);

    assertEquals(new StinkArrowConfig(false, 1199), updated);
  }
}
