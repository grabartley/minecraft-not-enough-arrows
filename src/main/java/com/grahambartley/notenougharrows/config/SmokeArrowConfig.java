package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record SmokeArrowConfig(float radius, int durationTicks) {

  public static final float RADIUS_MIN = 0.0f;
  public static final float RADIUS_MAX = 16.0f;
  public static final int DURATION_TICKS_MIN = 0;
  public static final int DURATION_TICKS_MAX = 6000;

  public static final float DEFAULT_RADIUS = 3.0f;
  public static final int DEFAULT_DURATION_TICKS = 200;

  static final String KEY_RADIUS = "radius";
  static final String KEY_DURATION_TICKS = "durationTicks";

  public SmokeArrowConfig {
    radius = ConfigValues.clampFloat(radius, RADIUS_MIN, RADIUS_MAX);
    durationTicks = ConfigValues.clampInt(durationTicks, DURATION_TICKS_MIN, DURATION_TICKS_MAX);
  }

  public static SmokeArrowConfig defaults() {
    return new SmokeArrowConfig(DEFAULT_RADIUS, DEFAULT_DURATION_TICKS);
  }

  public boolean clouds() {
    return durationTicks > 0 && radius > 0.0f;
  }

  public SmokeArrowConfig withRadius(final float value) {
    return new SmokeArrowConfig(value, durationTicks);
  }

  public SmokeArrowConfig withDurationTicks(final int value) {
    return new SmokeArrowConfig(radius, value);
  }

  public static SmokeArrowConfig fromJson(final JsonObject root) {
    final SmokeArrowConfig defaults = defaults();
    return new SmokeArrowConfig(
        ConfigValues.readFloat(root, KEY_RADIUS, defaults.radius(), RADIUS_MIN, RADIUS_MAX),
        ConfigValues.readInt(
            root,
            KEY_DURATION_TICKS,
            defaults.durationTicks(),
            DURATION_TICKS_MIN,
            DURATION_TICKS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_RADIUS, radius);
    root.addProperty(KEY_DURATION_TICKS, durationTicks);
    return root;
  }
}
