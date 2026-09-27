package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record ChickenArrowConfig(boolean enabled) {

  public static final boolean DEFAULT_ENABLED = true;

  static final String KEY_ENABLED = "enabled";

  public ChickenArrowConfig {}

  public static ChickenArrowConfig defaults() {
    return new ChickenArrowConfig(DEFAULT_ENABLED);
  }

  public static ChickenArrowConfig fromJson(final JsonObject root) {
    final ChickenArrowConfig defaults = defaults();
    return new ChickenArrowConfig(ConfigValues.readBoolean(root, KEY_ENABLED, defaults.enabled()));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_ENABLED, enabled);
    return root;
  }

  public ChickenArrowConfig withEnabled(final boolean value) {
    return new ChickenArrowConfig(value);
  }
}
