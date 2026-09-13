package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record EnderArrowConfig(
    int pearlMaxRangeBlocks, int recallMaxRangeBlocks, boolean recallAffectsPlayers) {

  public static final int PEARL_MAX_RANGE_BLOCKS_MIN = 4;
  public static final int PEARL_MAX_RANGE_BLOCKS_MAX = 128;
  public static final int RECALL_MAX_RANGE_BLOCKS_MIN = 4;
  public static final int RECALL_MAX_RANGE_BLOCKS_MAX = 128;

  public static final int DEFAULT_PEARL_MAX_RANGE_BLOCKS = PEARL_MAX_RANGE_BLOCKS_MAX;
  public static final int DEFAULT_RECALL_MAX_RANGE_BLOCKS = RECALL_MAX_RANGE_BLOCKS_MAX;
  public static final boolean DEFAULT_RECALL_AFFECTS_PLAYERS = true;

  static final String KEY_PEARL_MAX_RANGE_BLOCKS = "pearlMaxRangeBlocks";
  static final String KEY_RECALL_MAX_RANGE_BLOCKS = "recallMaxRangeBlocks";
  static final String KEY_RECALL_AFFECTS_PLAYERS = "recallAffectsPlayers";

  public EnderArrowConfig {
    pearlMaxRangeBlocks =
        ConfigValues.clampInt(
            pearlMaxRangeBlocks, PEARL_MAX_RANGE_BLOCKS_MIN, PEARL_MAX_RANGE_BLOCKS_MAX);
    recallMaxRangeBlocks =
        ConfigValues.clampInt(
            recallMaxRangeBlocks, RECALL_MAX_RANGE_BLOCKS_MIN, RECALL_MAX_RANGE_BLOCKS_MAX);
  }

  public static EnderArrowConfig defaults() {
    return new EnderArrowConfig(
        DEFAULT_PEARL_MAX_RANGE_BLOCKS,
        DEFAULT_RECALL_MAX_RANGE_BLOCKS,
        DEFAULT_RECALL_AFFECTS_PLAYERS);
  }

  public static EnderArrowConfig fromJson(final JsonObject root) {
    final EnderArrowConfig defaults = defaults();
    return new EnderArrowConfig(
        ConfigValues.readInt(
            root,
            KEY_PEARL_MAX_RANGE_BLOCKS,
            defaults.pearlMaxRangeBlocks(),
            PEARL_MAX_RANGE_BLOCKS_MIN,
            PEARL_MAX_RANGE_BLOCKS_MAX),
        ConfigValues.readInt(
            root,
            KEY_RECALL_MAX_RANGE_BLOCKS,
            defaults.recallMaxRangeBlocks(),
            RECALL_MAX_RANGE_BLOCKS_MIN,
            RECALL_MAX_RANGE_BLOCKS_MAX),
        ConfigValues.readBoolean(
            root, KEY_RECALL_AFFECTS_PLAYERS, defaults.recallAffectsPlayers()));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_PEARL_MAX_RANGE_BLOCKS, pearlMaxRangeBlocks);
    root.addProperty(KEY_RECALL_MAX_RANGE_BLOCKS, recallMaxRangeBlocks);
    root.addProperty(KEY_RECALL_AFFECTS_PLAYERS, recallAffectsPlayers);
    return root;
  }

  public EnderArrowConfig withPearlMaxRangeBlocks(final int value) {
    return new EnderArrowConfig(value, recallMaxRangeBlocks, recallAffectsPlayers);
  }

  public EnderArrowConfig withRecallMaxRangeBlocks(final int value) {
    return new EnderArrowConfig(pearlMaxRangeBlocks, value, recallAffectsPlayers);
  }

  public EnderArrowConfig withRecallAffectsPlayers(final boolean value) {
    return new EnderArrowConfig(pearlMaxRangeBlocks, recallMaxRangeBlocks, value);
  }
}
