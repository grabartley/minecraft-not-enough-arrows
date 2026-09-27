package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record BeaconArrowConfig(int lifetimeTicks) {

  public static final int LIFETIME_TICKS_MIN = 0;
  public static final int LIFETIME_TICKS_MAX = 12000;

  public static final int DEFAULT_LIFETIME_TICKS = 1200;

  static final String KEY_LIFETIME_TICKS = "lifetimeTicks";

  public BeaconArrowConfig {
    lifetimeTicks = ConfigValues.clampInt(lifetimeTicks, LIFETIME_TICKS_MIN, LIFETIME_TICKS_MAX);
  }

  public static BeaconArrowConfig defaults() {
    return new BeaconArrowConfig(DEFAULT_LIFETIME_TICKS);
  }

  public static BeaconArrowConfig fromJson(final JsonObject root) {
    final BeaconArrowConfig defaults = defaults();
    return new BeaconArrowConfig(
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

  public BeaconArrowConfig withLifetimeTicks(final int value) {
    return new BeaconArrowConfig(value);
  }
}
