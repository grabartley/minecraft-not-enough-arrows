package com.grahambartley.morearrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FletchingStationConfigTest {

  @Test
  void leavesTheStationEnabledByDefaultSoTheFeatureIsOnWithoutConfiguration() {
    assertTrue(FletchingStationConfig.defaults().stationEnabled());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(
        FletchingStationConfig.defaults(), FletchingStationConfig.fromJson(new JsonObject()));
  }

  @ParameterizedTest(name = "stationEnabled={0}")
  @ValueSource(booleans = {true, false})
  void roundTripsTheStationToggleThroughJson(final boolean enabled) {
    final FletchingStationConfig original = new FletchingStationConfig(enabled);

    assertEquals(original, FletchingStationConfig.fromJson(original.toJson()));
  }

  @ParameterizedTest(name = "stationEnabled={0}")
  @ValueSource(booleans = {true, false})
  void writesTheStationToggleWithTheValueItWasGiven(final boolean enabled) {
    assertEquals(
        enabled, new FletchingStationConfig(enabled).withStationEnabled(enabled).stationEnabled());
  }

  @Test
  void replacesTheStationToggleWithoutTouchingTheOriginal() {
    final FletchingStationConfig original = new FletchingStationConfig(true);

    assertEquals(false, original.withStationEnabled(false).stationEnabled());
    assertTrue(original.stationEnabled());
  }

  @Test
  void keepsDefaultsWhenTheStoredValueIsNotABoolean() {
    final FletchingStationConfig parsed =
        FletchingStationConfig.fromJson(
            JsonParser.parseString("{\"stationEnabled\":\"nope\"}").getAsJsonObject());

    assertEquals(FletchingStationConfig.defaults(), parsed);
  }
}
