package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record TargetingArrowConfig(
    float tauntRadius,
    int tauntDurationTicks,
    float repelRadius,
    int repelDurationTicks,
    int dazeDurationTicks) {

  public static final float RADIUS_MIN = 0.0f;
  public static final float RADIUS_MAX = 32.0f;
  public static final int DURATION_TICKS_MIN = 0;
  public static final int DURATION_TICKS_MAX = 6000;

  public static final float DEFAULT_TAUNT_RADIUS = 8.0f;
  public static final int DEFAULT_TAUNT_DURATION_TICKS = 200;
  public static final float DEFAULT_REPEL_RADIUS = 8.0f;
  public static final int DEFAULT_REPEL_DURATION_TICKS = 200;
  public static final int DEFAULT_DAZE_DURATION_TICKS = 200;

  static final String KEY_TAUNT_RADIUS = "tauntRadius";
  static final String KEY_TAUNT_DURATION_TICKS = "tauntDurationTicks";
  static final String KEY_REPEL_RADIUS = "repelRadius";
  static final String KEY_REPEL_DURATION_TICKS = "repelDurationTicks";
  static final String KEY_DAZE_DURATION_TICKS = "dazeDurationTicks";

  public TargetingArrowConfig {
    tauntRadius = ConfigValues.clampFloat(tauntRadius, RADIUS_MIN, RADIUS_MAX);
    repelRadius = ConfigValues.clampFloat(repelRadius, RADIUS_MIN, RADIUS_MAX);
    tauntDurationTicks =
        ConfigValues.clampInt(tauntDurationTicks, DURATION_TICKS_MIN, DURATION_TICKS_MAX);
    repelDurationTicks =
        ConfigValues.clampInt(repelDurationTicks, DURATION_TICKS_MIN, DURATION_TICKS_MAX);
    dazeDurationTicks =
        ConfigValues.clampInt(dazeDurationTicks, DURATION_TICKS_MIN, DURATION_TICKS_MAX);
  }

  public static TargetingArrowConfig defaults() {
    return new TargetingArrowConfig(
        DEFAULT_TAUNT_RADIUS,
        DEFAULT_TAUNT_DURATION_TICKS,
        DEFAULT_REPEL_RADIUS,
        DEFAULT_REPEL_DURATION_TICKS,
        DEFAULT_DAZE_DURATION_TICKS);
  }

  public boolean taunts() {
    return tauntDurationTicks > 0 && tauntRadius > 0.0f;
  }

  public boolean repels() {
    return repelDurationTicks > 0 && repelRadius > 0.0f;
  }

  public boolean dazes() {
    return dazeDurationTicks > 0;
  }

  public TargetingArrowConfig withTauntRadius(final float value) {
    return new TargetingArrowConfig(
        value, tauntDurationTicks, repelRadius, repelDurationTicks, dazeDurationTicks);
  }

  public TargetingArrowConfig withTauntDurationTicks(final int value) {
    return new TargetingArrowConfig(
        tauntRadius, value, repelRadius, repelDurationTicks, dazeDurationTicks);
  }

  public TargetingArrowConfig withRepelRadius(final float value) {
    return new TargetingArrowConfig(
        tauntRadius, tauntDurationTicks, value, repelDurationTicks, dazeDurationTicks);
  }

  public TargetingArrowConfig withRepelDurationTicks(final int value) {
    return new TargetingArrowConfig(
        tauntRadius, tauntDurationTicks, repelRadius, value, dazeDurationTicks);
  }

  public TargetingArrowConfig withDazeDurationTicks(final int value) {
    return new TargetingArrowConfig(
        tauntRadius, tauntDurationTicks, repelRadius, repelDurationTicks, value);
  }

  public static TargetingArrowConfig fromJson(final JsonObject root) {
    final TargetingArrowConfig defaults = defaults();
    return new TargetingArrowConfig(
        ConfigValues.readFloat(
            root, KEY_TAUNT_RADIUS, defaults.tauntRadius(), RADIUS_MIN, RADIUS_MAX),
        ConfigValues.readInt(
            root,
            KEY_TAUNT_DURATION_TICKS,
            defaults.tauntDurationTicks(),
            DURATION_TICKS_MIN,
            DURATION_TICKS_MAX),
        ConfigValues.readFloat(
            root, KEY_REPEL_RADIUS, defaults.repelRadius(), RADIUS_MIN, RADIUS_MAX),
        ConfigValues.readInt(
            root,
            KEY_REPEL_DURATION_TICKS,
            defaults.repelDurationTicks(),
            DURATION_TICKS_MIN,
            DURATION_TICKS_MAX),
        ConfigValues.readInt(
            root,
            KEY_DAZE_DURATION_TICKS,
            defaults.dazeDurationTicks(),
            DURATION_TICKS_MIN,
            DURATION_TICKS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_TAUNT_RADIUS, tauntRadius);
    root.addProperty(KEY_TAUNT_DURATION_TICKS, tauntDurationTicks);
    root.addProperty(KEY_REPEL_RADIUS, repelRadius);
    root.addProperty(KEY_REPEL_DURATION_TICKS, repelDurationTicks);
    root.addProperty(KEY_DAZE_DURATION_TICKS, dazeDurationTicks);
    return root;
  }
}
