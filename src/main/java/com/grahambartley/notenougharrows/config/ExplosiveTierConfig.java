package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record ExplosiveTierConfig(int delayTicks, float power) {

  public static final int DELAY_TICKS_MIN = 0;
  public static final int DELAY_TICKS_MAX = 200;
  public static final float POWER_MIN = 0.0f;
  public static final float POWER_MAX = 20.0f;

  static final String KEY_DELAY_TICKS = "delayTicks";
  static final String KEY_POWER = "power";

  public ExplosiveTierConfig {
    delayTicks = ConfigValues.clampInt(delayTicks, DELAY_TICKS_MIN, DELAY_TICKS_MAX);
    power = ConfigValues.clampFloat(power, POWER_MIN, POWER_MAX);
  }

  public boolean detonatesOnContact() {
    return delayTicks == DELAY_TICKS_MIN;
  }

  public ExplosiveTierConfig withDelayTicks(final int value) {
    return new ExplosiveTierConfig(value, power);
  }

  public ExplosiveTierConfig withPower(final float value) {
    return new ExplosiveTierConfig(delayTicks, value);
  }

  public static ExplosiveTierConfig fromJson(
      final JsonObject root, final ExplosiveTierConfig defaults) {
    return new ExplosiveTierConfig(
        ConfigValues.readInt(
            root, KEY_DELAY_TICKS, defaults.delayTicks(), DELAY_TICKS_MIN, DELAY_TICKS_MAX),
        ConfigValues.readFloat(root, KEY_POWER, defaults.power(), POWER_MIN, POWER_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_DELAY_TICKS, delayTicks);
    root.addProperty(KEY_POWER, power);
    return root;
  }
}
