package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record DisarmArrowConfig(boolean affectsPlayers) {

  public static final boolean DEFAULT_AFFECTS_PLAYERS = true;

  static final String KEY_AFFECTS_PLAYERS = "affectsPlayers";

  public static DisarmArrowConfig defaults() {
    return new DisarmArrowConfig(DEFAULT_AFFECTS_PLAYERS);
  }

  public DisarmArrowConfig withAffectsPlayers(final boolean value) {
    return new DisarmArrowConfig(value);
  }

  public static DisarmArrowConfig fromJson(final JsonObject root) {
    return new DisarmArrowConfig(
        ConfigValues.readBoolean(root, KEY_AFFECTS_PLAYERS, DEFAULT_AFFECTS_PLAYERS));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_AFFECTS_PLAYERS, affectsPlayers);
    return root;
  }
}
