package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record BridgeArrowConfig(int lengthBlocks, int lifetimeTicks) {

  public static final int LENGTH_BLOCKS_MIN = 1;
  public static final int LENGTH_BLOCKS_MAX = 64;
  public static final int LIFETIME_TICKS_MIN = 0;
  public static final int LIFETIME_TICKS_MAX = 12000;

  public static final int DEFAULT_LENGTH_BLOCKS = 16;
  public static final int DEFAULT_LIFETIME_TICKS = 600;

  static final String KEY_LENGTH_BLOCKS = "lengthBlocks";
  static final String KEY_LIFETIME_TICKS = "lifetimeTicks";

  public BridgeArrowConfig {
    lengthBlocks = ConfigValues.clampInt(lengthBlocks, LENGTH_BLOCKS_MIN, LENGTH_BLOCKS_MAX);
    lifetimeTicks = ConfigValues.clampInt(lifetimeTicks, LIFETIME_TICKS_MIN, LIFETIME_TICKS_MAX);
  }

  public static BridgeArrowConfig defaults() {
    return new BridgeArrowConfig(DEFAULT_LENGTH_BLOCKS, DEFAULT_LIFETIME_TICKS);
  }

  public static BridgeArrowConfig fromJson(final JsonObject root) {
    final BridgeArrowConfig defaults = defaults();
    return new BridgeArrowConfig(
        ConfigValues.readInt(
            root, KEY_LENGTH_BLOCKS, defaults.lengthBlocks(), LENGTH_BLOCKS_MIN, LENGTH_BLOCKS_MAX),
        ConfigValues.readInt(
            root,
            KEY_LIFETIME_TICKS,
            defaults.lifetimeTicks(),
            LIFETIME_TICKS_MIN,
            LIFETIME_TICKS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_LENGTH_BLOCKS, lengthBlocks);
    root.addProperty(KEY_LIFETIME_TICKS, lifetimeTicks);
    return root;
  }

  public BridgeArrowConfig withLengthBlocks(final int value) {
    return new BridgeArrowConfig(value, lifetimeTicks);
  }

  public BridgeArrowConfig withLifetimeTicks(final int value) {
    return new BridgeArrowConfig(lengthBlocks, value);
  }
}
