package com.grahambartley.morearrows.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public final class ConfigCodec {
  public static final int MAX_ENCODED_LENGTH = 32767;

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  private ConfigCodec() {}

  public static String encode(final MoreArrowsConfig config) {
    return GSON.toJson(config.toJson());
  }

  public static MoreArrowsConfig decode(final String json) {
    if (json == null || json.isBlank()) {
      return MoreArrowsConfig.defaults();
    }
    final JsonObject root = GSON.fromJson(json, JsonObject.class);
    if (root == null) {
      throw new JsonParseException("Config root is not a JSON object");
    }
    return MoreArrowsConfig.fromJson(root);
  }

  public static MoreArrowsConfig decodeOrDefaults(final String json) {
    try {
      return decode(json);
    } catch (final JsonParseException | IllegalStateException ex) {
      return MoreArrowsConfig.defaults();
    }
  }
}
