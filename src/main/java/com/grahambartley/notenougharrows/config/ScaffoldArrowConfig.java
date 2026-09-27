package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record ScaffoldArrowConfig(int heightBlocks, int lifetimeTicks) {

  public static final int HEIGHT_BLOCKS_MIN = 1;
  public static final int HEIGHT_BLOCKS_MAX = 64;
  public static final int LIFETIME_TICKS_MIN = 0;
  public static final int LIFETIME_TICKS_MAX = 12000;

  public static final int DEFAULT_HEIGHT_BLOCKS = 8;
  public static final int DEFAULT_LIFETIME_TICKS = 600;

  static final String KEY_HEIGHT_BLOCKS = "heightBlocks";
  static final String KEY_LIFETIME_TICKS = "lifetimeTicks";

  public ScaffoldArrowConfig {
    heightBlocks = ConfigValues.clampInt(heightBlocks, HEIGHT_BLOCKS_MIN, HEIGHT_BLOCKS_MAX);
    lifetimeTicks = ConfigValues.clampInt(lifetimeTicks, LIFETIME_TICKS_MIN, LIFETIME_TICKS_MAX);
  }

  public static ScaffoldArrowConfig defaults() {
    return new ScaffoldArrowConfig(DEFAULT_HEIGHT_BLOCKS, DEFAULT_LIFETIME_TICKS);
  }

  public static ScaffoldArrowConfig fromJson(final JsonObject root) {
    final ScaffoldArrowConfig defaults = defaults();
    return new ScaffoldArrowConfig(
        ConfigValues.readInt(
            root, KEY_HEIGHT_BLOCKS, defaults.heightBlocks(), HEIGHT_BLOCKS_MIN, HEIGHT_BLOCKS_MAX),
        ConfigValues.readInt(
            root,
            KEY_LIFETIME_TICKS,
            defaults.lifetimeTicks(),
            LIFETIME_TICKS_MIN,
            LIFETIME_TICKS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_HEIGHT_BLOCKS, heightBlocks);
    root.addProperty(KEY_LIFETIME_TICKS, lifetimeTicks);
    return root;
  }

  public ScaffoldArrowConfig withHeightBlocks(final int value) {
    return new ScaffoldArrowConfig(value, lifetimeTicks);
  }

  public ScaffoldArrowConfig withLifetimeTicks(final int value) {
    return new ScaffoldArrowConfig(heightBlocks, value);
  }
}
