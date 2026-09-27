package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record UpdraftArrowConfig(int heightBlocks, int lifetimeTicks, float strength) {

  public static final int HEIGHT_BLOCKS_MIN = 1;
  public static final int HEIGHT_BLOCKS_MAX = 64;
  public static final int LIFETIME_TICKS_MIN = 0;
  public static final int LIFETIME_TICKS_MAX = 1200;
  public static final float STRENGTH_MIN = 0.0f;
  public static final float STRENGTH_MAX = 2.0f;

  public static final int DEFAULT_HEIGHT_BLOCKS = 12;
  public static final int DEFAULT_LIFETIME_TICKS = 200;
  public static final float DEFAULT_STRENGTH = 0.4f;

  static final String KEY_HEIGHT_BLOCKS = "heightBlocks";
  static final String KEY_LIFETIME_TICKS = "lifetimeTicks";
  static final String KEY_STRENGTH = "strength";

  public UpdraftArrowConfig {
    heightBlocks = ConfigValues.clampInt(heightBlocks, HEIGHT_BLOCKS_MIN, HEIGHT_BLOCKS_MAX);
    lifetimeTicks = ConfigValues.clampInt(lifetimeTicks, LIFETIME_TICKS_MIN, LIFETIME_TICKS_MAX);
    strength = ConfigValues.clampFloat(strength, STRENGTH_MIN, STRENGTH_MAX);
  }

  public static UpdraftArrowConfig defaults() {
    return new UpdraftArrowConfig(DEFAULT_HEIGHT_BLOCKS, DEFAULT_LIFETIME_TICKS, DEFAULT_STRENGTH);
  }

  public static UpdraftArrowConfig fromJson(final JsonObject root) {
    final UpdraftArrowConfig defaults = defaults();
    return new UpdraftArrowConfig(
        ConfigValues.readInt(
            root, KEY_HEIGHT_BLOCKS, defaults.heightBlocks(), HEIGHT_BLOCKS_MIN, HEIGHT_BLOCKS_MAX),
        ConfigValues.readInt(
            root,
            KEY_LIFETIME_TICKS,
            defaults.lifetimeTicks(),
            LIFETIME_TICKS_MIN,
            LIFETIME_TICKS_MAX),
        ConfigValues.readFloat(
            root, KEY_STRENGTH, defaults.strength(), STRENGTH_MIN, STRENGTH_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_HEIGHT_BLOCKS, heightBlocks);
    root.addProperty(KEY_LIFETIME_TICKS, lifetimeTicks);
    root.addProperty(KEY_STRENGTH, strength);
    return root;
  }

  public UpdraftArrowConfig withHeightBlocks(final int value) {
    return new UpdraftArrowConfig(value, lifetimeTicks, strength);
  }

  public UpdraftArrowConfig withLifetimeTicks(final int value) {
    return new UpdraftArrowConfig(heightBlocks, value, strength);
  }

  public UpdraftArrowConfig withStrength(final float value) {
    return new UpdraftArrowConfig(heightBlocks, lifetimeTicks, value);
  }
}
