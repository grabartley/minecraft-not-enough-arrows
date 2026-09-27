package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record BlossomArrowConfig(int radius) {

  public static final int RADIUS_MIN = 0;
  public static final int RADIUS_MAX = 8;

  public static final int DEFAULT_RADIUS = 2;

  static final String KEY_RADIUS = "radius";

  public BlossomArrowConfig {
    radius = ConfigValues.clampInt(radius, RADIUS_MIN, RADIUS_MAX);
  }

  public static BlossomArrowConfig defaults() {
    return new BlossomArrowConfig(DEFAULT_RADIUS);
  }

  public static BlossomArrowConfig fromJson(final JsonObject root) {
    final BlossomArrowConfig defaults = defaults();
    return new BlossomArrowConfig(
        ConfigValues.readInt(root, KEY_RADIUS, defaults.radius(), RADIUS_MIN, RADIUS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_RADIUS, radius);
    return root;
  }

  public BlossomArrowConfig withRadius(final int value) {
    return new BlossomArrowConfig(value);
  }
}
