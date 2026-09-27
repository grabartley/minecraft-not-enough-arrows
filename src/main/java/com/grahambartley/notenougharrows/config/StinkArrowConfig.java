package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record StinkArrowConfig(boolean enabled, int cloudLifetimeTicks) {

  public static final int CLOUD_LIFETIME_TICKS_MIN = 0;
  public static final int CLOUD_LIFETIME_TICKS_MAX = 1200;

  public static final boolean DEFAULT_ENABLED = true;
  public static final int DEFAULT_CLOUD_LIFETIME_TICKS = 200;

  static final String KEY_ENABLED = "enabled";
  static final String KEY_CLOUD_LIFETIME_TICKS = "cloudLifetimeTicks";

  public StinkArrowConfig {
    cloudLifetimeTicks =
        ConfigValues.clampInt(
            cloudLifetimeTicks, CLOUD_LIFETIME_TICKS_MIN, CLOUD_LIFETIME_TICKS_MAX);
  }

  public static StinkArrowConfig defaults() {
    return new StinkArrowConfig(DEFAULT_ENABLED, DEFAULT_CLOUD_LIFETIME_TICKS);
  }

  public static StinkArrowConfig fromJson(final JsonObject root) {
    final StinkArrowConfig defaults = defaults();
    return new StinkArrowConfig(
        ConfigValues.readBoolean(root, KEY_ENABLED, defaults.enabled()),
        ConfigValues.readInt(
            root,
            KEY_CLOUD_LIFETIME_TICKS,
            defaults.cloudLifetimeTicks(),
            CLOUD_LIFETIME_TICKS_MIN,
            CLOUD_LIFETIME_TICKS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_ENABLED, enabled);
    root.addProperty(KEY_CLOUD_LIFETIME_TICKS, cloudLifetimeTicks);
    return root;
  }

  public StinkArrowConfig withEnabled(final boolean value) {
    return new StinkArrowConfig(value, cloudLifetimeTicks);
  }

  public StinkArrowConfig withCloudLifetimeTicks(final int value) {
    return new StinkArrowConfig(enabled, value);
  }
}
