package com.grahambartley.notenougharrows.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Optional;
import java.util.function.Predicate;

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
    return NotEnoughArrowsConfig.fromJson(parseRoot(json));
  }

  public static NotEnoughArrowsConfig decodeOrDefaults(final String json) {
    return tryDecode(json, root -> true).orElseGet(NotEnoughArrowsConfig::defaults);
  }

  public static Optional<NotEnoughArrowsConfig> decodeComplete(final String json) {
    return tryDecode(json, ConfigCodec::hasEveryFamily);
  }

  private static Optional<NotEnoughArrowsConfig> tryDecode(
      final String json, final Predicate<JsonObject> accepted) {
    if (json == null || json.isBlank()) {
      return Optional.empty();
    }
    try {
      final JsonObject root = parseRoot(json);
      return accepted.test(root)
          ? Optional.of(NotEnoughArrowsConfig.fromJson(root))
          : Optional.empty();
    } catch (final JsonParseException | IllegalStateException ex) {
      return Optional.empty();
    }
  }

  private static JsonObject parseRoot(final String json) {
    final JsonElement root = GSON.fromJson(json, JsonElement.class);
    if (root == null || !root.isJsonObject()) {
      throw new JsonParseException("Config root is not a JSON object");
    }
    return root.getAsJsonObject();
  }

  private static boolean hasEveryFamily(final JsonObject root) {
    return NotEnoughArrowsConfig.defaults().toJson().keySet().stream()
        .allMatch(family -> root.has(family) && root.get(family).isJsonObject());
  }
}
