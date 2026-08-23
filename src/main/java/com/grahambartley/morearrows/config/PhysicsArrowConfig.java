package com.grahambartley.morearrows.config;

import com.google.gson.JsonObject;
import java.util.List;

public record PhysicsArrowConfig(
    int gravityImpactRadius,
    List<String> gravityBlockExclusions,
    int ricochetBounceCount,
    boolean ricochetRetainsDamage) {

  public static final int GRAVITY_IMPACT_RADIUS_MIN = 0;
  public static final int GRAVITY_IMPACT_RADIUS_MAX = 8;
  public static final int GRAVITY_BLOCK_EXCLUSIONS_MAX = 256;
  public static final int RICOCHET_BOUNCE_COUNT_MIN = 0;
  public static final int RICOCHET_BOUNCE_COUNT_MAX = 16;

  public static final int DEFAULT_GRAVITY_IMPACT_RADIUS = 0;
  public static final List<String> DEFAULT_GRAVITY_BLOCK_EXCLUSIONS = List.of();
  public static final int DEFAULT_RICOCHET_BOUNCE_COUNT = 3;
  public static final boolean DEFAULT_RICOCHET_RETAINS_DAMAGE = true;

  static final String KEY_GRAVITY_IMPACT_RADIUS = "gravityImpactRadius";
  static final String KEY_GRAVITY_BLOCK_EXCLUSIONS = "gravityBlockExclusions";
  static final String KEY_RICOCHET_BOUNCE_COUNT = "ricochetBounceCount";
  static final String KEY_RICOCHET_RETAINS_DAMAGE = "ricochetRetainsDamage";

  public PhysicsArrowConfig {
    gravityImpactRadius =
        ConfigValues.clampInt(
            gravityImpactRadius, GRAVITY_IMPACT_RADIUS_MIN, GRAVITY_IMPACT_RADIUS_MAX);
    gravityBlockExclusions =
        ConfigValues.normalizeIdentifiers(gravityBlockExclusions, GRAVITY_BLOCK_EXCLUSIONS_MAX);
    ricochetBounceCount =
        ConfigValues.clampInt(
            ricochetBounceCount, RICOCHET_BOUNCE_COUNT_MIN, RICOCHET_BOUNCE_COUNT_MAX);
  }

  public static PhysicsArrowConfig defaults() {
    return new PhysicsArrowConfig(
        DEFAULT_GRAVITY_IMPACT_RADIUS,
        DEFAULT_GRAVITY_BLOCK_EXCLUSIONS,
        DEFAULT_RICOCHET_BOUNCE_COUNT,
        DEFAULT_RICOCHET_RETAINS_DAMAGE);
  }

  public boolean affectsOnlyTheHitBlock() {
    return gravityImpactRadius == GRAVITY_IMPACT_RADIUS_MIN;
  }

  public boolean isExcludedFromGravity(final String blockId) {
    return blockId != null
        && gravityBlockExclusions.contains(blockId.trim().toLowerCase(java.util.Locale.ROOT));
  }

  public static PhysicsArrowConfig fromJson(final JsonObject root) {
    final PhysicsArrowConfig defaults = defaults();
    return new PhysicsArrowConfig(
        ConfigValues.readInt(
            root,
            KEY_GRAVITY_IMPACT_RADIUS,
            defaults.gravityImpactRadius(),
            GRAVITY_IMPACT_RADIUS_MIN,
            GRAVITY_IMPACT_RADIUS_MAX),
        ConfigValues.readIdentifierList(
            root, KEY_GRAVITY_BLOCK_EXCLUSIONS, defaults.gravityBlockExclusions()),
        ConfigValues.readInt(
            root,
            KEY_RICOCHET_BOUNCE_COUNT,
            defaults.ricochetBounceCount(),
            RICOCHET_BOUNCE_COUNT_MIN,
            RICOCHET_BOUNCE_COUNT_MAX),
        ConfigValues.readBoolean(
            root, KEY_RICOCHET_RETAINS_DAMAGE, defaults.ricochetRetainsDamage()));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_GRAVITY_IMPACT_RADIUS, gravityImpactRadius);
    root.add(KEY_GRAVITY_BLOCK_EXCLUSIONS, ConfigValues.toJsonArray(gravityBlockExclusions));
    root.addProperty(KEY_RICOCHET_BOUNCE_COUNT, ricochetBounceCount);
    root.addProperty(KEY_RICOCHET_RETAINS_DAMAGE, ricochetRetainsDamage);
    return root;
  }

  public PhysicsArrowConfig withGravityImpactRadius(final int value) {
    return new PhysicsArrowConfig(
        value, gravityBlockExclusions, ricochetBounceCount, ricochetRetainsDamage);
  }

  public PhysicsArrowConfig withGravityBlockExclusions(final List<String> value) {
    return new PhysicsArrowConfig(
        gravityImpactRadius, value, ricochetBounceCount, ricochetRetainsDamage);
  }

  public PhysicsArrowConfig withRicochetBounceCount(final int value) {
    return new PhysicsArrowConfig(
        gravityImpactRadius, gravityBlockExclusions, value, ricochetRetainsDamage);
  }

  public PhysicsArrowConfig withRicochetRetainsDamage(final boolean value) {
    return new PhysicsArrowConfig(
        gravityImpactRadius, gravityBlockExclusions, ricochetBounceCount, value);
  }
}
