package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record SnowGolemArrowConfig(int lifetimeTicks) {

  public static final int LIFETIME_TICKS_MIN = 20;
  public static final int LIFETIME_TICKS_MAX = 12000;

  public static final int DEFAULT_LIFETIME_TICKS = 1200;

  static final String KEY_LIFETIME_TICKS = "lifetimeTicks";

  public SnowGolemArrowConfig {
    lifetimeTicks = ConfigValues.clampInt(lifetimeTicks, LIFETIME_TICKS_MIN, LIFETIME_TICKS_MAX);
  }

  public static SnowGolemArrowConfig defaults() {
    return new SnowGolemArrowConfig(DEFAULT_LIFETIME_TICKS);
  }

  public static SnowGolemArrowConfig fromJson(final JsonObject root) {
    final SnowGolemArrowConfig defaults = defaults();
    return new SnowGolemArrowConfig(
        ConfigValues.readInt(
            root,
            KEY_LIFETIME_TICKS,
            defaults.lifetimeTicks(),
            LIFETIME_TICKS_MIN,
            LIFETIME_TICKS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_LIFETIME_TICKS, lifetimeTicks);
    return root;
  }

  public SnowGolemArrowConfig withLifetimeTicks(final int value) {
    return new SnowGolemArrowConfig(value);
  }
}
