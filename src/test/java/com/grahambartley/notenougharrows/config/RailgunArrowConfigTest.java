package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class RailgunArrowConfigTest {

  @ParameterizedTest
  @CsvSource({"0.0, 1.0", "0.5, 1.0", "3.0, 3.0", "8.0, 8.0", "99.0, 8.0"})
  void neverFliesSlowerThanAnOrdinaryArrow(final float given, final float expected) {
    assertEquals(expected, new RailgunArrowConfig(given, 0.25f).speedMultiplier());
  }

  @ParameterizedTest
  @CsvSource({"0.25, 0.25", "2.0, 2.0", "9.0, 2.0"})
  void clampsGravityFactor(final float given, final float expected) {
    assertEquals(expected, new RailgunArrowConfig(3.0f, given).gravityFactor());
  }

  @ParameterizedTest
  @ValueSource(floats = {0.0f, -1.0f, 0.001f})
  void aGravityFactorOfNoneIsLiftedToTheMinimumSoTheArrowAlwaysFalls(final float given) {
    final float factor = new RailgunArrowConfig(3.0f, given).gravityFactor();

    assertEquals(RailgunArrowConfig.GRAVITY_FACTOR_MIN, factor);
    assertTrue(factor > 0.0f);
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(RailgunArrowConfig.defaults(), RailgunArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final RailgunArrowConfig parsed =
        RailgunArrowConfig.fromJson(
            JsonParser.parseString("{\"speedMultiplier\":5.0}").getAsJsonObject());

    assertEquals(5.0f, parsed.speedMultiplier());
    assertEquals(RailgunArrowConfig.DEFAULT_GRAVITY_FACTOR, parsed.gravityFactor());
  }

  @Test
  void roundTripsThroughJson() {
    final RailgunArrowConfig original = new RailgunArrowConfig(4.5f, 0.75f);

    assertEquals(original, RailgunArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheOtherAlone() {
    final RailgunArrowConfig updated = RailgunArrowConfig.defaults().withSpeedMultiplier(2.0f);

    assertEquals(2.0f, updated.speedMultiplier());
    assertEquals(RailgunArrowConfig.DEFAULT_GRAVITY_FACTOR, updated.gravityFactor());
  }

  @Test
  void aChangedValueIsStillClamped() {
    assertEquals(
        RailgunArrowConfig.GRAVITY_FACTOR_MIN,
        RailgunArrowConfig.defaults().withGravityFactor(0.0f).gravityFactor());
  }

  @Test
  void everyRailgunFieldCanBeChangedOnItsOwn() {
    assertEquals(
        new RailgunArrowConfig(6.0f, 1.5f),
        RailgunArrowConfig.defaults().withSpeedMultiplier(6.0f).withGravityFactor(1.5f));
  }
}
