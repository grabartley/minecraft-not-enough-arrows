package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record FrostArrowConfig(int durationTicks) {

  public static final int DURATION_TICKS_MIN = 0;
  public static final int DURATION_TICKS_MAX = 1200;

  public static final int DEFAULT_DURATION_TICKS = 200;

  static final String KEY_DURATION_TICKS = "durationTicks";

  public FrostArrowConfig {
    durationTicks = ConfigValues.clampInt(durationTicks, DURATION_TICKS_MIN, DURATION_TICKS_MAX);
  }

  public static FrostArrowConfig defaults() {
    return new FrostArrowConfig(DEFAULT_DURATION_TICKS);
  }

  public FrostArrowConfig withDurationTicks(final int value) {
    return new FrostArrowConfig(value);
  }

  public static FrostArrowConfig fromJson(final JsonObject root) {
    final FrostArrowConfig defaults = defaults();
    return new FrostArrowConfig(
        ConfigValues.readInt(
            root,
            KEY_DURATION_TICKS,
            defaults.durationTicks(),
            DURATION_TICKS_MIN,
            DURATION_TICKS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_DURATION_TICKS, durationTicks);
    return root;
  }
}
