package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class GrappleArrowConfigTest {

  @ParameterizedTest
  @CsvSource({"0, 4", "4, 4", "32, 32", "128, 128", "999, 128"})
  void clampsMaxRange(final int given, final int expected) {
    assertEquals(expected, config(given, 0.8f, 16).maxRangeBlocks());
  }

  @ParameterizedTest
  @CsvSource({"0.0, 0.1", "0.1, 0.1", "0.8, 0.8", "4.0, 4.0", "9.0, 4.0"})
  void clampsPullSpeed(final float given, final float expected) {
    assertEquals(expected, config(32, given, 16).pullSpeed());
  }

  @ParameterizedTest
  @CsvSource({"0.0, 0.01", "0.01, 0.01", "0.15, 0.15", "4.0, 4.0", "9.0, 4.0"})
  void clampsPullAcceleration(final float given, final float expected) {
    assertEquals(expected, accelerating(given).pullAcceleration());
  }

  private static GrappleArrowConfig accelerating(final float pullAcceleration) {
    return GrappleArrowConfig.defaults().withPullAcceleration(pullAcceleration);
  }

  @ParameterizedTest
  @CsvSource({"0, 1", "1, 1", "16, 16", "128, 128", "999, 128"})
  void clampsRopeLength(final int given, final int expected) {
    assertEquals(expected, config(32, 0.8f, given).ropeLengthBlocks());
  }

  @Test
  void defaultsToForgivingArrivalBehaviour() {
    final GrappleArrowConfig defaults = GrappleArrowConfig.defaults();

    assertTrue(defaults.cancelFallDamageOnArrival());
    assertTrue(defaults.returnArrowOnArrival());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(GrappleArrowConfig.defaults(), GrappleArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final GrappleArrowConfig parsed =
        GrappleArrowConfig.fromJson(
            JsonParser.parseString("{\"maxRangeBlocks\":64}").getAsJsonObject());

    assertEquals(64, parsed.maxRangeBlocks());
    assertEquals(GrappleArrowConfig.DEFAULT_PULL_SPEED, parsed.pullSpeed());
  }

  @Test
  void roundTripsThroughJson() {
    final GrappleArrowConfig original =
        new GrappleArrowConfig(64, 1.5f, 0.4f, false, false, 40, true);

    assertEquals(original, GrappleArrowConfig.fromJson(original.toJson()));
  }

  private static GrappleArrowConfig config(
      final int maxRange, final float pullSpeed, final int ropeLength) {
    return new GrappleArrowConfig(
        maxRange,
        pullSpeed,
        GrappleArrowConfig.DEFAULT_PULL_ACCELERATION,
        true,
        true,
        ropeLength,
        false);
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final GrappleArrowConfig original = GrappleArrowConfig.defaults();

    final GrappleArrowConfig updated = original.withMaxRangeBlocks(64);

    assertEquals(64, updated.maxRangeBlocks());
    assertEquals(original.withMaxRangeBlocks(original.maxRangeBlocks()), original);
    assertEquals(original.pullSpeed(), updated.pullSpeed());
    assertEquals(original.ropeLengthBlocks(), updated.ropeLengthBlocks());
    assertEquals(original.ropesDecay(), updated.ropesDecay());
  }

  @ParameterizedTest
  @CsvSource({"999, 128", "0, 4"})
  void aChangedValueIsStillClamped(final int given, final int expected) {
    assertEquals(
        expected, GrappleArrowConfig.defaults().withMaxRangeBlocks(given).maxRangeBlocks());
  }

  @Test
  void everyGrappleFieldCanBeChangedOnItsOwn() {
    final GrappleArrowConfig updated =
        GrappleArrowConfig.defaults()
            .withMaxRangeBlocks(64)
            .withPullSpeed(1.5f)
            .withPullAcceleration(0.2f)
            .withCancelFallDamageOnArrival(false)
            .withReturnArrowOnArrival(false)
            .withRopeLengthBlocks(40)
            .withRopesDecay(true);

    assertEquals(new GrappleArrowConfig(64, 1.5f, 0.2f, false, false, 40, true), updated);
  }
}
