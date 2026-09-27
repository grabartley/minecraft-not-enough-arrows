package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record FreezeArrowConfig(boolean enabled, int radius) {

  public static final int RADIUS_MIN = 0;
  public static final int RADIUS_MAX = 8;

  public static final boolean DEFAULT_ENABLED = true;
  public static final int DEFAULT_RADIUS = 3;

  static final String KEY_ENABLED = "enabled";
  static final String KEY_RADIUS = "radius";

  public FreezeArrowConfig {
    radius = ConfigValues.clampInt(radius, RADIUS_MIN, RADIUS_MAX);
  }

  public static FreezeArrowConfig defaults() {
    return new FreezeArrowConfig(DEFAULT_ENABLED, DEFAULT_RADIUS);
  }

  public static FreezeArrowConfig fromJson(final JsonObject root) {
    final FreezeArrowConfig defaults = defaults();
    return new FreezeArrowConfig(
        ConfigValues.readBoolean(root, KEY_ENABLED, defaults.enabled()),
        ConfigValues.readInt(root, KEY_RADIUS, defaults.radius(), RADIUS_MIN, RADIUS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_ENABLED, enabled);
    root.addProperty(KEY_RADIUS, radius);
    return root;
  }

  public FreezeArrowConfig withEnabled(final boolean value) {
    return new FreezeArrowConfig(value, radius);
  }

  public FreezeArrowConfig withRadius(final int value) {
    return new FreezeArrowConfig(enabled, value);
  }
}
