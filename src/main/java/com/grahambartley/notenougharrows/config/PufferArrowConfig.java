package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record PufferArrowConfig(boolean enabled, int durationTicks) {

  public static final int DURATION_TICKS_MIN = 0;
  public static final int DURATION_TICKS_MAX = 1200;

  public static final boolean DEFAULT_ENABLED = true;
  public static final int DEFAULT_DURATION_TICKS = 200;

  static final String KEY_ENABLED = "enabled";
  static final String KEY_DURATION_TICKS = "durationTicks";

  public PufferArrowConfig {
    durationTicks = ConfigValues.clampInt(durationTicks, DURATION_TICKS_MIN, DURATION_TICKS_MAX);
  }

  public static PufferArrowConfig defaults() {
    return new PufferArrowConfig(DEFAULT_ENABLED, DEFAULT_DURATION_TICKS);
  }

  public static PufferArrowConfig fromJson(final JsonObject root) {
    final PufferArrowConfig defaults = defaults();
    return new PufferArrowConfig(
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

  public PufferArrowConfig withEnabled(final boolean value) {
    return new PufferArrowConfig(value, durationTicks);
  }

  public PufferArrowConfig withDurationTicks(final int value) {
    return new PufferArrowConfig(enabled, value);
  }
}
