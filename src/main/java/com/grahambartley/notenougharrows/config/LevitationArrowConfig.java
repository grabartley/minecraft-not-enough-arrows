package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record LevitationArrowConfig(int durationTicks) {

  public static final int DURATION_TICKS_MIN = 0;
  public static final int DURATION_TICKS_MAX = 1200;

  public static final int DEFAULT_DURATION_TICKS = 60;

  static final String KEY_DURATION_TICKS = "durationTicks";

  public LevitationArrowConfig {
    durationTicks = ConfigValues.clampInt(durationTicks, DURATION_TICKS_MIN, DURATION_TICKS_MAX);
  }

  public static LevitationArrowConfig defaults() {
    return new LevitationArrowConfig(DEFAULT_DURATION_TICKS);
  }

  public boolean lifts() {
    return durationTicks > 0;
  }

  public LevitationArrowConfig withDurationTicks(final int value) {
    return new LevitationArrowConfig(value);
  }

  public static LevitationArrowConfig fromJson(final JsonObject root) {
    final LevitationArrowConfig defaults = defaults();
    return new LevitationArrowConfig(
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
