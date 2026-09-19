package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record ShockArrowConfig(float arcRadius, float damage) {

  public static final float ARC_RADIUS_MIN = 0.0f;
  public static final float ARC_RADIUS_MAX = 32.0f;
  public static final float DAMAGE_MIN = 0.0f;
  public static final float DAMAGE_MAX = 20.0f;

  public static final float DEFAULT_ARC_RADIUS = 6.0f;
  public static final float DEFAULT_DAMAGE = 5.0f;

  static final String KEY_ARC_RADIUS = "arcRadius";
  static final String KEY_DAMAGE = "damage";

  public ShockArrowConfig {
    arcRadius = ConfigValues.clampFloat(arcRadius, ARC_RADIUS_MIN, ARC_RADIUS_MAX);
    damage = ConfigValues.clampFloat(damage, DAMAGE_MIN, DAMAGE_MAX);
  }

  public static ShockArrowConfig defaults() {
    return new ShockArrowConfig(DEFAULT_ARC_RADIUS, DEFAULT_DAMAGE);
  }

  public boolean arcs() {
    return arcRadius > ARC_RADIUS_MIN;
  }

  public ShockArrowConfig withArcRadius(final float value) {
    return new ShockArrowConfig(value, damage);
  }

  public ShockArrowConfig withDamage(final float value) {
    return new ShockArrowConfig(arcRadius, value);
  }

  public static ShockArrowConfig fromJson(final JsonObject root) {
    final ShockArrowConfig defaults = defaults();
    return new ShockArrowConfig(
        ConfigValues.readFloat(
            root, KEY_ARC_RADIUS, defaults.arcRadius(), ARC_RADIUS_MIN, ARC_RADIUS_MAX),
        ConfigValues.readFloat(root, KEY_DAMAGE, defaults.damage(), DAMAGE_MIN, DAMAGE_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_ARC_RADIUS, arcRadius);
    root.addProperty(KEY_DAMAGE, damage);
    return root;
  }
}
