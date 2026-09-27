package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record TerrainArrowConfig(
    boolean drillEnabled,
    int drillToolTier,
    boolean pillarEnabled,
    int pillarHeightBlocks,
    int pillarLifetimeTicks,
    boolean drainEnabled,
    int drainRadius,
    int drainMaxBlocks,
    boolean freezeEnabled,
    int freezeRadius,
    boolean webEnabled,
    int webPatchRadius,
    int webLifetimeTicks,
    boolean paintEnabled) {

  public static final int DRILL_TOOL_TIER_MIN = 0;
  public static final int DRILL_TOOL_TIER_MAX = 2;
  public static final int PILLAR_HEIGHT_BLOCKS_MIN = 1;
  public static final int PILLAR_HEIGHT_BLOCKS_MAX = 16;
  public static final int PILLAR_LIFETIME_TICKS_MIN = 0;
  public static final int PILLAR_LIFETIME_TICKS_MAX = 12000;
  public static final int DRAIN_RADIUS_MIN = 0;
  public static final int DRAIN_RADIUS_MAX = 8;
  public static final int DRAIN_MAX_BLOCKS_MIN = 1;
  public static final int DRAIN_MAX_BLOCKS_MAX = 512;
  public static final int FREEZE_RADIUS_MIN = 0;
  public static final int FREEZE_RADIUS_MAX = 8;
  public static final int WEB_PATCH_RADIUS_MIN = 0;
  public static final int WEB_PATCH_RADIUS_MAX = 3;
  public static final int WEB_LIFETIME_TICKS_MIN = 0;
  public static final int WEB_LIFETIME_TICKS_MAX = 12000;

  public static final boolean DEFAULT_DRILL_ENABLED = true;
  public static final int DEFAULT_DRILL_TOOL_TIER = 2;
  public static final boolean DEFAULT_PILLAR_ENABLED = true;
  public static final int DEFAULT_PILLAR_HEIGHT_BLOCKS = 4;
  public static final int DEFAULT_PILLAR_LIFETIME_TICKS = 600;
  public static final boolean DEFAULT_DRAIN_ENABLED = true;
  public static final int DEFAULT_DRAIN_RADIUS = 3;
  public static final int DEFAULT_DRAIN_MAX_BLOCKS = 65;
  public static final boolean DEFAULT_FREEZE_ENABLED = true;
  public static final int DEFAULT_FREEZE_RADIUS = 3;
  public static final boolean DEFAULT_WEB_ENABLED = true;
  public static final int DEFAULT_WEB_PATCH_RADIUS = 1;
  public static final int DEFAULT_WEB_LIFETIME_TICKS = 400;
  public static final boolean DEFAULT_PAINT_ENABLED = true;

  static final String KEY_DRILL_ENABLED = "drillEnabled";
  static final String KEY_DRILL_TOOL_TIER = "drillToolTier";
  static final String KEY_PILLAR_ENABLED = "pillarEnabled";
  static final String KEY_PILLAR_HEIGHT_BLOCKS = "pillarHeightBlocks";
  static final String KEY_PILLAR_LIFETIME_TICKS = "pillarLifetimeTicks";
  static final String KEY_DRAIN_ENABLED = "drainEnabled";
  static final String KEY_DRAIN_RADIUS = "drainRadius";
  static final String KEY_DRAIN_MAX_BLOCKS = "drainMaxBlocks";
  static final String KEY_FREEZE_ENABLED = "freezeEnabled";
  static final String KEY_FREEZE_RADIUS = "freezeRadius";
  static final String KEY_WEB_ENABLED = "webEnabled";
  static final String KEY_WEB_PATCH_RADIUS = "webPatchRadius";
  static final String KEY_WEB_LIFETIME_TICKS = "webLifetimeTicks";
  static final String KEY_PAINT_ENABLED = "paintEnabled";

  public TerrainArrowConfig {
    drillToolTier = ConfigValues.clampInt(drillToolTier, DRILL_TOOL_TIER_MIN, DRILL_TOOL_TIER_MAX);
    pillarHeightBlocks =
        ConfigValues.clampInt(
            pillarHeightBlocks, PILLAR_HEIGHT_BLOCKS_MIN, PILLAR_HEIGHT_BLOCKS_MAX);
    pillarLifetimeTicks =
        ConfigValues.clampInt(
            pillarLifetimeTicks, PILLAR_LIFETIME_TICKS_MIN, PILLAR_LIFETIME_TICKS_MAX);
    drainRadius = ConfigValues.clampInt(drainRadius, DRAIN_RADIUS_MIN, DRAIN_RADIUS_MAX);
    drainMaxBlocks =
        ConfigValues.clampInt(drainMaxBlocks, DRAIN_MAX_BLOCKS_MIN, DRAIN_MAX_BLOCKS_MAX);
    freezeRadius = ConfigValues.clampInt(freezeRadius, FREEZE_RADIUS_MIN, FREEZE_RADIUS_MAX);
    webPatchRadius =
        ConfigValues.clampInt(webPatchRadius, WEB_PATCH_RADIUS_MIN, WEB_PATCH_RADIUS_MAX);
    webLifetimeTicks =
        ConfigValues.clampInt(webLifetimeTicks, WEB_LIFETIME_TICKS_MIN, WEB_LIFETIME_TICKS_MAX);
  }

  public static TerrainArrowConfig defaults() {
    return new TerrainArrowConfig(
        DEFAULT_DRILL_ENABLED,
        DEFAULT_DRILL_TOOL_TIER,
        DEFAULT_PILLAR_ENABLED,
        DEFAULT_PILLAR_HEIGHT_BLOCKS,
        DEFAULT_PILLAR_LIFETIME_TICKS,
        DEFAULT_DRAIN_ENABLED,
        DEFAULT_DRAIN_RADIUS,
        DEFAULT_DRAIN_MAX_BLOCKS,
        DEFAULT_FREEZE_ENABLED,
        DEFAULT_FREEZE_RADIUS,
        DEFAULT_WEB_ENABLED,
        DEFAULT_WEB_PATCH_RADIUS,
        DEFAULT_WEB_LIFETIME_TICKS,
        DEFAULT_PAINT_ENABLED);
  }

  public static TerrainArrowConfig fromJson(final JsonObject root) {
    final TerrainArrowConfig defaults = defaults();
    return new TerrainArrowConfig(
        ConfigValues.readBoolean(root, KEY_DRILL_ENABLED, defaults.drillEnabled()),
        ConfigValues.readInt(
            root,
            KEY_DRILL_TOOL_TIER,
            defaults.drillToolTier(),
            DRILL_TOOL_TIER_MIN,
            DRILL_TOOL_TIER_MAX),
        ConfigValues.readBoolean(root, KEY_PILLAR_ENABLED, defaults.pillarEnabled()),
        ConfigValues.readInt(
            root,
            KEY_PILLAR_HEIGHT_BLOCKS,
            defaults.pillarHeightBlocks(),
            PILLAR_HEIGHT_BLOCKS_MIN,
            PILLAR_HEIGHT_BLOCKS_MAX),
        ConfigValues.readInt(
            root,
            KEY_PILLAR_LIFETIME_TICKS,
            defaults.pillarLifetimeTicks(),
            PILLAR_LIFETIME_TICKS_MIN,
            PILLAR_LIFETIME_TICKS_MAX),
        ConfigValues.readBoolean(root, KEY_DRAIN_ENABLED, defaults.drainEnabled()),
        ConfigValues.readInt(
            root, KEY_DRAIN_RADIUS, defaults.drainRadius(), DRAIN_RADIUS_MIN, DRAIN_RADIUS_MAX),
        ConfigValues.readInt(
            root,
            KEY_DRAIN_MAX_BLOCKS,
            defaults.drainMaxBlocks(),
            DRAIN_MAX_BLOCKS_MIN,
            DRAIN_MAX_BLOCKS_MAX),
        ConfigValues.readBoolean(root, KEY_FREEZE_ENABLED, defaults.freezeEnabled()),
        ConfigValues.readInt(
            root, KEY_FREEZE_RADIUS, defaults.freezeRadius(), FREEZE_RADIUS_MIN, FREEZE_RADIUS_MAX),
        ConfigValues.readBoolean(root, KEY_WEB_ENABLED, defaults.webEnabled()),
        ConfigValues.readInt(
            root,
            KEY_WEB_PATCH_RADIUS,
            defaults.webPatchRadius(),
            WEB_PATCH_RADIUS_MIN,
            WEB_PATCH_RADIUS_MAX),
        ConfigValues.readInt(
            root,
            KEY_WEB_LIFETIME_TICKS,
            defaults.webLifetimeTicks(),
            WEB_LIFETIME_TICKS_MIN,
            WEB_LIFETIME_TICKS_MAX),
        ConfigValues.readBoolean(root, KEY_PAINT_ENABLED, defaults.paintEnabled()));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_DRILL_ENABLED, drillEnabled);
    root.addProperty(KEY_DRILL_TOOL_TIER, drillToolTier);
    root.addProperty(KEY_PILLAR_ENABLED, pillarEnabled);
    root.addProperty(KEY_PILLAR_HEIGHT_BLOCKS, pillarHeightBlocks);
    root.addProperty(KEY_PILLAR_LIFETIME_TICKS, pillarLifetimeTicks);
    root.addProperty(KEY_DRAIN_ENABLED, drainEnabled);
    root.addProperty(KEY_DRAIN_RADIUS, drainRadius);
    root.addProperty(KEY_DRAIN_MAX_BLOCKS, drainMaxBlocks);
    root.addProperty(KEY_FREEZE_ENABLED, freezeEnabled);
    root.addProperty(KEY_FREEZE_RADIUS, freezeRadius);
    root.addProperty(KEY_WEB_ENABLED, webEnabled);
    root.addProperty(KEY_WEB_PATCH_RADIUS, webPatchRadius);
    root.addProperty(KEY_WEB_LIFETIME_TICKS, webLifetimeTicks);
    root.addProperty(KEY_PAINT_ENABLED, paintEnabled);
    return root;
  }

  public TerrainArrowConfig withDrillEnabled(final boolean value) {
    return new TerrainArrowConfig(
        value,
        drillToolTier,
        pillarEnabled,
        pillarHeightBlocks,
        pillarLifetimeTicks,
        drainEnabled,
        drainRadius,
        drainMaxBlocks,
        freezeEnabled,
        freezeRadius,
        webEnabled,
        webPatchRadius,
        webLifetimeTicks,
        paintEnabled);
  }

  public TerrainArrowConfig withDrillToolTier(final int value) {
    return new TerrainArrowConfig(
        drillEnabled,
        value,
        pillarEnabled,
        pillarHeightBlocks,
        pillarLifetimeTicks,
        drainEnabled,
        drainRadius,
        drainMaxBlocks,
        freezeEnabled,
        freezeRadius,
        webEnabled,
        webPatchRadius,
        webLifetimeTicks,
        paintEnabled);
  }

  public TerrainArrowConfig withPillarEnabled(final boolean value) {
    return new TerrainArrowConfig(
        drillEnabled,
        drillToolTier,
        value,
        pillarHeightBlocks,
        pillarLifetimeTicks,
        drainEnabled,
        drainRadius,
        drainMaxBlocks,
        freezeEnabled,
        freezeRadius,
        webEnabled,
        webPatchRadius,
        webLifetimeTicks,
        paintEnabled);
  }

  public TerrainArrowConfig withPillarHeightBlocks(final int value) {
    return new TerrainArrowConfig(
        drillEnabled,
        drillToolTier,
        pillarEnabled,
        value,
        pillarLifetimeTicks,
        drainEnabled,
        drainRadius,
        drainMaxBlocks,
        freezeEnabled,
        freezeRadius,
        webEnabled,
        webPatchRadius,
        webLifetimeTicks,
        paintEnabled);
  }

  public TerrainArrowConfig withPillarLifetimeTicks(final int value) {
    return new TerrainArrowConfig(
        drillEnabled,
        drillToolTier,
        pillarEnabled,
        pillarHeightBlocks,
        value,
        drainEnabled,
        drainRadius,
        drainMaxBlocks,
        freezeEnabled,
        freezeRadius,
        webEnabled,
        webPatchRadius,
        webLifetimeTicks,
        paintEnabled);
  }

  public TerrainArrowConfig withDrainEnabled(final boolean value) {
    return new TerrainArrowConfig(
        drillEnabled,
        drillToolTier,
        pillarEnabled,
        pillarHeightBlocks,
        pillarLifetimeTicks,
        value,
        drainRadius,
        drainMaxBlocks,
        freezeEnabled,
        freezeRadius,
        webEnabled,
        webPatchRadius,
        webLifetimeTicks,
        paintEnabled);
  }

  public TerrainArrowConfig withDrainRadius(final int value) {
    return new TerrainArrowConfig(
        drillEnabled,
        drillToolTier,
        pillarEnabled,
        pillarHeightBlocks,
        pillarLifetimeTicks,
        drainEnabled,
        value,
        drainMaxBlocks,
        freezeEnabled,
        freezeRadius,
        webEnabled,
        webPatchRadius,
        webLifetimeTicks,
        paintEnabled);
  }

  public TerrainArrowConfig withDrainMaxBlocks(final int value) {
    return new TerrainArrowConfig(
        drillEnabled,
        drillToolTier,
        pillarEnabled,
        pillarHeightBlocks,
        pillarLifetimeTicks,
        drainEnabled,
        drainRadius,
        value,
        freezeEnabled,
        freezeRadius,
        webEnabled,
        webPatchRadius,
        webLifetimeTicks,
        paintEnabled);
  }

  public TerrainArrowConfig withFreezeEnabled(final boolean value) {
    return new TerrainArrowConfig(
        drillEnabled,
        drillToolTier,
        pillarEnabled,
        pillarHeightBlocks,
        pillarLifetimeTicks,
        drainEnabled,
        drainRadius,
        drainMaxBlocks,
        value,
        freezeRadius,
        webEnabled,
        webPatchRadius,
        webLifetimeTicks,
        paintEnabled);
  }

  public TerrainArrowConfig withFreezeRadius(final int value) {
    return new TerrainArrowConfig(
        drillEnabled,
        drillToolTier,
        pillarEnabled,
        pillarHeightBlocks,
        pillarLifetimeTicks,
        drainEnabled,
        drainRadius,
        drainMaxBlocks,
        freezeEnabled,
        value,
        webEnabled,
        webPatchRadius,
        webLifetimeTicks,
        paintEnabled);
  }

  public TerrainArrowConfig withWebEnabled(final boolean value) {
    return new TerrainArrowConfig(
        drillEnabled,
        drillToolTier,
        pillarEnabled,
        pillarHeightBlocks,
        pillarLifetimeTicks,
        drainEnabled,
        drainRadius,
        drainMaxBlocks,
        freezeEnabled,
        freezeRadius,
        value,
        webPatchRadius,
        webLifetimeTicks,
        paintEnabled);
  }

  public TerrainArrowConfig withWebPatchRadius(final int value) {
    return new TerrainArrowConfig(
        drillEnabled,
        drillToolTier,
        pillarEnabled,
        pillarHeightBlocks,
        pillarLifetimeTicks,
        drainEnabled,
        drainRadius,
        drainMaxBlocks,
        freezeEnabled,
        freezeRadius,
        webEnabled,
        value,
        webLifetimeTicks,
        paintEnabled);
  }

  public TerrainArrowConfig withWebLifetimeTicks(final int value) {
    return new TerrainArrowConfig(
        drillEnabled,
        drillToolTier,
        pillarEnabled,
        pillarHeightBlocks,
        pillarLifetimeTicks,
        drainEnabled,
        drainRadius,
        drainMaxBlocks,
        freezeEnabled,
        freezeRadius,
        webEnabled,
        webPatchRadius,
        value,
        paintEnabled);
  }

  public TerrainArrowConfig withPaintEnabled(final boolean value) {
    return new TerrainArrowConfig(
        drillEnabled,
        drillToolTier,
        pillarEnabled,
        pillarHeightBlocks,
        pillarLifetimeTicks,
        drainEnabled,
        drainRadius,
        drainMaxBlocks,
        freezeEnabled,
        freezeRadius,
        webEnabled,
        webPatchRadius,
        webLifetimeTicks,
        value);
  }
}
