package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.Locale;

public record CourierArrowConfig(int maxPayload, List<String> undeliverable) {

  public static final int MAX_PAYLOAD_MIN = 1;
  public static final int MAX_PAYLOAD_MAX = 64;
  public static final int UNDELIVERABLE_MAX = 32;

  public static final int DEFAULT_MAX_PAYLOAD = 64;
  public static final List<String> DEFAULT_UNDELIVERABLE = List.of();

  static final String KEY_MAX_PAYLOAD = "maxPayload";
  static final String KEY_UNDELIVERABLE = "undeliverable";

  private static final String VANILLA_NAMESPACE = "minecraft:";

  public CourierArrowConfig {
    maxPayload = ConfigValues.clampInt(maxPayload, MAX_PAYLOAD_MIN, MAX_PAYLOAD_MAX);
    undeliverable = ConfigValues.normalizeIdentifiers(undeliverable, UNDELIVERABLE_MAX);
  }

  public static CourierArrowConfig defaults() {
    return new CourierArrowConfig(DEFAULT_MAX_PAYLOAD, DEFAULT_UNDELIVERABLE);
  }

  public boolean isUndeliverable(final String itemId) {
    if (itemId == null) {
      return false;
    }
    final String normalized = itemId.trim().toLowerCase(Locale.ROOT);
    return undeliverable.contains(normalized)
        || undeliverable.contains(withoutVanillaNamespace(normalized));
  }

  public boolean exceedsPayload(final int count) {
    return count > maxPayload;
  }

  private static String withoutVanillaNamespace(final String itemId) {
    return itemId.startsWith(VANILLA_NAMESPACE)
        ? itemId.substring(VANILLA_NAMESPACE.length())
        : itemId;
  }

  public static CourierArrowConfig fromJson(final JsonObject root) {
    final CourierArrowConfig defaults = defaults();
    return new CourierArrowConfig(
        ConfigValues.readInt(
            root, KEY_MAX_PAYLOAD, defaults.maxPayload(), MAX_PAYLOAD_MIN, MAX_PAYLOAD_MAX),
        ConfigValues.readIdentifierList(root, KEY_UNDELIVERABLE, defaults.undeliverable()));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_MAX_PAYLOAD, maxPayload);
    root.add(KEY_UNDELIVERABLE, ConfigValues.toJsonArray(undeliverable));
    return root;
  }

  public CourierArrowConfig withMaxPayload(final int value) {
    return new CourierArrowConfig(value, undeliverable);
  }

  public CourierArrowConfig withUndeliverable(final List<String> value) {
    return new CourierArrowConfig(maxPayload, value);
  }
}
