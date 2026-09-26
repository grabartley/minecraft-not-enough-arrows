package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record AllegianceArrowConfig(int durationTicks, float defendRadius) {

  public static final int DURATION_TICKS_MIN = 0;
  public static final int DURATION_TICKS_MAX = 6000;
  public static final float DEFEND_RADIUS_MIN = 0.0f;
  public static final float DEFEND_RADIUS_MAX = 32.0f;

  public static final int DEFAULT_DURATION_TICKS = 400;
  public static final float DEFAULT_DEFEND_RADIUS = 16.0f;

  static final String KEY_DURATION_TICKS = "durationTicks";
  static final String KEY_DEFEND_RADIUS = "defendRadius";

  public AllegianceArrowConfig {
    durationTicks = ConfigValues.clampInt(durationTicks, DURATION_TICKS_MIN, DURATION_TICKS_MAX);
    defendRadius = ConfigValues.clampFloat(defendRadius, DEFEND_RADIUS_MIN, DEFEND_RADIUS_MAX);
  }

  public static AllegianceArrowConfig defaults() {
    return new AllegianceArrowConfig(DEFAULT_DURATION_TICKS, DEFAULT_DEFEND_RADIUS);
  }

  public boolean turns() {
    return durationTicks > 0;
  }

  public AllegianceArrowConfig withDurationTicks(final int value) {
    return new AllegianceArrowConfig(value, defendRadius);
  }

  public AllegianceArrowConfig withDefendRadius(final float value) {
    return new AllegianceArrowConfig(durationTicks, value);
  }

  public static AllegianceArrowConfig fromJson(final JsonObject root) {
    final AllegianceArrowConfig defaults = defaults();
    return new AllegianceArrowConfig(
        ConfigValues.readInt(
            root,
            KEY_DURATION_TICKS,
            defaults.durationTicks(),
            DURATION_TICKS_MIN,
            DURATION_TICKS_MAX),
        ConfigValues.readFloat(
            root,
            KEY_DEFEND_RADIUS,
            defaults.defendRadius(),
            DEFEND_RADIUS_MIN,
            DEFEND_RADIUS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_DURATION_TICKS, durationTicks);
    root.addProperty(KEY_DEFEND_RADIUS, defendRadius);
    return root;
  }
}
