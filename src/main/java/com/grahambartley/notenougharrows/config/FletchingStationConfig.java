package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record FletchingStationConfig(boolean stationEnabled) {

  public static final boolean DEFAULT_STATION_ENABLED = true;

  static final String KEY_STATION_ENABLED = "stationEnabled";

  public static FletchingStationConfig defaults() {
    return new FletchingStationConfig(DEFAULT_STATION_ENABLED);
  }

  public static FletchingStationConfig fromJson(final JsonObject root) {
    final FletchingStationConfig defaults = defaults();
    return new FletchingStationConfig(
        ConfigValues.readBoolean(root, KEY_STATION_ENABLED, defaults.stationEnabled()));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_STATION_ENABLED, stationEnabled);
    return root;
  }

  public FletchingStationConfig withStationEnabled(final boolean value) {
    return new FletchingStationConfig(value);
  }
}
