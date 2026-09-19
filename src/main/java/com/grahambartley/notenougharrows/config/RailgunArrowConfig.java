package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record RailgunArrowConfig(float speedMultiplier, float gravityFactor) {

  public static final float SPEED_MULTIPLIER_MIN = 1.0f;
  public static final float SPEED_MULTIPLIER_MAX = 8.0f;
  public static final float GRAVITY_FACTOR_MIN = 0.05f;
  public static final float GRAVITY_FACTOR_MAX = 2.0f;

  public static final float DEFAULT_SPEED_MULTIPLIER = 3.0f;
  public static final float DEFAULT_GRAVITY_FACTOR = 0.25f;

  static final String KEY_SPEED_MULTIPLIER = "speedMultiplier";
  static final String KEY_GRAVITY_FACTOR = "gravityFactor";

  public RailgunArrowConfig {
    speedMultiplier =
        ConfigValues.clampFloat(speedMultiplier, SPEED_MULTIPLIER_MIN, SPEED_MULTIPLIER_MAX);
    gravityFactor = ConfigValues.clampFloat(gravityFactor, GRAVITY_FACTOR_MIN, GRAVITY_FACTOR_MAX);
  }

  public static RailgunArrowConfig defaults() {
    return new RailgunArrowConfig(DEFAULT_SPEED_MULTIPLIER, DEFAULT_GRAVITY_FACTOR);
  }

  public RailgunArrowConfig withSpeedMultiplier(final float value) {
    return new RailgunArrowConfig(value, gravityFactor);
  }

  public RailgunArrowConfig withGravityFactor(final float value) {
    return new RailgunArrowConfig(speedMultiplier, value);
  }

  public static RailgunArrowConfig fromJson(final JsonObject root) {
    final RailgunArrowConfig defaults = defaults();
    return new RailgunArrowConfig(
        ConfigValues.readFloat(
            root,
            KEY_SPEED_MULTIPLIER,
            defaults.speedMultiplier(),
            SPEED_MULTIPLIER_MIN,
            SPEED_MULTIPLIER_MAX),
        ConfigValues.readFloat(
            root,
            KEY_GRAVITY_FACTOR,
            defaults.gravityFactor(),
            GRAVITY_FACTOR_MIN,
            GRAVITY_FACTOR_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_SPEED_MULTIPLIER, speedMultiplier);
    root.addProperty(KEY_GRAVITY_FACTOR, gravityFactor);
    return root;
  }
}
