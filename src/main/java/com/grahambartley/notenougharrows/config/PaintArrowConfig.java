package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record PaintArrowConfig(boolean enabled) {

  public static final boolean DEFAULT_ENABLED = true;

  static final String KEY_ENABLED = "enabled";

  public PaintArrowConfig {}

  public static PaintArrowConfig defaults() {
    return new PaintArrowConfig(DEFAULT_ENABLED);
  }

  public static PaintArrowConfig fromJson(final JsonObject root) {
    final PaintArrowConfig defaults = defaults();
    return new PaintArrowConfig(ConfigValues.readBoolean(root, KEY_ENABLED, defaults.enabled()));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_ENABLED, enabled);
    return root;
  }

  public PaintArrowConfig withEnabled(final boolean value) {
    return new PaintArrowConfig(value);
  }
}
