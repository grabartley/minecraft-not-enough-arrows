package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ZiplineArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final ZiplineArrowConfig defaults = ZiplineArrowConfig.defaults();

    assertEquals(ZiplineArrowConfig.DEFAULT_MAX_SPAN_BLOCKS, defaults.maxSpanBlocks());
    assertEquals(ZiplineArrowConfig.DEFAULT_PENDING_WINDOW_TICKS, defaults.pendingWindowTicks());
    assertEquals(ZiplineArrowConfig.DEFAULT_RIDE_SPEED, defaults.rideSpeed());
    assertEquals(ZiplineArrowConfig.DEFAULT_LIFETIME_TICKS, defaults.lifetimeTicks());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final ZiplineArrowConfig defaults = ZiplineArrowConfig.defaults();

    assertEquals(
        defaults,
        new ZiplineArrowConfig(
            defaults.maxSpanBlocks(),
            defaults.pendingWindowTicks(),
            defaults.rideSpeed(),
            defaults.lifetimeTicks()));
  }

  @ParameterizedTest
  @CsvSource({"1, 2", "2, 2", "128, 128", "129, 128"})
  void clampsMaxSpanBlocks(final int given, final int expected) {
    assertEquals(expected, ZiplineArrowConfig.defaults().withMaxSpanBlocks(given).maxSpanBlocks());
  }

  @ParameterizedTest
  @CsvSource({"19, 20", "20, 20", "6000, 6000", "6001, 6000"})
  void clampsPendingWindowTicks(final int given, final int expected) {
    assertEquals(
        expected, ZiplineArrowConfig.defaults().withPendingWindowTicks(given).pendingWindowTicks());
  }

  @ParameterizedTest
  @CsvSource({"-0.9, 0.1", "0.1, 0.1", "3.0, 3.0", "4.0, 3.0"})
  void clampsRideSpeed(final float given, final float expected) {
    assertEquals(expected, ZiplineArrowConfig.defaults().withRideSpeed(given).rideSpeed());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "12000, 12000", "12001, 12000"})
  void clampsLifetimeTicks(final int given, final int expected) {
    assertEquals(expected, ZiplineArrowConfig.defaults().withLifetimeTicks(given).lifetimeTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(ZiplineArrowConfig.defaults(), ZiplineArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(ZiplineArrowConfig.defaults(), ZiplineArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final ZiplineArrowConfig parsed =
        ZiplineArrowConfig.fromJson(
            JsonParser.parseString("{\"maxSpanBlocks\":127}").getAsJsonObject());

    assertEquals(127, parsed.maxSpanBlocks());
    assertEquals(ZiplineArrowConfig.DEFAULT_PENDING_WINDOW_TICKS, parsed.pendingWindowTicks());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final ZiplineArrowConfig parsed =
        ZiplineArrowConfig.fromJson(
            JsonParser.parseString("{\"maxSpanBlocks\":228}").getAsJsonObject());

    assertEquals(ZiplineArrowConfig.MAX_SPAN_BLOCKS_MAX, parsed.maxSpanBlocks());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final ZiplineArrowConfig parsed =
        ZiplineArrowConfig.fromJson(
            JsonParser.parseString("{\"maxSpanBlocks\":\"lots\"}").getAsJsonObject());

    assertEquals(ZiplineArrowConfig.DEFAULT_MAX_SPAN_BLOCKS, parsed.maxSpanBlocks());
  }

  @Test
  void roundTripsThroughJson() {
    final ZiplineArrowConfig original = new ZiplineArrowConfig(127, 5999, 2.5f, 11999);

    assertEquals(original, ZiplineArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final ZiplineArrowConfig original = ZiplineArrowConfig.defaults();

    final ZiplineArrowConfig updated = original.withMaxSpanBlocks(127);

    assertEquals(127, updated.maxSpanBlocks());
    assertEquals(original.pendingWindowTicks(), updated.pendingWindowTicks());
    assertEquals(original.rideSpeed(), updated.rideSpeed());
    assertEquals(original.lifetimeTicks(), updated.lifetimeTicks());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final ZiplineArrowConfig updated =
        ZiplineArrowConfig.defaults()
            .withMaxSpanBlocks(127)
            .withPendingWindowTicks(5999)
            .withRideSpeed(2.5f)
            .withLifetimeTicks(11999);

    assertEquals(new ZiplineArrowConfig(127, 5999, 2.5f, 11999), updated);
  }
}
