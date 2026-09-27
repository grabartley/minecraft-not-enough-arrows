package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record PartyArrowConfig(boolean enabled) {

  public static final boolean DEFAULT_ENABLED = true;

  static final String KEY_ENABLED = "enabled";

  public PartyArrowConfig {}

  public static PartyArrowConfig defaults() {
    return new PartyArrowConfig(DEFAULT_ENABLED);
  }

  public static PartyArrowConfig fromJson(final JsonObject root) {
    final PartyArrowConfig defaults = defaults();
    return new PartyArrowConfig(ConfigValues.readBoolean(root, KEY_ENABLED, defaults.enabled()));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_ENABLED, enabled);
    return root;
  }

  public PartyArrowConfig withEnabled(final boolean value) {
    return new PartyArrowConfig(value);
  }
}
