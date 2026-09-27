package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record SocialArrowConfig(
    CourierArrowConfig courier, SnowGolemArrowConfig snowGolem, MagnetArrowConfig magnet) {

  static final String KEY_COURIER = "courier";
  static final String KEY_SNOWGOLEM = "snowGolem";
  static final String KEY_MAGNET = "magnet";

  public SocialArrowConfig {
    courier = courier == null ? CourierArrowConfig.defaults() : courier;
    snowGolem = snowGolem == null ? SnowGolemArrowConfig.defaults() : snowGolem;
    magnet = magnet == null ? MagnetArrowConfig.defaults() : magnet;
  }

  public static SocialArrowConfig defaults() {
    return new SocialArrowConfig(
        CourierArrowConfig.defaults(),
        SnowGolemArrowConfig.defaults(),
        MagnetArrowConfig.defaults());
  }

  public SocialArrowConfig withCourier(final CourierArrowConfig value) {
    return new SocialArrowConfig(value, snowGolem, magnet);
  }

  public SocialArrowConfig withSnowGolem(final SnowGolemArrowConfig value) {
    return new SocialArrowConfig(courier, value, magnet);
  }

  public SocialArrowConfig withMagnet(final MagnetArrowConfig value) {
    return new SocialArrowConfig(courier, snowGolem, value);
  }

  public static SocialArrowConfig fromJson(final JsonObject root) {
    return new SocialArrowConfig(
        CourierArrowConfig.fromJson(ConfigValues.readObject(root, KEY_COURIER)),
        SnowGolemArrowConfig.fromJson(ConfigValues.readObject(root, KEY_SNOWGOLEM)),
        MagnetArrowConfig.fromJson(ConfigValues.readObject(root, KEY_MAGNET)));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.add(KEY_COURIER, courier.toJson());
    root.add(KEY_SNOWGOLEM, snowGolem.toJson());
    root.add(KEY_MAGNET, magnet.toJson());
    return root;
  }
}
