package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record LifestealArrowConfig(float share, float maxHealPerHit) {

  public static final float SHARE_MIN = 0.0f;
  public static final float SHARE_MAX = 1.0f;
  public static final float MAX_HEAL_PER_HIT_MIN = 0.0f;
  public static final float MAX_HEAL_PER_HIT_MAX = 20.0f;

  public static final float DEFAULT_SHARE = 0.5f;
  public static final float DEFAULT_MAX_HEAL_PER_HIT = 4.0f;

  static final String KEY_SHARE = "share";
  static final String KEY_MAX_HEAL_PER_HIT = "maxHealPerHit";

  public LifestealArrowConfig {
    share = ConfigValues.clampFloat(share, SHARE_MIN, SHARE_MAX);
    maxHealPerHit =
        ConfigValues.clampFloat(maxHealPerHit, MAX_HEAL_PER_HIT_MIN, MAX_HEAL_PER_HIT_MAX);
  }

  public static LifestealArrowConfig defaults() {
    return new LifestealArrowConfig(DEFAULT_SHARE, DEFAULT_MAX_HEAL_PER_HIT);
  }

  public LifestealArrowConfig withShare(final float value) {
    return new LifestealArrowConfig(value, maxHealPerHit);
  }

  public LifestealArrowConfig withMaxHealPerHit(final float value) {
    return new LifestealArrowConfig(share, value);
  }

  public static LifestealArrowConfig fromJson(final JsonObject root) {
    final LifestealArrowConfig defaults = defaults();
    return new LifestealArrowConfig(
        ConfigValues.readFloat(root, KEY_SHARE, defaults.share(), SHARE_MIN, SHARE_MAX),
        ConfigValues.readFloat(
            root,
            KEY_MAX_HEAL_PER_HIT,
            defaults.maxHealPerHit(),
            MAX_HEAL_PER_HIT_MIN,
            MAX_HEAL_PER_HIT_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_SHARE, share);
    root.addProperty(KEY_MAX_HEAL_PER_HIT, maxHealPerHit);
    return root;
  }
}
