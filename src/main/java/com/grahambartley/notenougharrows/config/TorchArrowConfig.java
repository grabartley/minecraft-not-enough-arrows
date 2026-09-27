package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record TorchArrowConfig(boolean enabled) {

  public static final boolean DEFAULT_ENABLED = true;

  static final String KEY_ENABLED = "enabled";

  public TorchArrowConfig {}

  public static TorchArrowConfig defaults() {
    return new TorchArrowConfig(DEFAULT_ENABLED);
  }

  public static TorchArrowConfig fromJson(final JsonObject root) {
    final TorchArrowConfig defaults = defaults();
    return new TorchArrowConfig(ConfigValues.readBoolean(root, KEY_ENABLED, defaults.enabled()));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_ENABLED, enabled);
    return root;
  }

  public TorchArrowConfig withEnabled(final boolean value) {
    return new TorchArrowConfig(value);
  }
}
