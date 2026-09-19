package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HomingArrowConfigTest {

  @ParameterizedTest
  @CsvSource({"-1.0, 0.0", "0.2, 0.2", "1.0, 1.0", "2.0, 1.0"})
  void clampsTurnRateToAFraction(final float given, final float expected) {
    assertEquals(expected, new HomingArrowConfig(given, 16.0f, 60.0f).turnRate());
  }

  @ParameterizedTest
  @CsvSource({"-1.0, 0.0", "16.0, 16.0", "64.0, 64.0", "99.0, 64.0"})
  void clampsSearchRadius(final float given, final float expected) {
    assertEquals(expected, new HomingArrowConfig(0.2f, given, 60.0f).searchRadius());
  }

  @ParameterizedTest
  @CsvSource({"-1.0, 0.0", "60.0, 60.0", "180.0, 180.0", "360.0, 180.0"})
  void clampsSearchConeToAtMostAFullSweepAhead(final float given, final float expected) {
    assertEquals(expected, new HomingArrowConfig(0.2f, 16.0f, given).searchConeDegrees());
  }

  @ParameterizedTest
  @CsvSource({"0.0, 16.0, false", "0.2, 0.0, false", "0.0, 0.0, false", "0.2, 16.0, true"})
  void seeksOnlyWhenItCanBothTurnAndSee(
      final float turnRate, final float searchRadius, final boolean expected) {
    final HomingArrowConfig config = new HomingArrowConfig(turnRate, searchRadius, 60.0f);

    if (expected) {
      assertTrue(config.seeks());
    } else {
      assertFalse(config.seeks());
    }
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(HomingArrowConfig.defaults(), HomingArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final HomingArrowConfig parsed =
        HomingArrowConfig.fromJson(JsonParser.parseString("{\"turnRate\":0.5}").getAsJsonObject());

    assertEquals(0.5f, parsed.turnRate());
    assertEquals(HomingArrowConfig.DEFAULT_SEARCH_RADIUS, parsed.searchRadius());
  }

  @Test
  void roundTripsThroughJson() {
    final HomingArrowConfig original = new HomingArrowConfig(0.35f, 24.0f, 45.0f);

    assertEquals(original, HomingArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestAlone() {
    final HomingArrowConfig updated = HomingArrowConfig.defaults().withSearchRadius(4.0f);

    assertEquals(4.0f, updated.searchRadius());
    assertEquals(HomingArrowConfig.DEFAULT_TURN_RATE, updated.turnRate());
    assertEquals(HomingArrowConfig.DEFAULT_SEARCH_CONE_DEGREES, updated.searchConeDegrees());
  }

  @Test
  void aChangedValueIsStillClamped() {
    assertEquals(
        HomingArrowConfig.TURN_RATE_MAX,
        HomingArrowConfig.defaults().withTurnRate(9.0f).turnRate());
  }

  @Test
  void everyHomingFieldCanBeChangedOnItsOwn() {
    assertEquals(
        new HomingArrowConfig(0.1f, 2.0f, 3.0f),
        HomingArrowConfig.defaults()
            .withTurnRate(0.1f)
            .withSearchRadius(2.0f)
            .withSearchConeDegrees(3.0f));
  }
}
