package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class UtilityArrowConfigTest {

  @ParameterizedTest
  @CsvSource({"-1, 0", "200, 200", "6000, 6000", "99999, 6000"})
  void clampsGlowDuration(final int given, final int expected) {
    assertEquals(expected, config(given, 40, 15).glowDurationTicks());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "1, 1", "40, 40", "1200, 1200", "99999, 1200"})
  void clampsRedstoneSignalDuration(final int given, final int expected) {
    assertEquals(expected, config(200, given, 15).redstoneSignalDurationTicks());
  }

  @ParameterizedTest
  @CsvSource({"0, 1", "1, 1", "15, 15", "99, 15"})
  void clampsRedstoneSignalStrengthToTheVanillaScale(final int given, final int expected) {
    assertEquals(expected, config(200, 40, given).redstoneSignalStrength());
  }

  @ParameterizedTest
  @CsvSource({"0.0, 0.5", "3.0, 3.0", "16.0, 16.0", "99.0, 16.0"})
  void clampsWindBurstRadius(final float given, final float expected) {
    final UtilityArrowConfig parsed = new UtilityArrowConfig(200, 40, 15, given, 1.0f);

    assertEquals(expected, parsed.windBurstRadius());
  }

  @ParameterizedTest
  @CsvSource({"-1.0, 0.0", "1.0, 1.0", "8.0, 8.0", "99.0, 8.0"})
  void clampsWindPushStrength(final float given, final float expected) {
    final UtilityArrowConfig parsed = new UtilityArrowConfig(200, 40, 15, 3.0f, given);

    assertEquals(expected, parsed.windPushStrength());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(UtilityArrowConfig.defaults(), UtilityArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final UtilityArrowConfig parsed =
        UtilityArrowConfig.fromJson(
            JsonParser.parseString("{\"redstoneSignalStrength\":4}").getAsJsonObject());

    assertEquals(4, parsed.redstoneSignalStrength());
    assertEquals(UtilityArrowConfig.DEFAULT_GLOW_DURATION_TICKS, parsed.glowDurationTicks());
  }

  @Test
  void roundTripsThroughJson() {
    final UtilityArrowConfig original = new UtilityArrowConfig(100, 20, 7, 5.5f, 2.5f);

    assertEquals(original, UtilityArrowConfig.fromJson(original.toJson()));
  }

  private static UtilityArrowConfig config(
      final int glowTicks, final int redstoneTicks, final int redstoneStrength) {
    return new UtilityArrowConfig(glowTicks, redstoneTicks, redstoneStrength, 3.0f, 1.0f);
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final UtilityArrowConfig original = UtilityArrowConfig.defaults();

    final UtilityArrowConfig updated = original.withRedstoneSignalStrength(7);

    assertEquals(7, updated.redstoneSignalStrength());
    assertEquals(original.glowDurationTicks(), updated.glowDurationTicks());
    assertEquals(original.windPushStrength(), updated.windPushStrength());
  }

  @Test
  void aChangedValueIsStillClamped() {
    assertEquals(
        UtilityArrowConfig.REDSTONE_SIGNAL_STRENGTH_MAX,
        UtilityArrowConfig.defaults().withRedstoneSignalStrength(999).redstoneSignalStrength());
  }

  @Test
  void everyUtilityFieldCanBeChangedOnItsOwn() {
    final UtilityArrowConfig updated =
        UtilityArrowConfig.defaults()
            .withGlowDurationTicks(100)
            .withRedstoneSignalDurationTicks(20)
            .withRedstoneSignalStrength(7)
            .withWindBurstRadius(5.0f)
            .withWindPushStrength(2.0f);

    assertEquals(new UtilityArrowConfig(100, 20, 7, 5.0f, 2.0f), updated);
  }
}
