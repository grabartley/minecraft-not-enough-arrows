package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record DisarmArrowConfig(boolean affectsPlayers, float throwDistance) {

  public static final float THROW_DISTANCE_MIN = 0.0f;
  public static final float THROW_DISTANCE_MAX = 16.0f;

  public static final boolean DEFAULT_AFFECTS_PLAYERS = true;
  public static final float DEFAULT_THROW_DISTANCE = 5.0f;

  static final String KEY_AFFECTS_PLAYERS = "affectsPlayers";
  static final String KEY_THROW_DISTANCE = "throwDistance";

  public DisarmArrowConfig {
    throwDistance = ConfigValues.clampFloat(throwDistance, THROW_DISTANCE_MIN, THROW_DISTANCE_MAX);
  }

  public static DisarmArrowConfig defaults() {
    return new DisarmArrowConfig(DEFAULT_AFFECTS_PLAYERS, DEFAULT_THROW_DISTANCE);
  }

  public DisarmArrowConfig withAffectsPlayers(final boolean value) {
    return new DisarmArrowConfig(value, throwDistance);
  }

  public DisarmArrowConfig withThrowDistance(final float value) {
    return new DisarmArrowConfig(affectsPlayers, value);
  }

  public static DisarmArrowConfig fromJson(final JsonObject root) {
    final DisarmArrowConfig defaults = defaults();
    return new DisarmArrowConfig(
        ConfigValues.readBoolean(root, KEY_AFFECTS_PLAYERS, defaults.affectsPlayers()),
        ConfigValues.readFloat(
            root,
            KEY_THROW_DISTANCE,
            defaults.throwDistance(),
            THROW_DISTANCE_MIN,
            THROW_DISTANCE_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_AFFECTS_PLAYERS, affectsPlayers);
    root.addProperty(KEY_THROW_DISTANCE, throwDistance);
    return root;
  }
}
