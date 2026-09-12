package com.grahambartley.notenougharrows.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public final class ConfigCodec {
  public static final int MAX_ENCODED_LENGTH = 32767;

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  private ConfigCodec() {}

  public static String encode(final NotEnoughArrowsConfig config) {
    return GSON.toJson(config.toJson());
  }

  public static NotEnoughArrowsConfig decode(final String json) {
    if (json == null || json.isBlank()) {
      return NotEnoughArrowsConfig.defaults();
    }
    final JsonElement root = GSON.fromJson(json, JsonElement.class);
    if (root == null || !root.isJsonObject()) {
      throw new JsonParseException("Config root is not a JSON object");
    }
    return NotEnoughArrowsConfig.fromJson(root.getAsJsonObject());
  }

  public static NotEnoughArrowsConfig decodeOrDefaults(final String json) {
    try {
      return decode(json);
    } catch (final JsonParseException | IllegalStateException ex) {
      return NotEnoughArrowsConfig.defaults();
    }
  }
}
