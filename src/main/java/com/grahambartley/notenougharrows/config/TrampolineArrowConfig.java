package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record TrampolineArrowConfig(float strength, int lifetimeTicks) {

  public static final float STRENGTH_MIN = 0.0f;
  public static final float STRENGTH_MAX = 4.0f;
  public static final int LIFETIME_TICKS_MIN = 0;
  public static final int LIFETIME_TICKS_MAX = 12000;

  public static final float DEFAULT_STRENGTH = 1.2f;
  public static final int DEFAULT_LIFETIME_TICKS = 600;

  static final String KEY_STRENGTH = "strength";
  static final String KEY_LIFETIME_TICKS = "lifetimeTicks";

  public TrampolineArrowConfig {
    strength = ConfigValues.clampFloat(strength, STRENGTH_MIN, STRENGTH_MAX);
    lifetimeTicks = ConfigValues.clampInt(lifetimeTicks, LIFETIME_TICKS_MIN, LIFETIME_TICKS_MAX);
  }

  public static TrampolineArrowConfig defaults() {
    return new TrampolineArrowConfig(DEFAULT_STRENGTH, DEFAULT_LIFETIME_TICKS);
  }

  public static TrampolineArrowConfig fromJson(final JsonObject root) {
    final TrampolineArrowConfig defaults = defaults();
    return new TrampolineArrowConfig(
        ConfigValues.readFloat(root, KEY_STRENGTH, defaults.strength(), STRENGTH_MIN, STRENGTH_MAX),
        ConfigValues.readInt(
            root,
            KEY_LIFETIME_TICKS,
            defaults.lifetimeTicks(),
            LIFETIME_TICKS_MIN,
            LIFETIME_TICKS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_STRENGTH, strength);
    root.addProperty(KEY_LIFETIME_TICKS, lifetimeTicks);
    return root;
  }

  public TrampolineArrowConfig withStrength(final float value) {
    return new TrampolineArrowConfig(value, lifetimeTicks);
  }

  public TrampolineArrowConfig withLifetimeTicks(final int value) {
    return new TrampolineArrowConfig(strength, value);
  }
}
