package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DrillArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final DrillArrowConfig defaults = DrillArrowConfig.defaults();

    assertEquals(DrillArrowConfig.DEFAULT_ENABLED, defaults.enabled());
    assertEquals(DrillArrowConfig.DEFAULT_TOOL_TIER, defaults.toolTier());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final DrillArrowConfig defaults = DrillArrowConfig.defaults();

    assertEquals(defaults, new DrillArrowConfig(defaults.enabled(), defaults.toolTier()));
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "2, 2", "3, 2"})
  void clampsToolTier(final int given, final int expected) {
    assertEquals(expected, DrillArrowConfig.defaults().withToolTier(given).toolTier());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(DrillArrowConfig.defaults(), DrillArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(DrillArrowConfig.defaults(), DrillArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final DrillArrowConfig parsed =
        DrillArrowConfig.fromJson(JsonParser.parseString("{\"toolTier\":1}").getAsJsonObject());

    assertEquals(1, parsed.toolTier());
    assertEquals(DrillArrowConfig.DEFAULT_ENABLED, parsed.enabled());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final DrillArrowConfig parsed =
        DrillArrowConfig.fromJson(JsonParser.parseString("{\"toolTier\":102}").getAsJsonObject());

    assertEquals(DrillArrowConfig.TOOL_TIER_MAX, parsed.toolTier());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final DrillArrowConfig parsed =
        DrillArrowConfig.fromJson(
            JsonParser.parseString("{\"toolTier\":\"lots\"}").getAsJsonObject());

    assertEquals(DrillArrowConfig.DEFAULT_TOOL_TIER, parsed.toolTier());
  }

  @Test
  void roundTripsThroughJson() {
    final DrillArrowConfig original = new DrillArrowConfig(false, 1);

    assertEquals(original, DrillArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final DrillArrowConfig original = DrillArrowConfig.defaults();

    final DrillArrowConfig updated = original.withEnabled(false);

    assertEquals(false, updated.enabled());
    assertEquals(original.toolTier(), updated.toolTier());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final DrillArrowConfig updated = DrillArrowConfig.defaults().withEnabled(false).withToolTier(1);

    assertEquals(new DrillArrowConfig(false, 1), updated);
  }
}
