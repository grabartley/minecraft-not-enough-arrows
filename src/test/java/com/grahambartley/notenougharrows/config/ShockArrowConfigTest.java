package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ShockArrowConfigTest {

  @ParameterizedTest
  @CsvSource({"-1.0, 0.0", "0.0, 0.0", "6.0, 6.0", "32.0, 32.0", "99.0, 32.0"})
  void clampsArcRadius(final float given, final float expected) {
    assertEquals(expected, new ShockArrowConfig(given, 5.0f).arcRadius());
  }

  @ParameterizedTest
  @CsvSource({"-1.0, 0.0", "5.0, 5.0", "20.0, 20.0", "99.0, 20.0"})
  void clampsDamage(final float given, final float expected) {
    assertEquals(expected, new ShockArrowConfig(6.0f, given).damage());
  }

  @Test
  void aZeroRadiusMeansTheBoltNeverJumps() {
    assertFalse(new ShockArrowConfig(0.0f, 5.0f).arcs());
  }

  @Test
  void anyRadiusAboveZeroLetsTheBoltJumpOnce() {
    assertTrue(new ShockArrowConfig(0.5f, 5.0f).arcs());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(ShockArrowConfig.defaults(), ShockArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final ShockArrowConfig parsed =
        ShockArrowConfig.fromJson(JsonParser.parseString("{\"damage\":2.0}").getAsJsonObject());

    assertEquals(2.0f, parsed.damage());
    assertEquals(ShockArrowConfig.DEFAULT_ARC_RADIUS, parsed.arcRadius());
  }

  @Test
  void roundTripsThroughJson() {
    final ShockArrowConfig original = new ShockArrowConfig(12.5f, 7.5f);

    assertEquals(original, ShockArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheOtherAlone() {
    final ShockArrowConfig updated = ShockArrowConfig.defaults().withArcRadius(1.0f);

    assertEquals(1.0f, updated.arcRadius());
    assertEquals(ShockArrowConfig.DEFAULT_DAMAGE, updated.damage());
  }

  @Test
  void aChangedValueIsStillClamped() {
    assertEquals(
        ShockArrowConfig.ARC_RADIUS_MAX,
        ShockArrowConfig.defaults().withArcRadius(999.0f).arcRadius());
  }

  @Test
  void everyShockFieldCanBeChangedOnItsOwn() {
    assertEquals(
        new ShockArrowConfig(2.0f, 3.0f),
        ShockArrowConfig.defaults().withArcRadius(2.0f).withDamage(3.0f));
  }
}
