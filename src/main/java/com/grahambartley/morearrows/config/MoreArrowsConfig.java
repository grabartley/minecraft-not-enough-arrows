package com.grahambartley.morearrows.config;

import com.google.gson.JsonObject;

public record MoreArrowsConfig(
    ExplosiveArrowConfig explosive,
    GrappleArrowConfig grapple,
    UtilityArrowConfig utility,
    PhysicsArrowConfig physics) {

  static final String KEY_EXPLOSIVE = "explosive";
  static final String KEY_GRAPPLE = "grapple";
  static final String KEY_UTILITY = "utility";
  static final String KEY_PHYSICS = "physics";

  public MoreArrowsConfig {
    explosive = explosive == null ? ExplosiveArrowConfig.defaults() : explosive;
    grapple = grapple == null ? GrappleArrowConfig.defaults() : grapple;
    utility = utility == null ? UtilityArrowConfig.defaults() : utility;
    physics = physics == null ? PhysicsArrowConfig.defaults() : physics;
  }

  public static MoreArrowsConfig defaults() {
    return new MoreArrowsConfig(
        ExplosiveArrowConfig.defaults(),
        GrappleArrowConfig.defaults(),
        UtilityArrowConfig.defaults(),
        PhysicsArrowConfig.defaults());
  }

  public MoreArrowsConfig withExplosive(final ExplosiveArrowConfig value) {
    return new MoreArrowsConfig(value, grapple, utility, physics);
  }

  public MoreArrowsConfig withGrapple(final GrappleArrowConfig value) {
    return new MoreArrowsConfig(explosive, value, utility, physics);
  }

  public MoreArrowsConfig withUtility(final UtilityArrowConfig value) {
    return new MoreArrowsConfig(explosive, grapple, value, physics);
  }

  public MoreArrowsConfig withPhysics(final PhysicsArrowConfig value) {
    return new MoreArrowsConfig(explosive, grapple, utility, value);
  }

  public static MoreArrowsConfig fromJson(final JsonObject root) {
    return new MoreArrowsConfig(
        ExplosiveArrowConfig.fromJson(ConfigValues.readObject(root, KEY_EXPLOSIVE)),
        GrappleArrowConfig.fromJson(ConfigValues.readObject(root, KEY_GRAPPLE)),
        UtilityArrowConfig.fromJson(ConfigValues.readObject(root, KEY_UTILITY)),
        PhysicsArrowConfig.fromJson(ConfigValues.readObject(root, KEY_PHYSICS)));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.add(KEY_EXPLOSIVE, explosive.toJson());
    root.add(KEY_GRAPPLE, grapple.toJson());
    root.add(KEY_UTILITY, utility.toJson());
    root.add(KEY_PHYSICS, physics.toJson());
    return root;
  }
}
