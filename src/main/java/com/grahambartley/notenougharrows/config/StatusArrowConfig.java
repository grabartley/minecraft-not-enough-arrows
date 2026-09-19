package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record StatusArrowConfig(
    int rustDurationTicks, int hasteDurationTicks, int guardDurationTicks) {

  public static final int DURATION_TICKS_MIN = 0;
  public static final int DURATION_TICKS_MAX = 12000;

  public static final int DEFAULT_RUST_DURATION_TICKS = 200;
  public static final int DEFAULT_HASTE_DURATION_TICKS = 600;
  public static final int DEFAULT_GUARD_DURATION_TICKS = 600;

  static final String KEY_RUST_DURATION_TICKS = "rustDurationTicks";
  static final String KEY_HASTE_DURATION_TICKS = "hasteDurationTicks";
  static final String KEY_GUARD_DURATION_TICKS = "guardDurationTicks";

  public StatusArrowConfig {
    rustDurationTicks =
        ConfigValues.clampInt(rustDurationTicks, DURATION_TICKS_MIN, DURATION_TICKS_MAX);
    hasteDurationTicks =
        ConfigValues.clampInt(hasteDurationTicks, DURATION_TICKS_MIN, DURATION_TICKS_MAX);
    guardDurationTicks =
        ConfigValues.clampInt(guardDurationTicks, DURATION_TICKS_MIN, DURATION_TICKS_MAX);
  }

  public static StatusArrowConfig defaults() {
    return new StatusArrowConfig(
        DEFAULT_RUST_DURATION_TICKS, DEFAULT_HASTE_DURATION_TICKS, DEFAULT_GUARD_DURATION_TICKS);
  }

  public StatusArrowConfig withRustDurationTicks(final int value) {
    return new StatusArrowConfig(value, hasteDurationTicks, guardDurationTicks);
  }

  public StatusArrowConfig withHasteDurationTicks(final int value) {
    return new StatusArrowConfig(rustDurationTicks, value, guardDurationTicks);
  }

  public StatusArrowConfig withGuardDurationTicks(final int value) {
    return new StatusArrowConfig(rustDurationTicks, hasteDurationTicks, value);
  }

  public static StatusArrowConfig fromJson(final JsonObject root) {
    final StatusArrowConfig defaults = defaults();
    return new StatusArrowConfig(
        ConfigValues.readInt(
            root,
            KEY_RUST_DURATION_TICKS,
            defaults.rustDurationTicks(),
            DURATION_TICKS_MIN,
            DURATION_TICKS_MAX),
        ConfigValues.readInt(
            root,
            KEY_HASTE_DURATION_TICKS,
            defaults.hasteDurationTicks(),
            DURATION_TICKS_MIN,
            DURATION_TICKS_MAX),
        ConfigValues.readInt(
            root,
            KEY_GUARD_DURATION_TICKS,
            defaults.guardDurationTicks(),
            DURATION_TICKS_MIN,
            DURATION_TICKS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_RUST_DURATION_TICKS, rustDurationTicks);
    root.addProperty(KEY_HASTE_DURATION_TICKS, hasteDurationTicks);
    root.addProperty(KEY_GUARD_DURATION_TICKS, guardDurationTicks);
    return root;
  }
}
