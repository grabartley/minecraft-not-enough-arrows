package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;
import java.util.List;

public record ProspectorArrowConfig(int radius, int durationTicks, List<String> blocks) {

  public static final int RADIUS_MIN = 0;
  public static final int RADIUS_MAX = 16;
  public static final int DURATION_TICKS_MIN = 0;
  public static final int DURATION_TICKS_MAX = 1200;
  public static final int BLOCKS_MAX = 32;

  public static final int DEFAULT_RADIUS = 8;
  public static final int DEFAULT_DURATION_TICKS = 200;
  public static final List<String> DEFAULT_BLOCKS =
      List.of(
          "minecraft:coal_ore",
          "minecraft:deepslate_coal_ore",
          "minecraft:copper_ore",
          "minecraft:deepslate_copper_ore",
          "minecraft:iron_ore",
          "minecraft:deepslate_iron_ore",
          "minecraft:gold_ore",
          "minecraft:deepslate_gold_ore",
          "minecraft:redstone_ore",
          "minecraft:deepslate_redstone_ore",
          "minecraft:lapis_ore",
          "minecraft:deepslate_lapis_ore",
          "minecraft:diamond_ore",
          "minecraft:deepslate_diamond_ore",
          "minecraft:emerald_ore",
          "minecraft:deepslate_emerald_ore",
          "minecraft:nether_gold_ore",
          "minecraft:nether_quartz_ore",
          "minecraft:ancient_debris");

  static final String KEY_RADIUS = "radius";
  static final String KEY_DURATION_TICKS = "durationTicks";
  static final String KEY_BLOCKS = "blocks";

  public ProspectorArrowConfig {
    radius = ConfigValues.clampInt(radius, RADIUS_MIN, RADIUS_MAX);
    durationTicks = ConfigValues.clampInt(durationTicks, DURATION_TICKS_MIN, DURATION_TICKS_MAX);
    blocks = ConfigValues.normalizeIdentifiers(blocks, BLOCKS_MAX);
  }

  public static ProspectorArrowConfig defaults() {
    return new ProspectorArrowConfig(DEFAULT_RADIUS, DEFAULT_DURATION_TICKS, DEFAULT_BLOCKS);
  }

  public static ProspectorArrowConfig fromJson(final JsonObject root) {
    final ProspectorArrowConfig defaults = defaults();
    return new ProspectorArrowConfig(
        ConfigValues.readInt(root, KEY_RADIUS, defaults.radius(), RADIUS_MIN, RADIUS_MAX),
        ConfigValues.readInt(
            root,
            KEY_DURATION_TICKS,
            defaults.durationTicks(),
            DURATION_TICKS_MIN,
            DURATION_TICKS_MAX),
        ConfigValues.readIdentifierList(root, KEY_BLOCKS, defaults.blocks()));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_RADIUS, radius);
    root.addProperty(KEY_DURATION_TICKS, durationTicks);
    root.add(KEY_BLOCKS, ConfigValues.toJsonArray(blocks));
    return root;
  }

  public ProspectorArrowConfig withRadius(final int value) {
    return new ProspectorArrowConfig(value, durationTicks, blocks);
  }

  public ProspectorArrowConfig withDurationTicks(final int value) {
    return new ProspectorArrowConfig(radius, value, blocks);
  }

  public ProspectorArrowConfig withBlocks(final List<String> value) {
    return new ProspectorArrowConfig(radius, durationTicks, value);
  }
}
