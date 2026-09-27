package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record BoomerangArrowConfig(boolean enabled) {

  public static final boolean DEFAULT_ENABLED = true;

  static final String KEY_ENABLED = "enabled";

  public BoomerangArrowConfig {}

  public static BoomerangArrowConfig defaults() {
    return new BoomerangArrowConfig(DEFAULT_ENABLED);
  }

  public static BoomerangArrowConfig fromJson(final JsonObject root) {
    final BoomerangArrowConfig defaults = defaults();
    return new BoomerangArrowConfig(
        ConfigValues.readBoolean(root, KEY_ENABLED, defaults.enabled()));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_ENABLED, enabled);
    return root;
  }

  public BoomerangArrowConfig withEnabled(final boolean value) {
    return new BoomerangArrowConfig(value);
  }
}
