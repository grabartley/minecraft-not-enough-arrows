package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record HomingArrowConfig(float turnRate, float searchRadius, float searchConeDegrees) {

  public static final float TURN_RATE_MIN = 0.0f;
  public static final float TURN_RATE_MAX = 1.0f;
  public static final float SEARCH_RADIUS_MIN = 0.0f;
  public static final float SEARCH_RADIUS_MAX = 64.0f;
  public static final float SEARCH_CONE_DEGREES_MIN = 0.0f;
  public static final float SEARCH_CONE_DEGREES_MAX = 180.0f;

  public static final float DEFAULT_TURN_RATE = 0.2f;
  public static final float DEFAULT_SEARCH_RADIUS = 16.0f;
  public static final float DEFAULT_SEARCH_CONE_DEGREES = 60.0f;

  static final String KEY_TURN_RATE = "turnRate";
  static final String KEY_SEARCH_RADIUS = "searchRadius";
  static final String KEY_SEARCH_CONE_DEGREES = "searchConeDegrees";

  public HomingArrowConfig {
    turnRate = ConfigValues.clampFloat(turnRate, TURN_RATE_MIN, TURN_RATE_MAX);
    searchRadius = ConfigValues.clampFloat(searchRadius, SEARCH_RADIUS_MIN, SEARCH_RADIUS_MAX);
    searchConeDegrees =
        ConfigValues.clampFloat(
            searchConeDegrees, SEARCH_CONE_DEGREES_MIN, SEARCH_CONE_DEGREES_MAX);
  }

  public static HomingArrowConfig defaults() {
    return new HomingArrowConfig(
        DEFAULT_TURN_RATE, DEFAULT_SEARCH_RADIUS, DEFAULT_SEARCH_CONE_DEGREES);
  }

  public boolean seeks() {
    return turnRate > TURN_RATE_MIN && searchRadius > SEARCH_RADIUS_MIN;
  }

  public HomingArrowConfig withTurnRate(final float value) {
    return new HomingArrowConfig(value, searchRadius, searchConeDegrees);
  }

  public HomingArrowConfig withSearchRadius(final float value) {
    return new HomingArrowConfig(turnRate, value, searchConeDegrees);
  }

  public HomingArrowConfig withSearchConeDegrees(final float value) {
    return new HomingArrowConfig(turnRate, searchRadius, value);
  }

  public static HomingArrowConfig fromJson(final JsonObject root) {
    final HomingArrowConfig defaults = defaults();
    return new HomingArrowConfig(
        ConfigValues.readFloat(
            root, KEY_TURN_RATE, defaults.turnRate(), TURN_RATE_MIN, TURN_RATE_MAX),
        ConfigValues.readFloat(
            root, KEY_SEARCH_RADIUS, defaults.searchRadius(), SEARCH_RADIUS_MIN, SEARCH_RADIUS_MAX),
        ConfigValues.readFloat(
            root,
            KEY_SEARCH_CONE_DEGREES,
            defaults.searchConeDegrees(),
            SEARCH_CONE_DEGREES_MIN,
            SEARCH_CONE_DEGREES_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_TURN_RATE, turnRate);
    root.addProperty(KEY_SEARCH_RADIUS, searchRadius);
    root.addProperty(KEY_SEARCH_CONE_DEGREES, searchConeDegrees);
    return root;
  }
}
