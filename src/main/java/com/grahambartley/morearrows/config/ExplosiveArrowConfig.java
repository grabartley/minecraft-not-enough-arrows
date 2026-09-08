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
    IncendiaryArrowConfig incendiary) {

  public static final int FIRE_PATCH_RADIUS_MIN = 0;
  public static final int FIRE_PATCH_RADIUS_MAX = 8;
  public static final int FIRE_PATCH_DURATION_TICKS_MIN = 0;
  public static final int FIRE_PATCH_DURATION_TICKS_MAX = 6000;
  public static final float BEEP_VOLUME_MIN = 0.0f;
  public static final float BEEP_VOLUME_MAX = 2.0f;

  public static final ExplosiveTierConfig DEFAULT_GUNPOWDER = new ExplosiveTierConfig(60, 4.0f);
  public static final ExplosiveTierConfig DEFAULT_TNT = new ExplosiveTierConfig(50, 6.0f);
  public static final ExplosiveTierConfig DEFAULT_FIRE_CHARGE = new ExplosiveTierConfig(40, 8.0f);
  public static final boolean DEFAULT_DAMAGE_TERRAIN = false;
  public static final boolean DEFAULT_DAMAGE_ENTITIES = true;
  public static final int DEFAULT_FIRE_PATCH_RADIUS = 2;
  public static final int DEFAULT_FIRE_PATCH_DURATION_TICKS = 200;
  public static final float DEFAULT_BEEP_VOLUME = 1.0f;

  static final String KEY_GUNPOWDER = "gunpowder";
  static final String KEY_TNT = "tnt";
  static final String KEY_FIRE_CHARGE = "fireCharge";
  static final String KEY_DAMAGE_TERRAIN = "damageTerrain";
  static final String KEY_DAMAGE_ENTITIES = "damageEntities";
  static final String KEY_FIRE_PATCH_RADIUS = "firePatchRadius";
  static final String KEY_FIRE_PATCH_DURATION_TICKS = "firePatchDurationTicks";
  static final String KEY_BEEP_VOLUME = "beepVolume";
  static final String KEY_INCENDIARY = "incendiary";

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
    incendiary = incendiary == null ? IncendiaryArrowConfig.defaults() : incendiary;
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
        IncendiaryArrowConfig.defaults());
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
        IncendiaryArrowConfig.fromJson(
            ConfigValues.readObject(root, KEY_INCENDIARY), defaults.incendiary()));
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
    root.add(KEY_INCENDIARY, incendiary.toJson());
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
        incendiary);
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
        incendiary);
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
        incendiary);
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
        incendiary);
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
        incendiary);
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
        incendiary);
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
        incendiary);
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
        incendiary);
  }

  public ExplosiveArrowConfig withIncendiary(
      final java.util.function.UnaryOperator<IncendiaryArrowConfig> change) {
    return withIncendiary(change.apply(incendiary));
  }

  public ExplosiveArrowConfig withIncendiary(final IncendiaryArrowConfig value) {
    return new ExplosiveArrowConfig(
        gunpowder,
        tnt,
        fireCharge,
        damageTerrain,
        damageEntities,
        firePatchRadius,
        firePatchDurationTicks,
        beepVolume,
        value);
  }
}
