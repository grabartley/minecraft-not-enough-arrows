package com.grahambartley.morearrows.config;

import com.google.gson.JsonObject;

public record UtilityArrowConfig(
    int glowDurationTicks,
    int redstoneSignalDurationTicks,
    int redstoneSignalStrength,
    float windBurstRadius,
    float windPushStrength) {

  public static final int GLOW_DURATION_TICKS_MIN = 0;
  public static final int GLOW_DURATION_TICKS_MAX = 6000;
  public static final int REDSTONE_SIGNAL_DURATION_TICKS_MIN = 1;
  public static final int REDSTONE_SIGNAL_DURATION_TICKS_MAX = 1200;
  public static final int REDSTONE_SIGNAL_STRENGTH_MIN = 1;
  public static final int REDSTONE_SIGNAL_STRENGTH_MAX = 15;
  public static final float WIND_BURST_RADIUS_MIN = 0.5f;
  public static final float WIND_BURST_RADIUS_MAX = 16.0f;
  public static final float WIND_PUSH_STRENGTH_MIN = 0.0f;
  public static final float WIND_PUSH_STRENGTH_MAX = 8.0f;

  public static final int DEFAULT_GLOW_DURATION_TICKS = 200;
  public static final int DEFAULT_REDSTONE_SIGNAL_DURATION_TICKS = 40;
  public static final int DEFAULT_REDSTONE_SIGNAL_STRENGTH = 15;
  public static final float DEFAULT_WIND_BURST_RADIUS = 3.0f;
  public static final float DEFAULT_WIND_PUSH_STRENGTH = 1.0f;

  static final String KEY_GLOW_DURATION_TICKS = "glowDurationTicks";
  static final String KEY_REDSTONE_SIGNAL_DURATION_TICKS = "redstoneSignalDurationTicks";
  static final String KEY_REDSTONE_SIGNAL_STRENGTH = "redstoneSignalStrength";
  static final String KEY_WIND_BURST_RADIUS = "windBurstRadius";
  static final String KEY_WIND_PUSH_STRENGTH = "windPushStrength";

  public UtilityArrowConfig {
    glowDurationTicks =
        ConfigValues.clampInt(glowDurationTicks, GLOW_DURATION_TICKS_MIN, GLOW_DURATION_TICKS_MAX);
    redstoneSignalDurationTicks =
        ConfigValues.clampInt(
            redstoneSignalDurationTicks,
            REDSTONE_SIGNAL_DURATION_TICKS_MIN,
            REDSTONE_SIGNAL_DURATION_TICKS_MAX);
    redstoneSignalStrength =
        ConfigValues.clampInt(
            redstoneSignalStrength, REDSTONE_SIGNAL_STRENGTH_MIN, REDSTONE_SIGNAL_STRENGTH_MAX);
    windBurstRadius =
        ConfigValues.clampFloat(windBurstRadius, WIND_BURST_RADIUS_MIN, WIND_BURST_RADIUS_MAX);
    windPushStrength =
        ConfigValues.clampFloat(windPushStrength, WIND_PUSH_STRENGTH_MIN, WIND_PUSH_STRENGTH_MAX);
  }

  public static UtilityArrowConfig defaults() {
    return new UtilityArrowConfig(
        DEFAULT_GLOW_DURATION_TICKS,
        DEFAULT_REDSTONE_SIGNAL_DURATION_TICKS,
        DEFAULT_REDSTONE_SIGNAL_STRENGTH,
        DEFAULT_WIND_BURST_RADIUS,
        DEFAULT_WIND_PUSH_STRENGTH);
  }

  public static UtilityArrowConfig fromJson(final JsonObject root) {
    final UtilityArrowConfig defaults = defaults();
    return new UtilityArrowConfig(
        ConfigValues.readInt(
            root,
            KEY_GLOW_DURATION_TICKS,
            defaults.glowDurationTicks(),
            GLOW_DURATION_TICKS_MIN,
            GLOW_DURATION_TICKS_MAX),
        ConfigValues.readInt(
            root,
            KEY_REDSTONE_SIGNAL_DURATION_TICKS,
            defaults.redstoneSignalDurationTicks(),
            REDSTONE_SIGNAL_DURATION_TICKS_MIN,
            REDSTONE_SIGNAL_DURATION_TICKS_MAX),
        ConfigValues.readInt(
            root,
            KEY_REDSTONE_SIGNAL_STRENGTH,
            defaults.redstoneSignalStrength(),
            REDSTONE_SIGNAL_STRENGTH_MIN,
            REDSTONE_SIGNAL_STRENGTH_MAX),
        ConfigValues.readFloat(
            root,
            KEY_WIND_BURST_RADIUS,
            defaults.windBurstRadius(),
            WIND_BURST_RADIUS_MIN,
            WIND_BURST_RADIUS_MAX),
        ConfigValues.readFloat(
            root,
            KEY_WIND_PUSH_STRENGTH,
            defaults.windPushStrength(),
            WIND_PUSH_STRENGTH_MIN,
            WIND_PUSH_STRENGTH_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_GLOW_DURATION_TICKS, glowDurationTicks);
    root.addProperty(KEY_REDSTONE_SIGNAL_DURATION_TICKS, redstoneSignalDurationTicks);
    root.addProperty(KEY_REDSTONE_SIGNAL_STRENGTH, redstoneSignalStrength);
    root.addProperty(KEY_WIND_BURST_RADIUS, windBurstRadius);
    root.addProperty(KEY_WIND_PUSH_STRENGTH, windPushStrength);
    return root;
  }
}
