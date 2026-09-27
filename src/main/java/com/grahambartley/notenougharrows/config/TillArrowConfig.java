package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record TillArrowConfig(int radius) {

  public static final int RADIUS_MIN = 0;
  public static final int RADIUS_MAX = 8;

  public static final int DEFAULT_RADIUS = 2;

  static final String KEY_RADIUS = "radius";

  public TillArrowConfig {
    radius = ConfigValues.clampInt(radius, RADIUS_MIN, RADIUS_MAX);
  }

  public static TillArrowConfig defaults() {
    return new TillArrowConfig(DEFAULT_RADIUS);
  }

  public static TillArrowConfig fromJson(final JsonObject root) {
    final TillArrowConfig defaults = defaults();
    return new TillArrowConfig(
        ConfigValues.readInt(root, KEY_RADIUS, defaults.radius(), RADIUS_MIN, RADIUS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_RADIUS, radius);
    return root;
  }

  public TillArrowConfig withRadius(final int value) {
    return new TillArrowConfig(value);
  }
}
