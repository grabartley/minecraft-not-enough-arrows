package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StatusArrowConfigTest {

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "200, 200", "12000, 12000", "99999, 12000"})
  void clampsRustDuration(final int given, final int expected) {
    assertEquals(expected, new StatusArrowConfig(given, 600, 600).rustDurationTicks());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "600, 600", "12000, 12000", "99999, 12000"})
  void clampsHasteDuration(final int given, final int expected) {
    assertEquals(expected, new StatusArrowConfig(200, given, 600).hasteDurationTicks());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "600, 600", "12000, 12000", "99999, 12000"})
  void clampsGuardDuration(final int given, final int expected) {
    assertEquals(expected, new StatusArrowConfig(200, 600, given).guardDurationTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(StatusArrowConfig.defaults(), StatusArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final StatusArrowConfig parsed =
        StatusArrowConfig.fromJson(
            JsonParser.parseString("{\"rustDurationTicks\":40}").getAsJsonObject());

    assertEquals(40, parsed.rustDurationTicks());
    assertEquals(StatusArrowConfig.DEFAULT_HASTE_DURATION_TICKS, parsed.hasteDurationTicks());
    assertEquals(StatusArrowConfig.DEFAULT_GUARD_DURATION_TICKS, parsed.guardDurationTicks());
  }

  @Test
  void roundTripsThroughJson() {
    final StatusArrowConfig original = new StatusArrowConfig(11, 22, 33);

    assertEquals(original, StatusArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestAlone() {
    final StatusArrowConfig updated = StatusArrowConfig.defaults().withHasteDurationTicks(10);

    assertEquals(10, updated.hasteDurationTicks());
    assertEquals(StatusArrowConfig.DEFAULT_RUST_DURATION_TICKS, updated.rustDurationTicks());
    assertEquals(StatusArrowConfig.DEFAULT_GUARD_DURATION_TICKS, updated.guardDurationTicks());
  }

  @Test
  void aChangedValueIsStillClamped() {
    assertEquals(
        StatusArrowConfig.DURATION_TICKS_MAX,
        StatusArrowConfig.defaults().withRustDurationTicks(999999).rustDurationTicks());
  }

  @Test
  void everyStatusFieldCanBeChangedOnItsOwn() {
    assertEquals(
        new StatusArrowConfig(1, 2, 3),
        StatusArrowConfig.defaults()
            .withRustDurationTicks(1)
            .withHasteDurationTicks(2)
            .withGuardDurationTicks(3));
  }
}
