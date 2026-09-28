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
    TraversalArrowConfig traversal,
    TerrainArrowConfig terrain,
    AgricultureArrowConfig agriculture,
    DiscoveryArrowConfig discovery,
    ChaosArrowConfig chaos,
    SocialArrowConfig social,
    FletchingStationConfig fletching,
    SoundConfig sound) {

  static final String KEY_EXPLOSIVE = "explosive";
  static final String KEY_GRAPPLE = "grapple";
  static final String KEY_UTILITY = "utility";
  static final String KEY_PHYSICS = "physics";
  static final String KEY_ENDER = "ender";
  static final String KEY_COMBAT = "combat";
  static final String KEY_CONTROL = "control";
  static final String KEY_TRAVERSAL = "traversal";
  static final String KEY_TERRAIN = "terrain";
  static final String KEY_AGRICULTURE = "agriculture";
  static final String KEY_DISCOVERY = "discovery";
  static final String KEY_CHAOS = "chaos";
  static final String KEY_SOCIAL = "social";
  static final String KEY_FLETCHING = "fletching";
  static final String KEY_SOUND = "sound";

  public NotEnoughArrowsConfig {
    explosive = explosive == null ? ExplosiveArrowConfig.defaults() : explosive;
    grapple = grapple == null ? GrappleArrowConfig.defaults() : grapple;
    utility = utility == null ? UtilityArrowConfig.defaults() : utility;
    physics = physics == null ? PhysicsArrowConfig.defaults() : physics;
    ender = ender == null ? EnderArrowConfig.defaults() : ender;
    combat = combat == null ? CombatArrowConfig.defaults() : combat;
    control = control == null ? ControlArrowConfig.defaults() : control;
    traversal = traversal == null ? TraversalArrowConfig.defaults() : traversal;
    terrain = terrain == null ? TerrainArrowConfig.defaults() : terrain;
    agriculture = agriculture == null ? AgricultureArrowConfig.defaults() : agriculture;
    discovery = discovery == null ? DiscoveryArrowConfig.defaults() : discovery;
    chaos = chaos == null ? ChaosArrowConfig.defaults() : chaos;
    social = social == null ? SocialArrowConfig.defaults() : social;
    fletching = fletching == null ? FletchingStationConfig.defaults() : fletching;
    sound = sound == null ? SoundConfig.defaults() : sound;
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
        TraversalArrowConfig.defaults(),
        TerrainArrowConfig.defaults(),
        AgricultureArrowConfig.defaults(),
        DiscoveryArrowConfig.defaults(),
        ChaosArrowConfig.defaults(),
        SocialArrowConfig.defaults(),
        FletchingStationConfig.defaults(),
        SoundConfig.defaults());
  }

  public NotEnoughArrowsConfig withExplosive(final ExplosiveArrowConfig value) {
    return new NotEnoughArrowsConfig(
        value,
        grapple,
        utility,
        physics,
        ender,
        combat,
        control,
        traversal,
        terrain,
        agriculture,
        discovery,
        chaos,
        social,
        fletching,
        sound);
  }

  public NotEnoughArrowsConfig withGrapple(final GrappleArrowConfig value) {
    return new NotEnoughArrowsConfig(
        explosive,
        value,
        utility,
        physics,
        ender,
        combat,
        control,
        traversal,
        terrain,
        agriculture,
        discovery,
        chaos,
        social,
        fletching,
        sound);
  }

  public NotEnoughArrowsConfig withUtility(final UtilityArrowConfig value) {
    return new NotEnoughArrowsConfig(
        explosive,
        grapple,
        value,
        physics,
        ender,
        combat,
        control,
        traversal,
        terrain,
        agriculture,
        discovery,
        chaos,
        social,
        fletching,
        sound);
  }

  public NotEnoughArrowsConfig withPhysics(final PhysicsArrowConfig value) {
    return new NotEnoughArrowsConfig(
        explosive,
        grapple,
        utility,
        value,
        ender,
        combat,
        control,
        traversal,
        terrain,
        agriculture,
        discovery,
        chaos,
        social,
        fletching,
        sound);
  }

  public NotEnoughArrowsConfig withEnder(final EnderArrowConfig value) {
    return new NotEnoughArrowsConfig(
        explosive,
        grapple,
        utility,
        physics,
        value,
        combat,
        control,
        traversal,
        terrain,
        agriculture,
        discovery,
        chaos,
        social,
        fletching,
        sound);
  }

  public NotEnoughArrowsConfig withCombat(final CombatArrowConfig value) {
    return new NotEnoughArrowsConfig(
        explosive,
        grapple,
        utility,
        physics,
        ender,
        value,
        control,
        traversal,
        terrain,
        agriculture,
        discovery,
        chaos,
        social,
        fletching,
        sound);
  }

  public NotEnoughArrowsConfig withControl(final ControlArrowConfig value) {
    return new NotEnoughArrowsConfig(
        explosive,
        grapple,
        utility,
        physics,
        ender,
        combat,
        value,
        traversal,
        terrain,
        agriculture,
        discovery,
        chaos,
        social,
        fletching,
        sound);
  }

  public NotEnoughArrowsConfig withTraversal(final TraversalArrowConfig value) {
    return new NotEnoughArrowsConfig(
        explosive,
        grapple,
        utility,
        physics,
        ender,
        combat,
        control,
        value,
        terrain,
        agriculture,
        discovery,
        chaos,
        social,
        fletching,
        sound);
  }

  public NotEnoughArrowsConfig withTerrain(final TerrainArrowConfig value) {
    return new NotEnoughArrowsConfig(
        explosive,
        grapple,
        utility,
        physics,
        ender,
        combat,
        control,
        traversal,
        value,
        agriculture,
        discovery,
        chaos,
        social,
        fletching,
        sound);
  }

  public NotEnoughArrowsConfig withAgriculture(final AgricultureArrowConfig value) {
    return new NotEnoughArrowsConfig(
        explosive, grapple, utility, physics, ender, combat, control, traversal, terrain, value,
        discovery, chaos, social, fletching, sound);
  }

  public NotEnoughArrowsConfig withDiscovery(final DiscoveryArrowConfig value) {
    return new NotEnoughArrowsConfig(
        explosive,
        grapple,
        utility,
        physics,
        ender,
        combat,
        control,
        traversal,
        terrain,
        agriculture,
        value,
        chaos,
        social,
        fletching,
        sound);
  }

  public NotEnoughArrowsConfig withChaos(final ChaosArrowConfig value) {
    return new NotEnoughArrowsConfig(
        explosive,
        grapple,
        utility,
        physics,
        ender,
        combat,
        control,
        traversal,
        terrain,
        agriculture,
        discovery,
        value,
        social,
        fletching,
        sound);
  }

  public NotEnoughArrowsConfig withSocial(final SocialArrowConfig value) {
    return new NotEnoughArrowsConfig(
        explosive,
        grapple,
        utility,
        physics,
        ender,
        combat,
        control,
        traversal,
        terrain,
        agriculture,
        discovery,
        chaos,
        value,
        fletching,
        sound);
  }

  public NotEnoughArrowsConfig withFletching(final FletchingStationConfig value) {
    return new NotEnoughArrowsConfig(
        explosive,
        grapple,
        utility,
        physics,
        ender,
        combat,
        control,
        traversal,
        terrain,
        agriculture,
        discovery,
        chaos,
        social,
        value,
        sound);
  }

  public NotEnoughArrowsConfig withSound(final SoundConfig value) {
    return new NotEnoughArrowsConfig(
        explosive,
        grapple,
        utility,
        physics,
        ender,
        combat,
        control,
        traversal,
        terrain,
        agriculture,
        discovery,
        chaos,
        social,
        fletching,
        value);
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
        TraversalArrowConfig.fromJson(ConfigValues.readObject(root, KEY_TRAVERSAL)),
        TerrainArrowConfig.fromJson(ConfigValues.readObject(root, KEY_TERRAIN)),
        AgricultureArrowConfig.fromJson(ConfigValues.readObject(root, KEY_AGRICULTURE)),
        DiscoveryArrowConfig.fromJson(ConfigValues.readObject(root, KEY_DISCOVERY)),
        ChaosArrowConfig.fromJson(ConfigValues.readObject(root, KEY_CHAOS)),
        SocialArrowConfig.fromJson(ConfigValues.readObject(root, KEY_SOCIAL)),
        FletchingStationConfig.fromJson(ConfigValues.readObject(root, KEY_FLETCHING)),
        SoundConfig.fromJson(ConfigValues.readObject(root, KEY_SOUND)));
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
    root.add(KEY_TRAVERSAL, traversal.toJson());
    root.add(KEY_TERRAIN, terrain.toJson());
    root.add(KEY_AGRICULTURE, agriculture.toJson());
    root.add(KEY_DISCOVERY, discovery.toJson());
    root.add(KEY_CHAOS, chaos.toJson());
    root.add(KEY_SOCIAL, social.toJson());
    root.add(KEY_FLETCHING, fletching.toJson());
    root.add(KEY_SOUND, sound.toJson());
    return root;
  }
}
