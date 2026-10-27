package com.grahambartley.volley.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigValues {
  public static final int MAX_IDENTIFIER_LENGTH = 64;

  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigValues.class);

  private ConfigValues() {}

  public static int clampInt(final int value, final int min, final int max) {
    return Math.max(min, Math.min(max, value));
  }

  public static float clampFloat(final float value, final float min, final float max) {
    return Math.max(min, Math.min(max, value));
  }

  public static int readInt(
      final JsonObject root, final String key, final int fallback, final int min, final int max) {
    if (root == null || !root.has(key)) {
      return fallback;
    }
    try {
      final int value = root.get(key).getAsInt();
      warnIfOutOfRange(key, value, min, max);
      return clampInt(value, min, max);
    } catch (final ClassCastException
        | IllegalStateException
        | NumberFormatException
        | UnsupportedOperationException ex) {
      warnUnreadable(key, root.get(key), fallback);
      return fallback;
    }
  }

  public static float readFloat(
      final JsonObject root,
      final String key,
      final float fallback,
      final float min,
      final float max) {
    if (root == null || !root.has(key)) {
      return fallback;
    }
    try {
      final float value = root.get(key).getAsFloat();
      warnIfOutOfRange(key, value, min, max);
      return clampFloat(value, min, max);
    } catch (final ClassCastException
        | IllegalStateException
        | NumberFormatException
        | UnsupportedOperationException ex) {
      warnUnreadable(key, root.get(key), fallback);
      return fallback;
    }
  }

  public static boolean readBoolean(
      final JsonObject root, final String key, final boolean fallback) {
    if (root == null || !root.has(key)) {
      return fallback;
    }
    final JsonElement element = root.get(key);
    if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isBoolean()) {
      warnUnreadable(key, element, fallback);
      return fallback;
    }
    return element.getAsBoolean();
  }

  public static JsonObject readObject(final JsonObject root, final String key) {
    if (root == null || !root.has(key) || !root.get(key).isJsonObject()) {
      return new JsonObject();
    }
    return root.getAsJsonObject(key);
  }

  public static List<String> readIdentifierList(
      final JsonObject root, final String key, final List<String> fallback) {
    if (root == null || !root.has(key) || !root.get(key).isJsonArray()) {
      return normalizeIdentifiers(fallback);
    }
    final JsonArray array = root.getAsJsonArray(key);
    final List<String> raw = new ArrayList<>(array.size());
    for (final JsonElement element : array) {
      if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
        raw.add(element.getAsString());
      } else {
        warnUnreadable(key, element, "dropped");
      }
    }
    return normalizeIdentifiers(raw);
  }

  public static List<String> normalizeIdentifiers(final List<String> raw) {
    return normalizeIdentifiers(raw, Integer.MAX_VALUE);
  }

  public static List<String> normalizeIdentifiers(final List<String> raw, final int maxEntries) {
    if (raw == null) {
      return List.of();
    }
    final Set<String> normalized = new LinkedHashSet<>();
    for (final String entry : raw) {
      if (entry == null || entry.isBlank()) {
        continue;
      }
      final Optional<String> parsed = IdentifierNames.normalize(entry);
      if (parsed.isEmpty()) {
        LOGGER.warn("Volley config identifier {} is not a valid identifier, dropping it", entry);
        continue;
      }
      final String identifier = parsed.get();
      if (identifier.length() > MAX_IDENTIFIER_LENGTH) {
        LOGGER.warn(
            "Volley config identifier {} is longer than {} characters, dropping it",
            identifier,
            MAX_IDENTIFIER_LENGTH);
        continue;
      }
      if (normalized.size() >= maxEntries) {
        LOGGER.warn(
            "Volley config identifier list exceeds {} entries, dropping {}",
            maxEntries,
            identifier);
        continue;
      }
      normalized.add(identifier);
    }
    return List.copyOf(normalized);
  }

  public static boolean containsIdentifier(final List<String> identifiers, final String queried) {
    return IdentifierNames.normalize(queried).map(identifiers::contains).orElse(false);
  }

  public static JsonArray toJsonArray(final List<String> values) {
    final JsonArray array = new JsonArray();
    values.forEach(array::add);
    return array;
  }

  private static void warnIfOutOfRange(
      final String key, final double value, final double min, final double max) {
    if (value < min || value > max) {
      LOGGER.warn(
          "Volley config value {}={} is outside [{}, {}] and has been clamped",
          key,
          value,
          min,
          max);
    }
  }

  private static void warnUnreadable(
      final String key, final JsonElement element, final Object fallback) {
    LOGGER.warn(
        "Volley config value {}={} could not be read, falling back to {}", key, element, fallback);
  }
}
