package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TargetingArrowConfigTest {

  private static TargetingArrowConfig with(
      final float tauntRadius,
      final int tauntTicks,
      final float repelRadius,
      final int repelTicks) {
    return new TargetingArrowConfig(tauntRadius, tauntTicks, repelRadius, repelTicks);
  }

  @ParameterizedTest
  @CsvSource({"-1.0, 0.0", "0.0, 0.0", "8.0, 8.0", "32.0, 32.0", "99.0, 32.0"})
  void clampsTauntRadius(final float given, final float expected) {
    assertEquals(expected, with(given, 200, 8.0f, 200).tauntRadius());
  }

  @ParameterizedTest
  @CsvSource({"-1.0, 0.0", "8.0, 8.0", "32.0, 32.0", "99.0, 32.0"})
  void clampsRepelRadius(final float given, final float expected) {
    assertEquals(expected, with(8.0f, 200, given, 200).repelRadius());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "200, 200", "6000, 6000", "99999, 6000"})
  void clampsTauntDuration(final int given, final int expected) {
    assertEquals(expected, with(8.0f, given, 8.0f, 200).tauntDurationTicks());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "200, 200", "6000, 6000", "99999, 6000"})
  void clampsRepelDuration(final int given, final int expected) {
    assertEquals(expected, with(8.0f, 200, 8.0f, given).repelDurationTicks());
  }

  @Test
  void tauntsOnlyWithBothAReachAndADuration() {
    assertTrue(with(8.0f, 200, 8.0f, 200).taunts());
    assertFalse(with(0.0f, 200, 8.0f, 200).taunts());
    assertFalse(with(8.0f, 0, 8.0f, 200).taunts());
  }

  @Test
  void repelsOnlyWithBothAReachAndADuration() {
    assertTrue(with(8.0f, 200, 8.0f, 200).repels());
    assertFalse(with(8.0f, 200, 0.0f, 200).repels());
    assertFalse(with(8.0f, 200, 8.0f, 0).repels());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(TargetingArrowConfig.defaults(), TargetingArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final TargetingArrowConfig parsed =
        TargetingArrowConfig.fromJson(
            JsonParser.parseString("{\"tauntDurationTicks\":40}").getAsJsonObject());

    assertEquals(40, parsed.tauntDurationTicks());
    assertEquals(TargetingArrowConfig.DEFAULT_TAUNT_RADIUS, parsed.tauntRadius());
    assertEquals(TargetingArrowConfig.DEFAULT_REPEL_DURATION_TICKS, parsed.repelDurationTicks());
  }

  @Test
  void roundTripsThroughJson() {
    final TargetingArrowConfig original = with(1.5f, 22, 2.5f, 33);

    assertEquals(original, TargetingArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestAlone() {
    final TargetingArrowConfig updated = TargetingArrowConfig.defaults().withRepelRadius(3.0f);

    assertEquals(3.0f, updated.repelRadius());
    assertEquals(TargetingArrowConfig.DEFAULT_TAUNT_RADIUS, updated.tauntRadius());
    assertEquals(TargetingArrowConfig.DEFAULT_TAUNT_DURATION_TICKS, updated.tauntDurationTicks());
    assertEquals(TargetingArrowConfig.DEFAULT_REPEL_DURATION_TICKS, updated.repelDurationTicks());
  }
}
