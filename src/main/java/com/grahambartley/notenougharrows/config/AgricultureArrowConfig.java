package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record AgricultureArrowConfig(
    int blossomRadius, int tillRadius, int harvestRadius, int beeCount, int beeLifetimeTicks) {

  public static final int BLOSSOM_RADIUS_MIN = 0;
  public static final int BLOSSOM_RADIUS_MAX = 8;
  public static final int TILL_RADIUS_MIN = 0;
  public static final int TILL_RADIUS_MAX = 8;
  public static final int HARVEST_RADIUS_MIN = 0;
  public static final int HARVEST_RADIUS_MAX = 8;
  public static final int BEE_COUNT_MIN = 1;
  public static final int BEE_COUNT_MAX = 8;
  public static final int BEE_LIFETIME_TICKS_MIN = 20;
  public static final int BEE_LIFETIME_TICKS_MAX = 6000;

  public static final int DEFAULT_BLOSSOM_RADIUS = 2;
  public static final int DEFAULT_TILL_RADIUS = 2;
  public static final int DEFAULT_HARVEST_RADIUS = 3;
  public static final int DEFAULT_BEE_COUNT = 3;
  public static final int DEFAULT_BEE_LIFETIME_TICKS = 600;

  static final String KEY_BLOSSOM_RADIUS = "blossomRadius";
  static final String KEY_TILL_RADIUS = "tillRadius";
  static final String KEY_HARVEST_RADIUS = "harvestRadius";
  static final String KEY_BEE_COUNT = "beeCount";
  static final String KEY_BEE_LIFETIME_TICKS = "beeLifetimeTicks";

  public AgricultureArrowConfig {
    blossomRadius = ConfigValues.clampInt(blossomRadius, BLOSSOM_RADIUS_MIN, BLOSSOM_RADIUS_MAX);
    tillRadius = ConfigValues.clampInt(tillRadius, TILL_RADIUS_MIN, TILL_RADIUS_MAX);
    harvestRadius = ConfigValues.clampInt(harvestRadius, HARVEST_RADIUS_MIN, HARVEST_RADIUS_MAX);
    beeCount = ConfigValues.clampInt(beeCount, BEE_COUNT_MIN, BEE_COUNT_MAX);
    beeLifetimeTicks =
        ConfigValues.clampInt(beeLifetimeTicks, BEE_LIFETIME_TICKS_MIN, BEE_LIFETIME_TICKS_MAX);
  }

  public static AgricultureArrowConfig defaults() {
    return new AgricultureArrowConfig(
        DEFAULT_BLOSSOM_RADIUS,
        DEFAULT_TILL_RADIUS,
        DEFAULT_HARVEST_RADIUS,
        DEFAULT_BEE_COUNT,
        DEFAULT_BEE_LIFETIME_TICKS);
  }

  public static AgricultureArrowConfig fromJson(final JsonObject root) {
    final AgricultureArrowConfig defaults = defaults();
    return new AgricultureArrowConfig(
        ConfigValues.readInt(
            root,
            KEY_BLOSSOM_RADIUS,
            defaults.blossomRadius(),
            BLOSSOM_RADIUS_MIN,
            BLOSSOM_RADIUS_MAX),
        ConfigValues.readInt(
            root, KEY_TILL_RADIUS, defaults.tillRadius(), TILL_RADIUS_MIN, TILL_RADIUS_MAX),
        ConfigValues.readInt(
            root,
            KEY_HARVEST_RADIUS,
            defaults.harvestRadius(),
            HARVEST_RADIUS_MIN,
            HARVEST_RADIUS_MAX),
        ConfigValues.readInt(
            root, KEY_BEE_COUNT, defaults.beeCount(), BEE_COUNT_MIN, BEE_COUNT_MAX),
        ConfigValues.readInt(
            root,
            KEY_BEE_LIFETIME_TICKS,
            defaults.beeLifetimeTicks(),
            BEE_LIFETIME_TICKS_MIN,
            BEE_LIFETIME_TICKS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_BLOSSOM_RADIUS, blossomRadius);
    root.addProperty(KEY_TILL_RADIUS, tillRadius);
    root.addProperty(KEY_HARVEST_RADIUS, harvestRadius);
    root.addProperty(KEY_BEE_COUNT, beeCount);
    root.addProperty(KEY_BEE_LIFETIME_TICKS, beeLifetimeTicks);
    return root;
  }

  public AgricultureArrowConfig withBlossomRadius(final int value) {
    return new AgricultureArrowConfig(value, tillRadius, harvestRadius, beeCount, beeLifetimeTicks);
  }

  public AgricultureArrowConfig withTillRadius(final int value) {
    return new AgricultureArrowConfig(
        blossomRadius, value, harvestRadius, beeCount, beeLifetimeTicks);
  }

  public AgricultureArrowConfig withHarvestRadius(final int value) {
    return new AgricultureArrowConfig(blossomRadius, tillRadius, value, beeCount, beeLifetimeTicks);
  }

  public AgricultureArrowConfig withBeeCount(final int value) {
    return new AgricultureArrowConfig(
        blossomRadius, tillRadius, harvestRadius, value, beeLifetimeTicks);
  }

  public AgricultureArrowConfig withBeeLifetimeTicks(final int value) {
    return new AgricultureArrowConfig(blossomRadius, tillRadius, harvestRadius, beeCount, value);
  }
}
