package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record VineArrowConfig(int lengthBlocks) {

  public static final int LENGTH_BLOCKS_MIN = 1;
  public static final int LENGTH_BLOCKS_MAX = 64;

  public static final int DEFAULT_LENGTH_BLOCKS = 12;

  static final String KEY_LENGTH_BLOCKS = "lengthBlocks";

  public VineArrowConfig {
    lengthBlocks = ConfigValues.clampInt(lengthBlocks, LENGTH_BLOCKS_MIN, LENGTH_BLOCKS_MAX);
  }

  public static VineArrowConfig defaults() {
    return new VineArrowConfig(DEFAULT_LENGTH_BLOCKS);
  }

  public static VineArrowConfig fromJson(final JsonObject root) {
    final VineArrowConfig defaults = defaults();
    return new VineArrowConfig(
        ConfigValues.readInt(
            root,
            KEY_LENGTH_BLOCKS,
            defaults.lengthBlocks(),
            LENGTH_BLOCKS_MIN,
            LENGTH_BLOCKS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_LENGTH_BLOCKS, lengthBlocks);
    return root;
  }

  public VineArrowConfig withLengthBlocks(final int value) {
    return new VineArrowConfig(value);
  }
}
