package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record HarvestArrowConfig(int radius) {

  public static final int RADIUS_MIN = 0;
  public static final int RADIUS_MAX = 8;

  public static final int DEFAULT_RADIUS = 3;

  static final String KEY_RADIUS = "radius";

  public HarvestArrowConfig {
    radius = ConfigValues.clampInt(radius, RADIUS_MIN, RADIUS_MAX);
  }

  public static HarvestArrowConfig defaults() {
    return new HarvestArrowConfig(DEFAULT_RADIUS);
  }

  public static HarvestArrowConfig fromJson(final JsonObject root) {
    final HarvestArrowConfig defaults = defaults();
    return new HarvestArrowConfig(
        ConfigValues.readInt(root, KEY_RADIUS, defaults.radius(), RADIUS_MIN, RADIUS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_RADIUS, radius);
    return root;
  }

  public HarvestArrowConfig withRadius(final int value) {
    return new HarvestArrowConfig(value);
  }
}
