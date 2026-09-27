package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record ZiplineArrowConfig(
    int maxSpanBlocks, int pendingWindowTicks, float rideSpeed, int lifetimeTicks) {

  public static final int MAX_SPAN_BLOCKS_MIN = 2;
  public static final int MAX_SPAN_BLOCKS_MAX = 128;
  public static final int PENDING_WINDOW_TICKS_MIN = 20;
  public static final int PENDING_WINDOW_TICKS_MAX = 6000;
  public static final float RIDE_SPEED_MIN = 0.1f;
  public static final float RIDE_SPEED_MAX = 3.0f;
  public static final int LIFETIME_TICKS_MIN = 0;
  public static final int LIFETIME_TICKS_MAX = 12000;

  public static final int DEFAULT_MAX_SPAN_BLOCKS = 32;
  public static final int DEFAULT_PENDING_WINDOW_TICKS = 600;
  public static final float DEFAULT_RIDE_SPEED = 0.6f;
  public static final int DEFAULT_LIFETIME_TICKS = 1200;

  static final String KEY_MAX_SPAN_BLOCKS = "maxSpanBlocks";
  static final String KEY_PENDING_WINDOW_TICKS = "pendingWindowTicks";
  static final String KEY_RIDE_SPEED = "rideSpeed";
  static final String KEY_LIFETIME_TICKS = "lifetimeTicks";

  public ZiplineArrowConfig {
    maxSpanBlocks = ConfigValues.clampInt(maxSpanBlocks, MAX_SPAN_BLOCKS_MIN, MAX_SPAN_BLOCKS_MAX);
    pendingWindowTicks =
        ConfigValues.clampInt(
            pendingWindowTicks, PENDING_WINDOW_TICKS_MIN, PENDING_WINDOW_TICKS_MAX);
    rideSpeed = ConfigValues.clampFloat(rideSpeed, RIDE_SPEED_MIN, RIDE_SPEED_MAX);
    lifetimeTicks = ConfigValues.clampInt(lifetimeTicks, LIFETIME_TICKS_MIN, LIFETIME_TICKS_MAX);
  }

  public static ZiplineArrowConfig defaults() {
    return new ZiplineArrowConfig(
        DEFAULT_MAX_SPAN_BLOCKS,
        DEFAULT_PENDING_WINDOW_TICKS,
        DEFAULT_RIDE_SPEED,
        DEFAULT_LIFETIME_TICKS);
  }

  public static ZiplineArrowConfig fromJson(final JsonObject root) {
    final ZiplineArrowConfig defaults = defaults();
    return new ZiplineArrowConfig(
        ConfigValues.readInt(
            root,
            KEY_MAX_SPAN_BLOCKS,
            defaults.maxSpanBlocks(),
            MAX_SPAN_BLOCKS_MIN,
            MAX_SPAN_BLOCKS_MAX),
        ConfigValues.readInt(
            root,
            KEY_PENDING_WINDOW_TICKS,
            defaults.pendingWindowTicks(),
            PENDING_WINDOW_TICKS_MIN,
            PENDING_WINDOW_TICKS_MAX),
        ConfigValues.readFloat(
            root, KEY_RIDE_SPEED, defaults.rideSpeed(), RIDE_SPEED_MIN, RIDE_SPEED_MAX),
        ConfigValues.readInt(
            root,
            KEY_LIFETIME_TICKS,
            defaults.lifetimeTicks(),
            LIFETIME_TICKS_MIN,
            LIFETIME_TICKS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_MAX_SPAN_BLOCKS, maxSpanBlocks);
    root.addProperty(KEY_PENDING_WINDOW_TICKS, pendingWindowTicks);
    root.addProperty(KEY_RIDE_SPEED, rideSpeed);
    root.addProperty(KEY_LIFETIME_TICKS, lifetimeTicks);
    return root;
  }

  public ZiplineArrowConfig withMaxSpanBlocks(final int value) {
    return new ZiplineArrowConfig(value, pendingWindowTicks, rideSpeed, lifetimeTicks);
  }

  public ZiplineArrowConfig withPendingWindowTicks(final int value) {
    return new ZiplineArrowConfig(maxSpanBlocks, value, rideSpeed, lifetimeTicks);
  }

  public ZiplineArrowConfig withRideSpeed(final float value) {
    return new ZiplineArrowConfig(maxSpanBlocks, pendingWindowTicks, value, lifetimeTicks);
  }

  public ZiplineArrowConfig withLifetimeTicks(final int value) {
    return new ZiplineArrowConfig(maxSpanBlocks, pendingWindowTicks, rideSpeed, value);
  }
}
