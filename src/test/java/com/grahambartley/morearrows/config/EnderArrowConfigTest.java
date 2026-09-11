package com.grahambartley.morearrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EnderArrowConfigTest {

  @ParameterizedTest
  @CsvSource({"0, 4", "4, 4", "64, 64", "128, 128", "999, 128"})
  void clampsPearlRange(final int given, final int expected) {
    assertEquals(
        expected, EnderArrowConfig.defaults().withPearlMaxRangeBlocks(given).pearlMaxRangeBlocks());
  }

  @ParameterizedTest
  @CsvSource({"-1.0, 0.0", "0.0, 0.0", "5.0, 5.0", "20.0, 20.0", "99.0, 20.0"})
  void clampsArrivalDamage(final float given, final float expected) {
    assertEquals(
        expected, EnderArrowConfig.defaults().withPearlArrivalDamage(given).pearlArrivalDamage());
  }

  @ParameterizedTest
  @CsvSource({"0, 4", "4, 4", "32, 32", "128, 128", "999, 128"})
  void clampsRecallRange(final int given, final int expected) {
    assertEquals(
        expected,
        EnderArrowConfig.defaults().withRecallMaxRangeBlocks(given).recallMaxRangeBlocks());
  }

  @Test
  void leavesPlayersOutOfReachOfTheRecallArrowByDefault() {
    assertFalse(EnderArrowConfig.defaults().recallAffectsPlayers());
  }

  @Test
  void arrivesAsHardAsAThrownPearlByDefault() {
    assertEquals(5.0f, EnderArrowConfig.defaults().pearlArrivalDamage());
  }

  @Test
  void reachesFurtherWithAPearlThanWithARecallByDefault() {
    final EnderArrowConfig defaults = EnderArrowConfig.defaults();

    assertEquals(64, defaults.pearlMaxRangeBlocks());
    assertEquals(32, defaults.recallMaxRangeBlocks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(EnderArrowConfig.defaults(), EnderArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final EnderArrowConfig parsed =
        EnderArrowConfig.fromJson(
            JsonParser.parseString("{\"pearlMaxRangeBlocks\":16}").getAsJsonObject());

    assertEquals(16, parsed.pearlMaxRangeBlocks());
    assertEquals(EnderArrowConfig.DEFAULT_PEARL_ARRIVAL_DAMAGE, parsed.pearlArrivalDamage());
    assertEquals(EnderArrowConfig.DEFAULT_RECALL_MAX_RANGE_BLOCKS, parsed.recallMaxRangeBlocks());
    assertEquals(EnderArrowConfig.DEFAULT_RECALL_AFFECTS_PLAYERS, parsed.recallAffectsPlayers());
  }

  @Test
  void roundTripsThroughJson() {
    final EnderArrowConfig original = new EnderArrowConfig(96, 12.5f, 8, true);

    assertEquals(original, EnderArrowConfig.fromJson(original.toJson()));
  }
}
