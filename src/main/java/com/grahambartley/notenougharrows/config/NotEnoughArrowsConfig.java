package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record NotEnoughArrowsConfig(
    ExplosiveArrowConfig explosive,
    GrappleArrowConfig grapple,
    UtilityArrowConfig utility,
    PhysicsArrowConfig physics,
    EnderArrowConfig ender,
    CombatArrowConfig combat,
    ControlArrowConfig control,
    FletchingStationConfig fletching) {

  static final String KEY_EXPLOSIVE = "explosive";
  static final String KEY_GRAPPLE = "grapple";
  static final String KEY_UTILITY = "utility";
  static final String KEY_PHYSICS = "physics";
  static final String KEY_ENDER = "ender";
  static final String KEY_COMBAT = "combat";
  static final String KEY_CONTROL = "control";
  static final String KEY_FLETCHING = "fletching";

  public NotEnoughArrowsConfig {
    explosive = explosive == null ? ExplosiveArrowConfig.defaults() : explosive;
    grapple = grapple == null ? GrappleArrowConfig.defaults() : grapple;
    utility = utility == null ? UtilityArrowConfig.defaults() : utility;
    physics = physics == null ? PhysicsArrowConfig.defaults() : physics;
    ender = ender == null ? EnderArrowConfig.defaults() : ender;
    combat = combat == null ? CombatArrowConfig.defaults() : combat;
    control = control == null ? ControlArrowConfig.defaults() : control;
    fletching = fletching == null ? FletchingStationConfig.defaults() : fletching;
  }

  public static NotEnoughArrowsConfig defaults() {
    return new NotEnoughArrowsConfig(
        ExplosiveArrowConfig.defaults(),
        GrappleArrowConfig.defaults(),
        UtilityArrowConfig.defaults(),
        PhysicsArrowConfig.defaults(),
        EnderArrowConfig.defaults(),
        CombatArrowConfig.defaults(),
        ControlArrowConfig.defaults(),
        FletchingStationConfig.defaults());
  }

  public NotEnoughArrowsConfig withExplosive(final ExplosiveArrowConfig value) {
    return new NotEnoughArrowsConfig(
        value, grapple, utility, physics, ender, combat, control, fletching);
  }

  public NotEnoughArrowsConfig withGrapple(final GrappleArrowConfig value) {
    return new NotEnoughArrowsConfig(
        explosive, value, utility, physics, ender, combat, control, fletching);
  }

  public NotEnoughArrowsConfig withUtility(final UtilityArrowConfig value) {
    return new NotEnoughArrowsConfig(
        explosive, grapple, value, physics, ender, combat, control, fletching);
  }

  public NotEnoughArrowsConfig withPhysics(final PhysicsArrowConfig value) {
    return new NotEnoughArrowsConfig(
        explosive, grapple, utility, value, ender, combat, control, fletching);
  }

  public NotEnoughArrowsConfig withEnder(final EnderArrowConfig value) {
    return new NotEnoughArrowsConfig(
        explosive, grapple, utility, physics, value, combat, control, fletching);
  }

  public NotEnoughArrowsConfig withCombat(final CombatArrowConfig value) {
    return new NotEnoughArrowsConfig(
        explosive, grapple, utility, physics, ender, value, control, fletching);
  }

  public NotEnoughArrowsConfig withControl(final ControlArrowConfig value) {
    return new NotEnoughArrowsConfig(
        explosive, grapple, utility, physics, ender, combat, value, fletching);
  }

  public NotEnoughArrowsConfig withFletching(final FletchingStationConfig value) {
    return new NotEnoughArrowsConfig(
        explosive, grapple, utility, physics, ender, combat, control, value);
  }

  public static NotEnoughArrowsConfig fromJson(final JsonObject root) {
    return new NotEnoughArrowsConfig(
        ExplosiveArrowConfig.fromJson(ConfigValues.readObject(root, KEY_EXPLOSIVE)),
        GrappleArrowConfig.fromJson(ConfigValues.readObject(root, KEY_GRAPPLE)),
        UtilityArrowConfig.fromJson(ConfigValues.readObject(root, KEY_UTILITY)),
        PhysicsArrowConfig.fromJson(ConfigValues.readObject(root, KEY_PHYSICS)),
        EnderArrowConfig.fromJson(ConfigValues.readObject(root, KEY_ENDER)),
        CombatArrowConfig.fromJson(ConfigValues.readObject(root, KEY_COMBAT)),
        ControlArrowConfig.fromJson(ConfigValues.readObject(root, KEY_CONTROL)),
        FletchingStationConfig.fromJson(ConfigValues.readObject(root, KEY_FLETCHING)));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.add(KEY_EXPLOSIVE, explosive.toJson());
    root.add(KEY_GRAPPLE, grapple.toJson());
    root.add(KEY_UTILITY, utility.toJson());
    root.add(KEY_PHYSICS, physics.toJson());
    root.add(KEY_ENDER, ender.toJson());
    root.add(KEY_COMBAT, combat.toJson());
    root.add(KEY_CONTROL, control.toJson());
    root.add(KEY_FLETCHING, fletching.toJson());
    return root;
  }
}
