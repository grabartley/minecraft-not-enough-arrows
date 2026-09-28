package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record TowArrowConfig(int rangeBlocks, int maxTicks, float speed) {

  public static final int RANGE_BLOCKS_MIN = 1;
  public static final int RANGE_BLOCKS_MAX = 128;
  public static final int MAX_TICKS_MIN = 1;
  public static final int MAX_TICKS_MAX = 1200;
  public static final float SPEED_MIN = 0.1f;
  public static final float SPEED_MAX = 1.5f;

  public static final int DEFAULT_RANGE_BLOCKS = 32;
  public static final int DEFAULT_MAX_TICKS = 100;
  public static final float DEFAULT_SPEED = 0.5f;

  static final String KEY_RANGE_BLOCKS = "rangeBlocks";
  static final String KEY_MAX_TICKS = "maxTicks";
  static final String KEY_SPEED = "speed";

  public TowArrowConfig {
    rangeBlocks = ConfigValues.clampInt(rangeBlocks, RANGE_BLOCKS_MIN, RANGE_BLOCKS_MAX);
    maxTicks = ConfigValues.clampInt(maxTicks, MAX_TICKS_MIN, MAX_TICKS_MAX);
    speed = ConfigValues.clampFloat(speed, SPEED_MIN, SPEED_MAX);
  }

  public static TowArrowConfig defaults() {
    return new TowArrowConfig(DEFAULT_RANGE_BLOCKS, DEFAULT_MAX_TICKS, DEFAULT_SPEED);
  }

  public static TowArrowConfig fromJson(final JsonObject root) {
    final TowArrowConfig defaults = defaults();
    return new TowArrowConfig(
        ConfigValues.readInt(
            root, KEY_RANGE_BLOCKS, defaults.rangeBlocks(), RANGE_BLOCKS_MIN, RANGE_BLOCKS_MAX),
        ConfigValues.readInt(
            root, KEY_MAX_TICKS, defaults.maxTicks(), MAX_TICKS_MIN, MAX_TICKS_MAX),
        ConfigValues.readFloat(root, KEY_SPEED, defaults.speed(), SPEED_MIN, SPEED_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_RANGE_BLOCKS, rangeBlocks);
    root.addProperty(KEY_MAX_TICKS, maxTicks);
    root.addProperty(KEY_SPEED, speed);
    return root;
  }

  public TowArrowConfig withRangeBlocks(final int value) {
    return new TowArrowConfig(value, maxTicks, speed);
  }

  public TowArrowConfig withMaxTicks(final int value) {
    return new TowArrowConfig(rangeBlocks, value, speed);
  }

  public TowArrowConfig withSpeed(final float value) {
    return new TowArrowConfig(rangeBlocks, maxTicks, value);
  }
}
