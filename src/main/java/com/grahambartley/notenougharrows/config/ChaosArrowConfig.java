package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record ChaosArrowConfig(
    boolean partyEnabled,
    boolean chickenEnabled,
    boolean pufferEnabled,
    int pufferDurationTicks,
    boolean stinkEnabled,
    int stinkCloudLifetimeTicks,
    boolean boomerangEnabled,
    boolean polymorphEnabled,
    int polymorphDurationTicks) {

  public static final int PUFFER_DURATION_TICKS_MIN = 0;
  public static final int PUFFER_DURATION_TICKS_MAX = 1200;
  public static final int STINK_CLOUD_LIFETIME_TICKS_MIN = 0;
  public static final int STINK_CLOUD_LIFETIME_TICKS_MAX = 1200;
  public static final int POLYMORPH_DURATION_TICKS_MIN = 0;
  public static final int POLYMORPH_DURATION_TICKS_MAX = 2400;

  public static final boolean DEFAULT_PARTY_ENABLED = true;
  public static final boolean DEFAULT_CHICKEN_ENABLED = true;
  public static final boolean DEFAULT_PUFFER_ENABLED = true;
  public static final int DEFAULT_PUFFER_DURATION_TICKS = 200;
  public static final boolean DEFAULT_STINK_ENABLED = true;
  public static final int DEFAULT_STINK_CLOUD_LIFETIME_TICKS = 200;
  public static final boolean DEFAULT_BOOMERANG_ENABLED = true;
  public static final boolean DEFAULT_POLYMORPH_ENABLED = true;
  public static final int DEFAULT_POLYMORPH_DURATION_TICKS = 400;

  static final String KEY_PARTY_ENABLED = "partyEnabled";
  static final String KEY_CHICKEN_ENABLED = "chickenEnabled";
  static final String KEY_PUFFER_ENABLED = "pufferEnabled";
  static final String KEY_PUFFER_DURATION_TICKS = "pufferDurationTicks";
  static final String KEY_STINK_ENABLED = "stinkEnabled";
  static final String KEY_STINK_CLOUD_LIFETIME_TICKS = "stinkCloudLifetimeTicks";
  static final String KEY_BOOMERANG_ENABLED = "boomerangEnabled";
  static final String KEY_POLYMORPH_ENABLED = "polymorphEnabled";
  static final String KEY_POLYMORPH_DURATION_TICKS = "polymorphDurationTicks";

  public ChaosArrowConfig {
    pufferDurationTicks =
        ConfigValues.clampInt(
            pufferDurationTicks, PUFFER_DURATION_TICKS_MIN, PUFFER_DURATION_TICKS_MAX);
    stinkCloudLifetimeTicks =
        ConfigValues.clampInt(
            stinkCloudLifetimeTicks,
            STINK_CLOUD_LIFETIME_TICKS_MIN,
            STINK_CLOUD_LIFETIME_TICKS_MAX);
    polymorphDurationTicks =
        ConfigValues.clampInt(
            polymorphDurationTicks, POLYMORPH_DURATION_TICKS_MIN, POLYMORPH_DURATION_TICKS_MAX);
  }

  public static ChaosArrowConfig defaults() {
    return new ChaosArrowConfig(
        DEFAULT_PARTY_ENABLED,
        DEFAULT_CHICKEN_ENABLED,
        DEFAULT_PUFFER_ENABLED,
        DEFAULT_PUFFER_DURATION_TICKS,
        DEFAULT_STINK_ENABLED,
        DEFAULT_STINK_CLOUD_LIFETIME_TICKS,
        DEFAULT_BOOMERANG_ENABLED,
        DEFAULT_POLYMORPH_ENABLED,
        DEFAULT_POLYMORPH_DURATION_TICKS);
  }

  public static ChaosArrowConfig fromJson(final JsonObject root) {
    final ChaosArrowConfig defaults = defaults();
    return new ChaosArrowConfig(
        ConfigValues.readBoolean(root, KEY_PARTY_ENABLED, defaults.partyEnabled()),
        ConfigValues.readBoolean(root, KEY_CHICKEN_ENABLED, defaults.chickenEnabled()),
        ConfigValues.readBoolean(root, KEY_PUFFER_ENABLED, defaults.pufferEnabled()),
        ConfigValues.readInt(
            root,
            KEY_PUFFER_DURATION_TICKS,
            defaults.pufferDurationTicks(),
            PUFFER_DURATION_TICKS_MIN,
            PUFFER_DURATION_TICKS_MAX),
        ConfigValues.readBoolean(root, KEY_STINK_ENABLED, defaults.stinkEnabled()),
        ConfigValues.readInt(
            root,
            KEY_STINK_CLOUD_LIFETIME_TICKS,
            defaults.stinkCloudLifetimeTicks(),
            STINK_CLOUD_LIFETIME_TICKS_MIN,
            STINK_CLOUD_LIFETIME_TICKS_MAX),
        ConfigValues.readBoolean(root, KEY_BOOMERANG_ENABLED, defaults.boomerangEnabled()),
        ConfigValues.readBoolean(root, KEY_POLYMORPH_ENABLED, defaults.polymorphEnabled()),
        ConfigValues.readInt(
            root,
            KEY_POLYMORPH_DURATION_TICKS,
            defaults.polymorphDurationTicks(),
            POLYMORPH_DURATION_TICKS_MIN,
            POLYMORPH_DURATION_TICKS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_PARTY_ENABLED, partyEnabled);
    root.addProperty(KEY_CHICKEN_ENABLED, chickenEnabled);
    root.addProperty(KEY_PUFFER_ENABLED, pufferEnabled);
    root.addProperty(KEY_PUFFER_DURATION_TICKS, pufferDurationTicks);
    root.addProperty(KEY_STINK_ENABLED, stinkEnabled);
    root.addProperty(KEY_STINK_CLOUD_LIFETIME_TICKS, stinkCloudLifetimeTicks);
    root.addProperty(KEY_BOOMERANG_ENABLED, boomerangEnabled);
    root.addProperty(KEY_POLYMORPH_ENABLED, polymorphEnabled);
    root.addProperty(KEY_POLYMORPH_DURATION_TICKS, polymorphDurationTicks);
    return root;
  }

  public ChaosArrowConfig withPartyEnabled(final boolean value) {
    return new ChaosArrowConfig(
        value,
        chickenEnabled,
        pufferEnabled,
        pufferDurationTicks,
        stinkEnabled,
        stinkCloudLifetimeTicks,
        boomerangEnabled,
        polymorphEnabled,
        polymorphDurationTicks);
  }

  public ChaosArrowConfig withChickenEnabled(final boolean value) {
    return new ChaosArrowConfig(
        partyEnabled,
        value,
        pufferEnabled,
        pufferDurationTicks,
        stinkEnabled,
        stinkCloudLifetimeTicks,
        boomerangEnabled,
        polymorphEnabled,
        polymorphDurationTicks);
  }

  public ChaosArrowConfig withPufferEnabled(final boolean value) {
    return new ChaosArrowConfig(
        partyEnabled,
        chickenEnabled,
        value,
        pufferDurationTicks,
        stinkEnabled,
        stinkCloudLifetimeTicks,
        boomerangEnabled,
        polymorphEnabled,
        polymorphDurationTicks);
  }

  public ChaosArrowConfig withPufferDurationTicks(final int value) {
    return new ChaosArrowConfig(
        partyEnabled,
        chickenEnabled,
        pufferEnabled,
        value,
        stinkEnabled,
        stinkCloudLifetimeTicks,
        boomerangEnabled,
        polymorphEnabled,
        polymorphDurationTicks);
  }

  public ChaosArrowConfig withStinkEnabled(final boolean value) {
    return new ChaosArrowConfig(
        partyEnabled,
        chickenEnabled,
        pufferEnabled,
        pufferDurationTicks,
        value,
        stinkCloudLifetimeTicks,
        boomerangEnabled,
        polymorphEnabled,
        polymorphDurationTicks);
  }

  public ChaosArrowConfig withStinkCloudLifetimeTicks(final int value) {
    return new ChaosArrowConfig(
        partyEnabled,
        chickenEnabled,
        pufferEnabled,
        pufferDurationTicks,
        stinkEnabled,
        value,
        boomerangEnabled,
        polymorphEnabled,
        polymorphDurationTicks);
  }

  public ChaosArrowConfig withBoomerangEnabled(final boolean value) {
    return new ChaosArrowConfig(
        partyEnabled,
        chickenEnabled,
        pufferEnabled,
        pufferDurationTicks,
        stinkEnabled,
        stinkCloudLifetimeTicks,
        value,
        polymorphEnabled,
        polymorphDurationTicks);
  }

  public ChaosArrowConfig withPolymorphEnabled(final boolean value) {
    return new ChaosArrowConfig(
        partyEnabled,
        chickenEnabled,
        pufferEnabled,
        pufferDurationTicks,
        stinkEnabled,
        stinkCloudLifetimeTicks,
        boomerangEnabled,
        value,
        polymorphDurationTicks);
  }

  public ChaosArrowConfig withPolymorphDurationTicks(final int value) {
    return new ChaosArrowConfig(
        partyEnabled,
        chickenEnabled,
        pufferEnabled,
        pufferDurationTicks,
        stinkEnabled,
        stinkCloudLifetimeTicks,
        boomerangEnabled,
        polymorphEnabled,
        value);
  }
}
