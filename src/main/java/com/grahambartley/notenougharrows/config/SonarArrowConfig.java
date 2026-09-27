package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record SonarArrowConfig(int radius, int durationTicks) {

  public static final int RADIUS_MIN = 0;
  public static final int RADIUS_MAX = 32;
  public static final int DURATION_TICKS_MIN = 0;
  public static final int DURATION_TICKS_MAX = 1200;

  public static final int DEFAULT_RADIUS = 16;
  public static final int DEFAULT_DURATION_TICKS = 200;

  static final String KEY_RADIUS = "radius";
  static final String KEY_DURATION_TICKS = "durationTicks";

  public SonarArrowConfig {
    radius = ConfigValues.clampInt(radius, RADIUS_MIN, RADIUS_MAX);
    durationTicks = ConfigValues.clampInt(durationTicks, DURATION_TICKS_MIN, DURATION_TICKS_MAX);
  }

  public static SonarArrowConfig defaults() {
    return new SonarArrowConfig(DEFAULT_RADIUS, DEFAULT_DURATION_TICKS);
  }

  public static SonarArrowConfig fromJson(final JsonObject root) {
    final SonarArrowConfig defaults = defaults();
    return new SonarArrowConfig(
        ConfigValues.readInt(root, KEY_RADIUS, defaults.radius(), RADIUS_MIN, RADIUS_MAX),
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

  public SonarArrowConfig withRadius(final int value) {
    return new SonarArrowConfig(value, durationTicks);
  }

  public SonarArrowConfig withDurationTicks(final int value) {
    return new SonarArrowConfig(radius, value);
  }
}
