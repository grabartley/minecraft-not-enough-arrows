package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record MagnetArrowConfig(int radius) {

  public static final int RADIUS_MIN = 0;
  public static final int RADIUS_MAX = 16;

  public static final int DEFAULT_RADIUS = 8;

  static final String KEY_RADIUS = "radius";

  public MagnetArrowConfig {
    radius = ConfigValues.clampInt(radius, RADIUS_MIN, RADIUS_MAX);
  }

  public static MagnetArrowConfig defaults() {
    return new MagnetArrowConfig(DEFAULT_RADIUS);
  }

  public static MagnetArrowConfig fromJson(final JsonObject root) {
    final MagnetArrowConfig defaults = defaults();
    return new MagnetArrowConfig(
        ConfigValues.readInt(root, KEY_RADIUS, defaults.radius(), RADIUS_MIN, RADIUS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_RADIUS, radius);
    return root;
  }

  public MagnetArrowConfig withRadius(final int value) {
    return new MagnetArrowConfig(value);
  }
}
