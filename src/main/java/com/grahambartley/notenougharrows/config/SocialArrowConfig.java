package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;
import java.util.List;

public record SocialArrowConfig(
    int courierMaxPayload,
    List<String> courierUndeliverable,
    int snowGolemLifetimeTicks,
    int magnetRadius) {

  public static final int COURIER_MAX_PAYLOAD_MIN = 1;
  public static final int COURIER_MAX_PAYLOAD_MAX = 64;
  public static final int COURIER_UNDELIVERABLE_MAX = 64;
  public static final int SNOW_GOLEM_LIFETIME_TICKS_MIN = 20;
  public static final int SNOW_GOLEM_LIFETIME_TICKS_MAX = 12000;
  public static final int MAGNET_RADIUS_MIN = 0;
  public static final int MAGNET_RADIUS_MAX = 16;

  public static final int DEFAULT_COURIER_MAX_PAYLOAD = 64;
  public static final List<String> DEFAULT_COURIER_UNDELIVERABLE = List.of();
  public static final int DEFAULT_SNOW_GOLEM_LIFETIME_TICKS = 1200;
  public static final int DEFAULT_MAGNET_RADIUS = 8;

  static final String KEY_COURIER_MAX_PAYLOAD = "courierMaxPayload";
  static final String KEY_COURIER_UNDELIVERABLE = "courierUndeliverable";
  static final String KEY_SNOW_GOLEM_LIFETIME_TICKS = "snowGolemLifetimeTicks";
  static final String KEY_MAGNET_RADIUS = "magnetRadius";

  public SocialArrowConfig {
    courierMaxPayload =
        ConfigValues.clampInt(courierMaxPayload, COURIER_MAX_PAYLOAD_MIN, COURIER_MAX_PAYLOAD_MAX);
    courierUndeliverable =
        ConfigValues.normalizeIdentifiers(courierUndeliverable, COURIER_UNDELIVERABLE_MAX);
    snowGolemLifetimeTicks =
        ConfigValues.clampInt(
            snowGolemLifetimeTicks, SNOW_GOLEM_LIFETIME_TICKS_MIN, SNOW_GOLEM_LIFETIME_TICKS_MAX);
    magnetRadius = ConfigValues.clampInt(magnetRadius, MAGNET_RADIUS_MIN, MAGNET_RADIUS_MAX);
  }

  public static SocialArrowConfig defaults() {
    return new SocialArrowConfig(
        DEFAULT_COURIER_MAX_PAYLOAD,
        DEFAULT_COURIER_UNDELIVERABLE,
        DEFAULT_SNOW_GOLEM_LIFETIME_TICKS,
        DEFAULT_MAGNET_RADIUS);
  }

  public static SocialArrowConfig fromJson(final JsonObject root) {
    final SocialArrowConfig defaults = defaults();
    return new SocialArrowConfig(
        ConfigValues.readInt(
            root,
            KEY_COURIER_MAX_PAYLOAD,
            defaults.courierMaxPayload(),
            COURIER_MAX_PAYLOAD_MIN,
            COURIER_MAX_PAYLOAD_MAX),
        ConfigValues.readIdentifierList(
            root, KEY_COURIER_UNDELIVERABLE, defaults.courierUndeliverable()),
        ConfigValues.readInt(
            root,
            KEY_SNOW_GOLEM_LIFETIME_TICKS,
            defaults.snowGolemLifetimeTicks(),
            SNOW_GOLEM_LIFETIME_TICKS_MIN,
            SNOW_GOLEM_LIFETIME_TICKS_MAX),
        ConfigValues.readInt(
            root,
            KEY_MAGNET_RADIUS,
            defaults.magnetRadius(),
            MAGNET_RADIUS_MIN,
            MAGNET_RADIUS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_COURIER_MAX_PAYLOAD, courierMaxPayload);
    root.add(KEY_COURIER_UNDELIVERABLE, ConfigValues.toJsonArray(courierUndeliverable));
    root.addProperty(KEY_SNOW_GOLEM_LIFETIME_TICKS, snowGolemLifetimeTicks);
    root.addProperty(KEY_MAGNET_RADIUS, magnetRadius);
    return root;
  }

  public SocialArrowConfig withCourierMaxPayload(final int value) {
    return new SocialArrowConfig(value, courierUndeliverable, snowGolemLifetimeTicks, magnetRadius);
  }

  public SocialArrowConfig withCourierUndeliverable(final List<String> value) {
    return new SocialArrowConfig(courierMaxPayload, value, snowGolemLifetimeTicks, magnetRadius);
  }

  public SocialArrowConfig withSnowGolemLifetimeTicks(final int value) {
    return new SocialArrowConfig(courierMaxPayload, courierUndeliverable, value, magnetRadius);
  }

  public SocialArrowConfig withMagnetRadius(final int value) {
    return new SocialArrowConfig(
        courierMaxPayload, courierUndeliverable, snowGolemLifetimeTicks, value);
  }
}
