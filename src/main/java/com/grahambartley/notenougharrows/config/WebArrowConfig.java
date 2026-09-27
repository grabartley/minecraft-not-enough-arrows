package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record WebArrowConfig(boolean enabled, int patchRadius, int lifetimeTicks) {

  public static final int PATCH_RADIUS_MIN = 0;
  public static final int PATCH_RADIUS_MAX = 3;
  public static final int LIFETIME_TICKS_MIN = 0;
  public static final int LIFETIME_TICKS_MAX = 12000;

  public static final boolean DEFAULT_ENABLED = true;
  public static final int DEFAULT_PATCH_RADIUS = 1;
  public static final int DEFAULT_LIFETIME_TICKS = 400;

  static final String KEY_ENABLED = "enabled";
  static final String KEY_PATCH_RADIUS = "patchRadius";
  static final String KEY_LIFETIME_TICKS = "lifetimeTicks";

  public WebArrowConfig {
    patchRadius = ConfigValues.clampInt(patchRadius, PATCH_RADIUS_MIN, PATCH_RADIUS_MAX);
    lifetimeTicks = ConfigValues.clampInt(lifetimeTicks, LIFETIME_TICKS_MIN, LIFETIME_TICKS_MAX);
  }

  public static WebArrowConfig defaults() {
    return new WebArrowConfig(DEFAULT_ENABLED, DEFAULT_PATCH_RADIUS, DEFAULT_LIFETIME_TICKS);
  }

  public static WebArrowConfig fromJson(final JsonObject root) {
    final WebArrowConfig defaults = defaults();
    return new WebArrowConfig(
        ConfigValues.readBoolean(root, KEY_ENABLED, defaults.enabled()),
        ConfigValues.readInt(
            root, KEY_PATCH_RADIUS, defaults.patchRadius(), PATCH_RADIUS_MIN, PATCH_RADIUS_MAX),
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
    root.addProperty(KEY_PATCH_RADIUS, patchRadius);
    root.addProperty(KEY_LIFETIME_TICKS, lifetimeTicks);
    return root;
  }

  public WebArrowConfig withEnabled(final boolean value) {
    return new WebArrowConfig(value, patchRadius, lifetimeTicks);
  }

  public WebArrowConfig withPatchRadius(final int value) {
    return new WebArrowConfig(enabled, value, lifetimeTicks);
  }

  public WebArrowConfig withLifetimeTicks(final int value) {
    return new WebArrowConfig(enabled, patchRadius, value);
  }
}
