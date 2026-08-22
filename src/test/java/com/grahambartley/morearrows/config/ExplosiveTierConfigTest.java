package com.grahambartley.morearrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ExplosiveTierConfigTest {

  @ParameterizedTest
  @CsvSource({"-5, 0", "0, 0", "60, 60", "200, 200", "5000, 200"})
  void clampsDelayTicksIntoRange(final int given, final int expected) {
    assertEquals(expected, new ExplosiveTierConfig(given, 4.0f).delayTicks());
  }

  @ParameterizedTest
  @CsvSource({"-1.0, 0.0", "0.0, 0.0", "4.0, 4.0", "20.0, 20.0", "99.0, 20.0"})
  void clampsPowerIntoRange(final float given, final float expected) {
    assertEquals(expected, new ExplosiveTierConfig(60, given).power());
  }

  @Test
  void treatsAZeroDelayAsDetonateOnContact() {
    assertTrue(new ExplosiveTierConfig(0, 4.0f).detonatesOnContact());
  }

  @Test
  void treatsAnyPositiveDelayAsATelegraphedCountdown() {
    assertFalse(new ExplosiveTierConfig(1, 4.0f).detonatesOnContact());
  }

  @Test
  void replacesOnlyTheDelayWhenWitheringIt() {
    final ExplosiveTierConfig updated = new ExplosiveTierConfig(60, 4.0f).withDelayTicks(20);

    assertEquals(20, updated.delayTicks());
    assertEquals(4.0f, updated.power());
  }

  @Test
  void replacesOnlyThePowerWhenWitheringIt() {
    final ExplosiveTierConfig updated = new ExplosiveTierConfig(60, 4.0f).withPower(9.0f);

    assertEquals(60, updated.delayTicks());
    assertEquals(9.0f, updated.power());
  }

  @Test
  void clampsThroughAWitherToo() {
    assertEquals(200, new ExplosiveTierConfig(60, 4.0f).withDelayTicks(9999).delayTicks());
  }

  @Test
  void fallsBackToTheGivenDefaultsWhenKeysAreAbsent() {
    final ExplosiveTierConfig defaults = new ExplosiveTierConfig(60, 4.0f);

    assertEquals(defaults, ExplosiveTierConfig.fromJson(new JsonObject(), defaults));
  }

  @Test
  void roundTripsThroughJson() {
    final ExplosiveTierConfig original = new ExplosiveTierConfig(35, 7.5f);

    assertEquals(
        original,
        ExplosiveTierConfig.fromJson(original.toJson(), new ExplosiveTierConfig(1, 1.0f)));
  }
}
