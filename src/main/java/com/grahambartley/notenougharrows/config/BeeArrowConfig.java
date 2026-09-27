package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record BeeArrowConfig(int count, int lifetimeTicks) {

  public static final int COUNT_MIN = 1;
  public static final int COUNT_MAX = 8;
  public static final int LIFETIME_TICKS_MIN = 20;
  public static final int LIFETIME_TICKS_MAX = 6000;

  public static final int DEFAULT_COUNT = 3;
  public static final int DEFAULT_LIFETIME_TICKS = 600;

  static final String KEY_COUNT = "count";
  static final String KEY_LIFETIME_TICKS = "lifetimeTicks";

  public BeeArrowConfig {
    count = ConfigValues.clampInt(count, COUNT_MIN, COUNT_MAX);
    lifetimeTicks = ConfigValues.clampInt(lifetimeTicks, LIFETIME_TICKS_MIN, LIFETIME_TICKS_MAX);
  }

  public static BeeArrowConfig defaults() {
    return new BeeArrowConfig(DEFAULT_COUNT, DEFAULT_LIFETIME_TICKS);
  }

  public static BeeArrowConfig fromJson(final JsonObject root) {
    final BeeArrowConfig defaults = defaults();
    return new BeeArrowConfig(
        ConfigValues.readInt(root, KEY_COUNT, defaults.count(), COUNT_MIN, COUNT_MAX),
        ConfigValues.readInt(
            root,
            KEY_LIFETIME_TICKS,
            defaults.lifetimeTicks(),
            LIFETIME_TICKS_MIN,
            LIFETIME_TICKS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_COUNT, count);
    root.addProperty(KEY_LIFETIME_TICKS, lifetimeTicks);
    return root;
  }

  public BeeArrowConfig withCount(final int value) {
    return new BeeArrowConfig(value, lifetimeTicks);
  }

  public BeeArrowConfig withLifetimeTicks(final int value) {
    return new BeeArrowConfig(count, value);
  }
}
