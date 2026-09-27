package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record TracerArrowConfig(int pathLifetimeTicks) {

  public static final int PATH_LIFETIME_TICKS_MIN = 0;
  public static final int PATH_LIFETIME_TICKS_MAX = 1200;

  public static final int DEFAULT_PATH_LIFETIME_TICKS = 200;

  static final String KEY_PATH_LIFETIME_TICKS = "pathLifetimeTicks";

  public TracerArrowConfig {
    pathLifetimeTicks =
        ConfigValues.clampInt(pathLifetimeTicks, PATH_LIFETIME_TICKS_MIN, PATH_LIFETIME_TICKS_MAX);
  }

  public static TracerArrowConfig defaults() {
    return new TracerArrowConfig(DEFAULT_PATH_LIFETIME_TICKS);
  }

  public static TracerArrowConfig fromJson(final JsonObject root) {
    final TracerArrowConfig defaults = defaults();
    return new TracerArrowConfig(
        ConfigValues.readInt(
            root,
            KEY_PATH_LIFETIME_TICKS,
            defaults.pathLifetimeTicks(),
            PATH_LIFETIME_TICKS_MIN,
            PATH_LIFETIME_TICKS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_PATH_LIFETIME_TICKS, pathLifetimeTicks);
    return root;
  }

  public TracerArrowConfig withPathLifetimeTicks(final int value) {
    return new TracerArrowConfig(value);
  }
}
