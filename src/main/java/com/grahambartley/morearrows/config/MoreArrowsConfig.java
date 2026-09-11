package com.grahambartley.morearrows.config;

import com.google.gson.JsonObject;

public record MoreArrowsConfig(
    ExplosiveArrowConfig explosive,
    GrappleArrowConfig grapple,
    UtilityArrowConfig utility,
    PhysicsArrowConfig physics,
    EnderArrowConfig ender,
    FletchingStationConfig fletching) {

  static final String KEY_EXPLOSIVE = "explosive";
  static final String KEY_GRAPPLE = "grapple";
  static final String KEY_UTILITY = "utility";
  static final String KEY_PHYSICS = "physics";
  static final String KEY_ENDER = "ender";
  static final String KEY_FLETCHING = "fletching";

  public MoreArrowsConfig {
    explosive = explosive == null ? ExplosiveArrowConfig.defaults() : explosive;
    grapple = grapple == null ? GrappleArrowConfig.defaults() : grapple;
    utility = utility == null ? UtilityArrowConfig.defaults() : utility;
    physics = physics == null ? PhysicsArrowConfig.defaults() : physics;
    ender = ender == null ? EnderArrowConfig.defaults() : ender;
    fletching = fletching == null ? FletchingStationConfig.defaults() : fletching;
  }

  public static MoreArrowsConfig defaults() {
    return new MoreArrowsConfig(
        ExplosiveArrowConfig.defaults(),
        GrappleArrowConfig.defaults(),
        UtilityArrowConfig.defaults(),
        PhysicsArrowConfig.defaults(),
        EnderArrowConfig.defaults(),
        FletchingStationConfig.defaults());
  }

  public MoreArrowsConfig withExplosive(final ExplosiveArrowConfig value) {
    return new MoreArrowsConfig(value, grapple, utility, physics, ender, fletching);
  }

  public MoreArrowsConfig withGrapple(final GrappleArrowConfig value) {
    return new MoreArrowsConfig(explosive, value, utility, physics, ender, fletching);
  }

  public MoreArrowsConfig withUtility(final UtilityArrowConfig value) {
    return new MoreArrowsConfig(explosive, grapple, value, physics, ender, fletching);
  }

  public MoreArrowsConfig withPhysics(final PhysicsArrowConfig value) {
    return new MoreArrowsConfig(explosive, grapple, utility, value, ender, fletching);
  }

  public MoreArrowsConfig withEnder(final EnderArrowConfig value) {
    return new MoreArrowsConfig(explosive, grapple, utility, physics, value, fletching);
  }

  public MoreArrowsConfig withFletching(final FletchingStationConfig value) {
    return new MoreArrowsConfig(explosive, grapple, utility, physics, ender, value);
  }

  public static MoreArrowsConfig fromJson(final JsonObject root) {
    return new MoreArrowsConfig(
        ExplosiveArrowConfig.fromJson(ConfigValues.readObject(root, KEY_EXPLOSIVE)),
        GrappleArrowConfig.fromJson(ConfigValues.readObject(root, KEY_GRAPPLE)),
        UtilityArrowConfig.fromJson(ConfigValues.readObject(root, KEY_UTILITY)),
        PhysicsArrowConfig.fromJson(ConfigValues.readObject(root, KEY_PHYSICS)),
        EnderArrowConfig.fromJson(ConfigValues.readObject(root, KEY_ENDER)),
        FletchingStationConfig.fromJson(ConfigValues.readObject(root, KEY_FLETCHING)));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.add(KEY_EXPLOSIVE, explosive.toJson());
    root.add(KEY_GRAPPLE, grapple.toJson());
    root.add(KEY_UTILITY, utility.toJson());
    root.add(KEY_PHYSICS, physics.toJson());
    root.add(KEY_ENDER, ender.toJson());
    root.add(KEY_FLETCHING, fletching.toJson());
    return root;
  }
}
