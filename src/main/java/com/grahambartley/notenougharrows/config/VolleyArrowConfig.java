package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record VolleyArrowConfig(
    int fragmentCount, float damageShare, float spreadDegrees, int splitDelayTicks) {

  public static final int FRAGMENT_COUNT_MIN = 2;
  public static final int FRAGMENT_COUNT_MAX = 12;
  public static final float DAMAGE_SHARE_MIN = 0.0f;
  public static final float DAMAGE_SHARE_MAX = 1.0f;
  public static final float SPREAD_DEGREES_MIN = 0.0f;
  public static final float SPREAD_DEGREES_MAX = 45.0f;
  public static final int SPLIT_DELAY_TICKS_MIN = 1;
  public static final int SPLIT_DELAY_TICKS_MAX = 40;

  public static final int DEFAULT_FRAGMENT_COUNT = 5;
  public static final float DEFAULT_DAMAGE_SHARE = 0.4f;
  public static final float DEFAULT_SPREAD_DEGREES = 10.0f;
  public static final int DEFAULT_SPLIT_DELAY_TICKS = 4;

  static final String KEY_FRAGMENT_COUNT = "fragmentCount";
  static final String KEY_DAMAGE_SHARE = "damageShare";
  static final String KEY_SPREAD_DEGREES = "spreadDegrees";
  static final String KEY_SPLIT_DELAY_TICKS = "splitDelayTicks";

  public VolleyArrowConfig {
    fragmentCount = ConfigValues.clampInt(fragmentCount, FRAGMENT_COUNT_MIN, FRAGMENT_COUNT_MAX);
    damageShare = ConfigValues.clampFloat(damageShare, DAMAGE_SHARE_MIN, DAMAGE_SHARE_MAX);
    spreadDegrees = ConfigValues.clampFloat(spreadDegrees, SPREAD_DEGREES_MIN, SPREAD_DEGREES_MAX);
    splitDelayTicks =
        ConfigValues.clampInt(splitDelayTicks, SPLIT_DELAY_TICKS_MIN, SPLIT_DELAY_TICKS_MAX);
  }

  public static VolleyArrowConfig defaults() {
    return new VolleyArrowConfig(
        DEFAULT_FRAGMENT_COUNT,
        DEFAULT_DAMAGE_SHARE,
        DEFAULT_SPREAD_DEGREES,
        DEFAULT_SPLIT_DELAY_TICKS);
  }

  public VolleyArrowConfig withFragmentCount(final int value) {
    return new VolleyArrowConfig(value, damageShare, spreadDegrees, splitDelayTicks);
  }

  public VolleyArrowConfig withDamageShare(final float value) {
    return new VolleyArrowConfig(fragmentCount, value, spreadDegrees, splitDelayTicks);
  }

  public VolleyArrowConfig withSpreadDegrees(final float value) {
    return new VolleyArrowConfig(fragmentCount, damageShare, value, splitDelayTicks);
  }

  public VolleyArrowConfig withSplitDelayTicks(final int value) {
    return new VolleyArrowConfig(fragmentCount, damageShare, spreadDegrees, value);
  }

  public static VolleyArrowConfig fromJson(final JsonObject root) {
    final VolleyArrowConfig defaults = defaults();
    return new VolleyArrowConfig(
        ConfigValues.readInt(
            root,
            KEY_FRAGMENT_COUNT,
            defaults.fragmentCount(),
            FRAGMENT_COUNT_MIN,
            FRAGMENT_COUNT_MAX),
        ConfigValues.readFloat(
            root, KEY_DAMAGE_SHARE, defaults.damageShare(), DAMAGE_SHARE_MIN, DAMAGE_SHARE_MAX),
        ConfigValues.readFloat(
            root,
            KEY_SPREAD_DEGREES,
            defaults.spreadDegrees(),
            SPREAD_DEGREES_MIN,
            SPREAD_DEGREES_MAX),
        ConfigValues.readInt(
            root,
            KEY_SPLIT_DELAY_TICKS,
            defaults.splitDelayTicks(),
            SPLIT_DELAY_TICKS_MIN,
            SPLIT_DELAY_TICKS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_FRAGMENT_COUNT, fragmentCount);
    root.addProperty(KEY_DAMAGE_SHARE, damageShare);
    root.addProperty(KEY_SPREAD_DEGREES, spreadDegrees);
    root.addProperty(KEY_SPLIT_DELAY_TICKS, splitDelayTicks);
    return root;
  }
}
