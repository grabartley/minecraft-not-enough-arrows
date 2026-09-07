package com.grahambartley.morearrows.config;

import com.google.gson.JsonObject;

public record GrappleArrowConfig(
    int maxRangeBlocks,
    float pullSpeed,
    float pullAcceleration,
    boolean cancelFallDamageOnArrival,
    boolean returnArrowOnArrival,
    int ropeLengthBlocks,
    boolean ropesDecay) {

  public static final int MAX_RANGE_BLOCKS_MIN = 4;
  public static final int MAX_RANGE_BLOCKS_MAX = 128;
  public static final float PULL_SPEED_MIN = 0.1f;
  public static final float PULL_SPEED_MAX = 4.0f;
  public static final float PULL_ACCELERATION_MIN = 0.01f;
  public static final float PULL_ACCELERATION_MAX = 4.0f;
  public static final int ROPE_LENGTH_BLOCKS_MIN = 1;
  public static final int ROPE_LENGTH_BLOCKS_MAX = 128;

  public static final int DEFAULT_MAX_RANGE_BLOCKS = 32;
  public static final float DEFAULT_PULL_SPEED = 1.5f;
  public static final float DEFAULT_PULL_ACCELERATION = 0.15f;
  public static final boolean DEFAULT_CANCEL_FALL_DAMAGE_ON_ARRIVAL = true;
  public static final boolean DEFAULT_RETURN_ARROW_ON_ARRIVAL = true;
  public static final int DEFAULT_ROPE_LENGTH_BLOCKS = 16;
  public static final boolean DEFAULT_ROPES_DECAY = false;

  static final String KEY_MAX_RANGE_BLOCKS = "maxRangeBlocks";
  static final String KEY_PULL_SPEED = "pullSpeed";
  static final String KEY_PULL_ACCELERATION = "pullAcceleration";
  static final String KEY_CANCEL_FALL_DAMAGE_ON_ARRIVAL = "cancelFallDamageOnArrival";
  static final String KEY_RETURN_ARROW_ON_ARRIVAL = "returnArrowOnArrival";
  static final String KEY_ROPE_LENGTH_BLOCKS = "ropeLengthBlocks";
  static final String KEY_ROPES_DECAY = "ropesDecay";

  public GrappleArrowConfig {
    maxRangeBlocks =
        ConfigValues.clampInt(maxRangeBlocks, MAX_RANGE_BLOCKS_MIN, MAX_RANGE_BLOCKS_MAX);
    pullSpeed = ConfigValues.clampFloat(pullSpeed, PULL_SPEED_MIN, PULL_SPEED_MAX);
    pullAcceleration =
        ConfigValues.clampFloat(pullAcceleration, PULL_ACCELERATION_MIN, PULL_ACCELERATION_MAX);
    ropeLengthBlocks =
        ConfigValues.clampInt(ropeLengthBlocks, ROPE_LENGTH_BLOCKS_MIN, ROPE_LENGTH_BLOCKS_MAX);
  }

  public static GrappleArrowConfig defaults() {
    return new GrappleArrowConfig(
        DEFAULT_MAX_RANGE_BLOCKS,
        DEFAULT_PULL_SPEED,
        DEFAULT_PULL_ACCELERATION,
        DEFAULT_CANCEL_FALL_DAMAGE_ON_ARRIVAL,
        DEFAULT_RETURN_ARROW_ON_ARRIVAL,
        DEFAULT_ROPE_LENGTH_BLOCKS,
        DEFAULT_ROPES_DECAY);
  }

  public static GrappleArrowConfig fromJson(final JsonObject root) {
    final GrappleArrowConfig defaults = defaults();
    return new GrappleArrowConfig(
        ConfigValues.readInt(
            root,
            KEY_MAX_RANGE_BLOCKS,
            defaults.maxRangeBlocks(),
            MAX_RANGE_BLOCKS_MIN,
            MAX_RANGE_BLOCKS_MAX),
        ConfigValues.readFloat(
            root, KEY_PULL_SPEED, defaults.pullSpeed(), PULL_SPEED_MIN, PULL_SPEED_MAX),
        ConfigValues.readFloat(
            root,
            KEY_PULL_ACCELERATION,
            defaults.pullAcceleration(),
            PULL_ACCELERATION_MIN,
            PULL_ACCELERATION_MAX),
        ConfigValues.readBoolean(
            root, KEY_CANCEL_FALL_DAMAGE_ON_ARRIVAL, defaults.cancelFallDamageOnArrival()),
        ConfigValues.readBoolean(
            root, KEY_RETURN_ARROW_ON_ARRIVAL, defaults.returnArrowOnArrival()),
        ConfigValues.readInt(
            root,
            KEY_ROPE_LENGTH_BLOCKS,
            defaults.ropeLengthBlocks(),
            ROPE_LENGTH_BLOCKS_MIN,
            ROPE_LENGTH_BLOCKS_MAX),
        ConfigValues.readBoolean(root, KEY_ROPES_DECAY, defaults.ropesDecay()));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_MAX_RANGE_BLOCKS, maxRangeBlocks);
    root.addProperty(KEY_PULL_SPEED, pullSpeed);
    root.addProperty(KEY_PULL_ACCELERATION, pullAcceleration);
    root.addProperty(KEY_CANCEL_FALL_DAMAGE_ON_ARRIVAL, cancelFallDamageOnArrival);
    root.addProperty(KEY_RETURN_ARROW_ON_ARRIVAL, returnArrowOnArrival);
    root.addProperty(KEY_ROPE_LENGTH_BLOCKS, ropeLengthBlocks);
    root.addProperty(KEY_ROPES_DECAY, ropesDecay);
    return root;
  }

  public GrappleArrowConfig withMaxRangeBlocks(final int value) {
    return new GrappleArrowConfig(
        value,
        pullSpeed,
        pullAcceleration,
        cancelFallDamageOnArrival,
        returnArrowOnArrival,
        ropeLengthBlocks,
        ropesDecay);
  }

  public GrappleArrowConfig withPullSpeed(final float value) {
    return new GrappleArrowConfig(
        maxRangeBlocks,
        value,
        pullAcceleration,
        cancelFallDamageOnArrival,
        returnArrowOnArrival,
        ropeLengthBlocks,
        ropesDecay);
  }

  public GrappleArrowConfig withPullAcceleration(final float value) {
    return new GrappleArrowConfig(
        maxRangeBlocks,
        pullSpeed,
        value,
        cancelFallDamageOnArrival,
        returnArrowOnArrival,
        ropeLengthBlocks,
        ropesDecay);
  }

  public GrappleArrowConfig withCancelFallDamageOnArrival(final boolean value) {
    return new GrappleArrowConfig(
        maxRangeBlocks,
        pullSpeed,
        pullAcceleration,
        value,
        returnArrowOnArrival,
        ropeLengthBlocks,
        ropesDecay);
  }

  public GrappleArrowConfig withReturnArrowOnArrival(final boolean value) {
    return new GrappleArrowConfig(
        maxRangeBlocks,
        pullSpeed,
        pullAcceleration,
        cancelFallDamageOnArrival,
        value,
        ropeLengthBlocks,
        ropesDecay);
  }

  public GrappleArrowConfig withRopeLengthBlocks(final int value) {
    return new GrappleArrowConfig(
        maxRangeBlocks,
        pullSpeed,
        pullAcceleration,
        cancelFallDamageOnArrival,
        returnArrowOnArrival,
        value,
        ropesDecay);
  }

  public GrappleArrowConfig withRopesDecay(final boolean value) {
    return new GrappleArrowConfig(
        maxRangeBlocks,
        pullSpeed,
        pullAcceleration,
        cancelFallDamageOnArrival,
        returnArrowOnArrival,
        ropeLengthBlocks,
        value);
  }
}
