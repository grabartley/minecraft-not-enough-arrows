package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record PillarArrowConfig(boolean enabled, int heightBlocks, int lifetimeTicks) {

  public static final int HEIGHT_BLOCKS_MIN = 1;
  public static final int HEIGHT_BLOCKS_MAX = 16;
  public static final int LIFETIME_TICKS_MIN = 0;
  public static final int LIFETIME_TICKS_MAX = 12000;

  public static final boolean DEFAULT_ENABLED = true;
  public static final int DEFAULT_HEIGHT_BLOCKS = 4;
  public static final int DEFAULT_LIFETIME_TICKS = 600;

  static final String KEY_ENABLED = "enabled";
  static final String KEY_HEIGHT_BLOCKS = "heightBlocks";
  static final String KEY_LIFETIME_TICKS = "lifetimeTicks";

  public PillarArrowConfig {
    heightBlocks = ConfigValues.clampInt(heightBlocks, HEIGHT_BLOCKS_MIN, HEIGHT_BLOCKS_MAX);
    lifetimeTicks = ConfigValues.clampInt(lifetimeTicks, LIFETIME_TICKS_MIN, LIFETIME_TICKS_MAX);
  }

  public static PillarArrowConfig defaults() {
    return new PillarArrowConfig(DEFAULT_ENABLED, DEFAULT_HEIGHT_BLOCKS, DEFAULT_LIFETIME_TICKS);
  }

  public static PillarArrowConfig fromJson(final JsonObject root) {
    final PillarArrowConfig defaults = defaults();
    return new PillarArrowConfig(
        ConfigValues.readBoolean(root, KEY_ENABLED, defaults.enabled()),
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
    root.addProperty(KEY_ENABLED, enabled);
    root.addProperty(KEY_HEIGHT_BLOCKS, heightBlocks);
    root.addProperty(KEY_LIFETIME_TICKS, lifetimeTicks);
    return root;
  }

  public PillarArrowConfig withEnabled(final boolean value) {
    return new PillarArrowConfig(value, heightBlocks, lifetimeTicks);
  }

  public PillarArrowConfig withHeightBlocks(final int value) {
    return new PillarArrowConfig(enabled, value, lifetimeTicks);
  }

  public PillarArrowConfig withLifetimeTicks(final int value) {
    return new PillarArrowConfig(enabled, heightBlocks, value);
  }
}
