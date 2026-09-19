package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record FrostArrowConfig(int freezeTicksPerHit) {

  public static final int FREEZE_TICKS_PER_HIT_MIN = 0;
  public static final int FREEZE_TICKS_PER_HIT_MAX = 1200;

  public static final int DEFAULT_FREEZE_TICKS_PER_HIT = 140;

  static final String KEY_FREEZE_TICKS_PER_HIT = "freezeTicksPerHit";

  public FrostArrowConfig {
    freezeTicksPerHit =
        ConfigValues.clampInt(
            freezeTicksPerHit, FREEZE_TICKS_PER_HIT_MIN, FREEZE_TICKS_PER_HIT_MAX);
  }

  public static FrostArrowConfig defaults() {
    return new FrostArrowConfig(DEFAULT_FREEZE_TICKS_PER_HIT);
  }

  public boolean builds() {
    return freezeTicksPerHit > 0;
  }

  public FrostArrowConfig withFreezeTicksPerHit(final int value) {
    return new FrostArrowConfig(value);
  }

  public static FrostArrowConfig fromJson(final JsonObject root) {
    final FrostArrowConfig defaults = defaults();
    return new FrostArrowConfig(
        ConfigValues.readInt(
            root,
            KEY_FREEZE_TICKS_PER_HIT,
            defaults.freezeTicksPerHit(),
            FREEZE_TICKS_PER_HIT_MIN,
            FREEZE_TICKS_PER_HIT_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_FREEZE_TICKS_PER_HIT, freezeTicksPerHit);
    return root;
  }
}
