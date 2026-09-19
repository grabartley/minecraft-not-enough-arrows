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
      final int repelTicks,
      final int dazeTicks) {
    return new TargetingArrowConfig(tauntRadius, tauntTicks, repelRadius, repelTicks, dazeTicks);
  }

  @ParameterizedTest
  @CsvSource({"-1.0, 0.0", "0.0, 0.0", "8.0, 8.0", "32.0, 32.0", "99.0, 32.0"})
  void clampsTauntRadius(final float given, final float expected) {
    assertEquals(expected, with(given, 200, 8.0f, 200, 200).tauntRadius());
  }

  @ParameterizedTest
  @CsvSource({"-1.0, 0.0", "8.0, 8.0", "32.0, 32.0", "99.0, 32.0"})
  void clampsRepelRadius(final float given, final float expected) {
    assertEquals(expected, with(8.0f, 200, given, 200, 200).repelRadius());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "200, 200", "6000, 6000", "99999, 6000"})
  void clampsTauntDuration(final int given, final int expected) {
    assertEquals(expected, with(8.0f, given, 8.0f, 200, 200).tauntDurationTicks());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "200, 200", "6000, 6000", "99999, 6000"})
  void clampsRepelDuration(final int given, final int expected) {
    assertEquals(expected, with(8.0f, 200, 8.0f, given, 200).repelDurationTicks());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "200, 200", "6000, 6000", "99999, 6000"})
  void clampsDazeDuration(final int given, final int expected) {
    assertEquals(expected, with(8.0f, 200, 8.0f, 200, given).dazeDurationTicks());
  }

  @Test
  void tauntsOnlyWithBothAReachAndADuration() {
    assertTrue(with(8.0f, 200, 8.0f, 200, 200).taunts());
    assertFalse(with(0.0f, 200, 8.0f, 200, 200).taunts());
    assertFalse(with(8.0f, 0, 8.0f, 200, 200).taunts());
  }

  @Test
  void repelsOnlyWithBothAReachAndADuration() {
    assertTrue(with(8.0f, 200, 8.0f, 200, 200).repels());
    assertFalse(with(8.0f, 200, 0.0f, 200, 200).repels());
    assertFalse(with(8.0f, 200, 8.0f, 0, 200).repels());
  }

  @Test
  void dazesOnlyWithADuration() {
    assertTrue(with(8.0f, 200, 8.0f, 200, 1).dazes());
    assertFalse(with(8.0f, 200, 8.0f, 200, 0).dazes());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(TargetingArrowConfig.defaults(), TargetingArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final TargetingArrowConfig parsed =
        TargetingArrowConfig.fromJson(
            JsonParser.parseString("{\"dazeDurationTicks\":40}").getAsJsonObject());

    assertEquals(40, parsed.dazeDurationTicks());
    assertEquals(TargetingArrowConfig.DEFAULT_TAUNT_RADIUS, parsed.tauntRadius());
    assertEquals(TargetingArrowConfig.DEFAULT_REPEL_DURATION_TICKS, parsed.repelDurationTicks());
  }

  @Test
  void roundTripsThroughJson() {
    final TargetingArrowConfig original = with(1.5f, 22, 2.5f, 33, 44);

    assertEquals(original, TargetingArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestAlone() {
    final TargetingArrowConfig updated = TargetingArrowConfig.defaults().withRepelRadius(3.0f);

    assertEquals(3.0f, updated.repelRadius());
    assertEquals(TargetingArrowConfig.DEFAULT_TAUNT_RADIUS, updated.tauntRadius());
    assertEquals(TargetingArrowConfig.DEFAULT_DAZE_DURATION_TICKS, updated.dazeDurationTicks());
  }
}
