package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record DrillArrowConfig(boolean enabled, int toolTier) {

  public static final int TOOL_TIER_MIN = 0;
  public static final int TOOL_TIER_MAX = 2;

  public static final boolean DEFAULT_ENABLED = true;
  public static final int DEFAULT_TOOL_TIER = 2;

  static final String KEY_ENABLED = "enabled";
  static final String KEY_TOOL_TIER = "toolTier";

  public DrillArrowConfig {
    toolTier = ConfigValues.clampInt(toolTier, TOOL_TIER_MIN, TOOL_TIER_MAX);
  }

  public static DrillArrowConfig defaults() {
    return new DrillArrowConfig(DEFAULT_ENABLED, DEFAULT_TOOL_TIER);
  }

  public static DrillArrowConfig fromJson(final JsonObject root) {
    final DrillArrowConfig defaults = defaults();
    return new DrillArrowConfig(
        ConfigValues.readBoolean(root, KEY_ENABLED, defaults.enabled()),
        ConfigValues.readInt(
            root, KEY_TOOL_TIER, defaults.toolTier(), TOOL_TIER_MIN, TOOL_TIER_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_ENABLED, enabled);
    root.addProperty(KEY_TOOL_TIER, toolTier);
    return root;
  }

  public DrillArrowConfig withEnabled(final boolean value) {
    return new DrillArrowConfig(value, toolTier);
  }

  public DrillArrowConfig withToolTier(final int value) {
    return new DrillArrowConfig(enabled, value);
  }
}
