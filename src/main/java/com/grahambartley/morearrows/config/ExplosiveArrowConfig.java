package com.grahambartley.morearrows.config;

import com.google.gson.JsonObject;

public record ExplosiveArrowConfig(
    ExplosiveTierConfig gunpowder,
    ExplosiveTierConfig tnt,
    ExplosiveTierConfig fireCharge,
    boolean damageTerrain,
    boolean damageEntities,
    int firePatchRadius,
    int firePatchDurationTicks,
    float beepVolume,
    int incendiaryBurnRadius,
    int incendiaryIgniteSeconds,
    boolean incendiaryIgnitesBlocks) {

  public static final int FIRE_PATCH_RADIUS_MIN = 0;
  public static final int FIRE_PATCH_RADIUS_MAX = 8;
  public static final int FIRE_PATCH_DURATION_TICKS_MIN = 0;
  public static final int FIRE_PATCH_DURATION_TICKS_MAX = 6000;
  public static final float BEEP_VOLUME_MIN = 0.0f;
  public static final float BEEP_VOLUME_MAX = 2.0f;
  public static final int INCENDIARY_BURN_RADIUS_MIN = 0;
  public static final int INCENDIARY_BURN_RADIUS_MAX = 8;
  public static final int INCENDIARY_IGNITE_SECONDS_MIN = 0;
  public static final int INCENDIARY_IGNITE_SECONDS_MAX = 60;

  public static final ExplosiveTierConfig DEFAULT_GUNPOWDER = new ExplosiveTierConfig(60, 4.0f);
  public static final ExplosiveTierConfig DEFAULT_TNT = new ExplosiveTierConfig(50, 6.0f);
  public static final ExplosiveTierConfig DEFAULT_FIRE_CHARGE = new ExplosiveTierConfig(40, 8.0f);
  public static final boolean DEFAULT_DAMAGE_TERRAIN = false;
  public static final boolean DEFAULT_DAMAGE_ENTITIES = true;
  public static final int DEFAULT_FIRE_PATCH_RADIUS = 2;
  public static final int DEFAULT_FIRE_PATCH_DURATION_TICKS = 200;
  public static final float DEFAULT_BEEP_VOLUME = 1.0f;
  public static final int DEFAULT_INCENDIARY_BURN_RADIUS = 3;
  public static final int DEFAULT_INCENDIARY_IGNITE_SECONDS = 5;
  public static final boolean DEFAULT_INCENDIARY_IGNITES_BLOCKS = true;

  static final String KEY_GUNPOWDER = "gunpowder";
  static final String KEY_TNT = "tnt";
  static final String KEY_FIRE_CHARGE = "fireCharge";
  static final String KEY_DAMAGE_TERRAIN = "damageTerrain";
  static final String KEY_DAMAGE_ENTITIES = "damageEntities";
  static final String KEY_FIRE_PATCH_RADIUS = "firePatchRadius";
  static final String KEY_FIRE_PATCH_DURATION_TICKS = "firePatchDurationTicks";
  static final String KEY_BEEP_VOLUME = "beepVolume";
  static final String KEY_INCENDIARY_BURN_RADIUS = "incendiaryBurnRadius";
  static final String KEY_INCENDIARY_IGNITE_SECONDS = "incendiaryIgniteSeconds";
  static final String KEY_INCENDIARY_IGNITES_BLOCKS = "incendiaryIgnitesBlocks";

  public ExplosiveArrowConfig {
    gunpowder = gunpowder == null ? DEFAULT_GUNPOWDER : gunpowder;
    tnt = tnt == null ? DEFAULT_TNT : tnt;
    fireCharge = fireCharge == null ? DEFAULT_FIRE_CHARGE : fireCharge;
    firePatchRadius =
        ConfigValues.clampInt(firePatchRadius, FIRE_PATCH_RADIUS_MIN, FIRE_PATCH_RADIUS_MAX);
    firePatchDurationTicks =
        ConfigValues.clampInt(
            firePatchDurationTicks, FIRE_PATCH_DURATION_TICKS_MIN, FIRE_PATCH_DURATION_TICKS_MAX);
    beepVolume = ConfigValues.clampFloat(beepVolume, BEEP_VOLUME_MIN, BEEP_VOLUME_MAX);
    incendiaryBurnRadius =
        ConfigValues.clampInt(
            incendiaryBurnRadius, INCENDIARY_BURN_RADIUS_MIN, INCENDIARY_BURN_RADIUS_MAX);
    incendiaryIgniteSeconds =
        ConfigValues.clampInt(
            incendiaryIgniteSeconds, INCENDIARY_IGNITE_SECONDS_MIN, INCENDIARY_IGNITE_SECONDS_MAX);
  }

  public static ExplosiveArrowConfig defaults() {
    return new ExplosiveArrowConfig(
        DEFAULT_GUNPOWDER,
        DEFAULT_TNT,
        DEFAULT_FIRE_CHARGE,
        DEFAULT_DAMAGE_TERRAIN,
        DEFAULT_DAMAGE_ENTITIES,
        DEFAULT_FIRE_PATCH_RADIUS,
        DEFAULT_FIRE_PATCH_DURATION_TICKS,
        DEFAULT_BEEP_VOLUME,
        DEFAULT_INCENDIARY_BURN_RADIUS,
        DEFAULT_INCENDIARY_IGNITE_SECONDS,
        DEFAULT_INCENDIARY_IGNITES_BLOCKS);
  }

  public static ExplosiveArrowConfig fromJson(final JsonObject root) {
    final ExplosiveArrowConfig defaults = defaults();
    return new ExplosiveArrowConfig(
        ExplosiveTierConfig.fromJson(
            ConfigValues.readObject(root, KEY_GUNPOWDER), defaults.gunpowder()),
        ExplosiveTierConfig.fromJson(ConfigValues.readObject(root, KEY_TNT), defaults.tnt()),
        ExplosiveTierConfig.fromJson(
            ConfigValues.readObject(root, KEY_FIRE_CHARGE), defaults.fireCharge()),
        ConfigValues.readBoolean(root, KEY_DAMAGE_TERRAIN, defaults.damageTerrain()),
        ConfigValues.readBoolean(root, KEY_DAMAGE_ENTITIES, defaults.damageEntities()),
        ConfigValues.readInt(
            root,
            KEY_FIRE_PATCH_RADIUS,
            defaults.firePatchRadius(),
            FIRE_PATCH_RADIUS_MIN,
            FIRE_PATCH_RADIUS_MAX),
        ConfigValues.readInt(
            root,
            KEY_FIRE_PATCH_DURATION_TICKS,
            defaults.firePatchDurationTicks(),
            FIRE_PATCH_DURATION_TICKS_MIN,
            FIRE_PATCH_DURATION_TICKS_MAX),
        ConfigValues.readFloat(
            root, KEY_BEEP_VOLUME, defaults.beepVolume(), BEEP_VOLUME_MIN, BEEP_VOLUME_MAX),
        ConfigValues.readInt(
            root,
            KEY_INCENDIARY_BURN_RADIUS,
            defaults.incendiaryBurnRadius(),
            INCENDIARY_BURN_RADIUS_MIN,
            INCENDIARY_BURN_RADIUS_MAX),
        ConfigValues.readInt(
            root,
            KEY_INCENDIARY_IGNITE_SECONDS,
            defaults.incendiaryIgniteSeconds(),
            INCENDIARY_IGNITE_SECONDS_MIN,
            INCENDIARY_IGNITE_SECONDS_MAX),
        ConfigValues.readBoolean(
            root, KEY_INCENDIARY_IGNITES_BLOCKS, defaults.incendiaryIgnitesBlocks()));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.add(KEY_GUNPOWDER, gunpowder.toJson());
    root.add(KEY_TNT, tnt.toJson());
    root.add(KEY_FIRE_CHARGE, fireCharge.toJson());
    root.addProperty(KEY_DAMAGE_TERRAIN, damageTerrain);
    root.addProperty(KEY_DAMAGE_ENTITIES, damageEntities);
    root.addProperty(KEY_FIRE_PATCH_RADIUS, firePatchRadius);
    root.addProperty(KEY_FIRE_PATCH_DURATION_TICKS, firePatchDurationTicks);
    root.addProperty(KEY_BEEP_VOLUME, beepVolume);
    root.addProperty(KEY_INCENDIARY_BURN_RADIUS, incendiaryBurnRadius);
    root.addProperty(KEY_INCENDIARY_IGNITE_SECONDS, incendiaryIgniteSeconds);
    root.addProperty(KEY_INCENDIARY_IGNITES_BLOCKS, incendiaryIgnitesBlocks);
    return root;
  }

  public ExplosiveArrowConfig withGunpowder(final ExplosiveTierConfig value) {
    return new ExplosiveArrowConfig(
        value,
        tnt,
        fireCharge,
        damageTerrain,
        damageEntities,
        firePatchRadius,
        firePatchDurationTicks,
        beepVolume,
        incendiaryBurnRadius,
        incendiaryIgniteSeconds,
        incendiaryIgnitesBlocks);
  }

  public ExplosiveArrowConfig withTnt(final ExplosiveTierConfig value) {
    return new ExplosiveArrowConfig(
        gunpowder,
        value,
        fireCharge,
        damageTerrain,
        damageEntities,
        firePatchRadius,
        firePatchDurationTicks,
        beepVolume,
        incendiaryBurnRadius,
        incendiaryIgniteSeconds,
        incendiaryIgnitesBlocks);
  }

  public ExplosiveArrowConfig withFireCharge(final ExplosiveTierConfig value) {
    return new ExplosiveArrowConfig(
        gunpowder,
        tnt,
        value,
        damageTerrain,
        damageEntities,
        firePatchRadius,
        firePatchDurationTicks,
        beepVolume,
        incendiaryBurnRadius,
        incendiaryIgniteSeconds,
        incendiaryIgnitesBlocks);
  }

  public ExplosiveArrowConfig withDamageTerrain(final boolean value) {
    return new ExplosiveArrowConfig(
        gunpowder,
        tnt,
        fireCharge,
        value,
        damageEntities,
        firePatchRadius,
        firePatchDurationTicks,
        beepVolume,
        incendiaryBurnRadius,
        incendiaryIgniteSeconds,
        incendiaryIgnitesBlocks);
  }

  public ExplosiveArrowConfig withDamageEntities(final boolean value) {
    return new ExplosiveArrowConfig(
        gunpowder,
        tnt,
        fireCharge,
        damageTerrain,
        value,
        firePatchRadius,
        firePatchDurationTicks,
        beepVolume,
        incendiaryBurnRadius,
        incendiaryIgniteSeconds,
        incendiaryIgnitesBlocks);
  }

  public ExplosiveArrowConfig withFirePatchRadius(final int value) {
    return new ExplosiveArrowConfig(
        gunpowder,
        tnt,
        fireCharge,
        damageTerrain,
        damageEntities,
        value,
        firePatchDurationTicks,
        beepVolume,
        incendiaryBurnRadius,
        incendiaryIgniteSeconds,
        incendiaryIgnitesBlocks);
  }

  public ExplosiveArrowConfig withFirePatchDurationTicks(final int value) {
    return new ExplosiveArrowConfig(
        gunpowder,
        tnt,
        fireCharge,
        damageTerrain,
        damageEntities,
        firePatchRadius,
        value,
        beepVolume,
        incendiaryBurnRadius,
        incendiaryIgniteSeconds,
        incendiaryIgnitesBlocks);
  }

  public ExplosiveArrowConfig withBeepVolume(final float value) {
    return new ExplosiveArrowConfig(
        gunpowder,
        tnt,
        fireCharge,
        damageTerrain,
        damageEntities,
        firePatchRadius,
        firePatchDurationTicks,
        value,
        incendiaryBurnRadius,
        incendiaryIgniteSeconds,
        incendiaryIgnitesBlocks);
  }

  public ExplosiveArrowConfig withIncendiaryBurnRadius(final int value) {
    return new ExplosiveArrowConfig(
        gunpowder,
        tnt,
        fireCharge,
        damageTerrain,
        damageEntities,
        firePatchRadius,
        firePatchDurationTicks,
        beepVolume,
        value,
        incendiaryIgniteSeconds,
        incendiaryIgnitesBlocks);
  }

  public ExplosiveArrowConfig withIncendiaryIgniteSeconds(final int value) {
    return new ExplosiveArrowConfig(
        gunpowder,
        tnt,
        fireCharge,
        damageTerrain,
        damageEntities,
        firePatchRadius,
        firePatchDurationTicks,
        beepVolume,
        incendiaryBurnRadius,
        value,
        incendiaryIgnitesBlocks);
  }

  public ExplosiveArrowConfig withIncendiaryIgnitesBlocks(final boolean value) {
    return new ExplosiveArrowConfig(
        gunpowder,
        tnt,
        fireCharge,
        damageTerrain,
        damageEntities,
        firePatchRadius,
        firePatchDurationTicks,
        beepVolume,
        incendiaryBurnRadius,
        incendiaryIgniteSeconds,
        value);
  }
}
