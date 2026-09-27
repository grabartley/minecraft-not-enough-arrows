package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record FreezeArrowConfig(boolean enabled, int radius, int maxBlocks) {

  public static final int RADIUS_MIN = 0;
  public static final int RADIUS_MAX = 8;
  public static final int MAX_BLOCKS_MIN = 1;
  public static final int MAX_BLOCKS_MAX = 512;

  public static final boolean DEFAULT_ENABLED = true;
  public static final int DEFAULT_RADIUS = 3;
  public static final int DEFAULT_MAX_BLOCKS = 65;

  static final String KEY_ENABLED = "enabled";
  static final String KEY_RADIUS = "radius";
  static final String KEY_MAX_BLOCKS = "maxBlocks";

  public FreezeArrowConfig {
    radius = ConfigValues.clampInt(radius, RADIUS_MIN, RADIUS_MAX);
    maxBlocks = ConfigValues.clampInt(maxBlocks, MAX_BLOCKS_MIN, MAX_BLOCKS_MAX);
  }

  public static FreezeArrowConfig defaults() {
    return new FreezeArrowConfig(DEFAULT_ENABLED, DEFAULT_RADIUS, DEFAULT_MAX_BLOCKS);
  }

  public static FreezeArrowConfig fromJson(final JsonObject root) {
    final FreezeArrowConfig defaults = defaults();
    return new FreezeArrowConfig(
        ConfigValues.readBoolean(root, KEY_ENABLED, defaults.enabled()),
        ConfigValues.readInt(root, KEY_RADIUS, defaults.radius(), RADIUS_MIN, RADIUS_MAX),
        ConfigValues.readInt(
            root, KEY_MAX_BLOCKS, defaults.maxBlocks(), MAX_BLOCKS_MIN, MAX_BLOCKS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_ENABLED, enabled);
    root.addProperty(KEY_RADIUS, radius);
    root.addProperty(KEY_MAX_BLOCKS, maxBlocks);
    return root;
  }

  public FreezeArrowConfig withEnabled(final boolean value) {
    return new FreezeArrowConfig(value, radius, maxBlocks);
  }

  public FreezeArrowConfig withRadius(final int value) {
    return new FreezeArrowConfig(enabled, value, maxBlocks);
  }

  public FreezeArrowConfig withMaxBlocks(final int value) {
    return new FreezeArrowConfig(enabled, radius, value);
  }
}
