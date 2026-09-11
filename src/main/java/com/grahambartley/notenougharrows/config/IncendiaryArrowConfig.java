package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record IncendiaryArrowConfig(int burnRadius, int igniteSeconds, boolean ignitesBlocks) {

  public static final int BURN_RADIUS_MIN = 0;
  public static final int BURN_RADIUS_MAX = 8;
  public static final int IGNITE_SECONDS_MIN = 0;
  public static final int IGNITE_SECONDS_MAX = 60;

  public static final int DEFAULT_BURN_RADIUS = 3;
  public static final int DEFAULT_IGNITE_SECONDS = 5;
  public static final boolean DEFAULT_IGNITES_BLOCKS = true;

  static final String KEY_BURN_RADIUS = "burnRadius";
  static final String KEY_IGNITE_SECONDS = "igniteSeconds";
  static final String KEY_IGNITES_BLOCKS = "ignitesBlocks";

  public IncendiaryArrowConfig {
    burnRadius = ConfigValues.clampInt(burnRadius, BURN_RADIUS_MIN, BURN_RADIUS_MAX);
    igniteSeconds = ConfigValues.clampInt(igniteSeconds, IGNITE_SECONDS_MIN, IGNITE_SECONDS_MAX);
  }

  public static IncendiaryArrowConfig defaults() {
    return new IncendiaryArrowConfig(
        DEFAULT_BURN_RADIUS, DEFAULT_IGNITE_SECONDS, DEFAULT_IGNITES_BLOCKS);
  }

  public static IncendiaryArrowConfig fromJson(
      final JsonObject root, final IncendiaryArrowConfig defaults) {
    return new IncendiaryArrowConfig(
        ConfigValues.readInt(
            root, KEY_BURN_RADIUS, defaults.burnRadius(), BURN_RADIUS_MIN, BURN_RADIUS_MAX),
        ConfigValues.readInt(
            root,
            KEY_IGNITE_SECONDS,
            defaults.igniteSeconds(),
            IGNITE_SECONDS_MIN,
            IGNITE_SECONDS_MAX),
        ConfigValues.readBoolean(root, KEY_IGNITES_BLOCKS, defaults.ignitesBlocks()));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_BURN_RADIUS, burnRadius);
    root.addProperty(KEY_IGNITE_SECONDS, igniteSeconds);
    root.addProperty(KEY_IGNITES_BLOCKS, ignitesBlocks);
    return root;
  }

  public IncendiaryArrowConfig withBurnRadius(final int value) {
    return new IncendiaryArrowConfig(value, igniteSeconds, ignitesBlocks);
  }

  public IncendiaryArrowConfig withIgniteSeconds(final int value) {
    return new IncendiaryArrowConfig(burnRadius, value, ignitesBlocks);
  }

  public IncendiaryArrowConfig withIgnitesBlocks(final boolean value) {
    return new IncendiaryArrowConfig(burnRadius, igniteSeconds, value);
  }
}
