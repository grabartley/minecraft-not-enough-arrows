package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record PolymorphArrowConfig(boolean enabled, int durationTicks) {

  public static final int DURATION_TICKS_MIN = 0;
  public static final int DURATION_TICKS_MAX = 2400;

  public static final boolean DEFAULT_ENABLED = true;
  public static final int DEFAULT_DURATION_TICKS = 400;

  static final String KEY_ENABLED = "enabled";
  static final String KEY_DURATION_TICKS = "durationTicks";

  public PolymorphArrowConfig {
    durationTicks = ConfigValues.clampInt(durationTicks, DURATION_TICKS_MIN, DURATION_TICKS_MAX);
  }

  public static PolymorphArrowConfig defaults() {
    return new PolymorphArrowConfig(DEFAULT_ENABLED, DEFAULT_DURATION_TICKS);
  }

  public static PolymorphArrowConfig fromJson(final JsonObject root) {
    final PolymorphArrowConfig defaults = defaults();
    return new PolymorphArrowConfig(
        ConfigValues.readBoolean(root, KEY_ENABLED, defaults.enabled()),
        ConfigValues.readInt(
            root,
            KEY_DURATION_TICKS,
            defaults.durationTicks(),
            DURATION_TICKS_MIN,
            DURATION_TICKS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_ENABLED, enabled);
    root.addProperty(KEY_DURATION_TICKS, durationTicks);
    return root;
  }

  public PolymorphArrowConfig withEnabled(final boolean value) {
    return new PolymorphArrowConfig(value, durationTicks);
  }

  public PolymorphArrowConfig withDurationTicks(final int value) {
    return new PolymorphArrowConfig(enabled, value);
  }
}
