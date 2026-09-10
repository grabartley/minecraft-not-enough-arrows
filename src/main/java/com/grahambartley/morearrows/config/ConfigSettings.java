package com.grahambartley.morearrows.config;

public final class ConfigSettings {
  public static final String EXPLOSIVE = "explosive";
  public static final String GRAPPLE = "grapple";
  public static final String UTILITY = "utility";
  public static final String PHYSICS = "physics";
  public static final String FLETCHING = "fletching";

  public static final String EXPLOSIVE_GUNPOWDER = EXPLOSIVE + ".gunpowder";
  public static final String EXPLOSIVE_TNT = EXPLOSIVE + ".tnt";
  public static final String EXPLOSIVE_FIRE_CHARGE = EXPLOSIVE + ".fireCharge";

  public static final String DELAY_TICKS = "delayTicks";
  public static final String POWER = "power";

  public static final String EXPLOSIVE_GUNPOWDER_DELAY_TICKS =
      EXPLOSIVE_GUNPOWDER + "." + DELAY_TICKS;
  public static final String EXPLOSIVE_GUNPOWDER_POWER = EXPLOSIVE_GUNPOWDER + "." + POWER;
  public static final String EXPLOSIVE_TNT_DELAY_TICKS = EXPLOSIVE_TNT + "." + DELAY_TICKS;
  public static final String EXPLOSIVE_TNT_POWER = EXPLOSIVE_TNT + "." + POWER;
  public static final String EXPLOSIVE_FIRE_CHARGE_DELAY_TICKS =
      EXPLOSIVE_FIRE_CHARGE + "." + DELAY_TICKS;
  public static final String EXPLOSIVE_FIRE_CHARGE_POWER = EXPLOSIVE_FIRE_CHARGE + "." + POWER;
  public static final String EXPLOSIVE_DAMAGE_TERRAIN = EXPLOSIVE + ".damageTerrain";
  public static final String EXPLOSIVE_DAMAGE_ENTITIES = EXPLOSIVE + ".damageEntities";
  public static final String EXPLOSIVE_FIRE_PATCH_RADIUS = EXPLOSIVE + ".firePatchRadius";
  public static final String EXPLOSIVE_FIRE_PATCH_DURATION_TICKS =
      EXPLOSIVE + ".firePatchDurationTicks";
  public static final String EXPLOSIVE_BEEP_VOLUME = EXPLOSIVE + ".beepVolume";
  public static final String EXPLOSIVE_INCENDIARY = EXPLOSIVE + ".incendiary";
  public static final String EXPLOSIVE_INCENDIARY_BURN_RADIUS =
      EXPLOSIVE_INCENDIARY + ".burnRadius";
  public static final String EXPLOSIVE_INCENDIARY_IGNITE_SECONDS =
      EXPLOSIVE_INCENDIARY + ".igniteSeconds";
  public static final String EXPLOSIVE_INCENDIARY_IGNITES_BLOCKS =
      EXPLOSIVE_INCENDIARY + ".ignitesBlocks";

  public static final String GRAPPLE_MAX_RANGE_BLOCKS = GRAPPLE + ".maxRangeBlocks";
  public static final String GRAPPLE_PULL_SPEED = GRAPPLE + ".pullSpeed";
  public static final String GRAPPLE_PULL_ACCELERATION = GRAPPLE + ".pullAcceleration";
  public static final String GRAPPLE_CANCEL_FALL_DAMAGE_ON_ARRIVAL =
      GRAPPLE + ".cancelFallDamageOnArrival";
  public static final String GRAPPLE_RETURN_ARROW_ON_ARRIVAL = GRAPPLE + ".returnArrowOnArrival";
  public static final String GRAPPLE_ROPE_LENGTH_BLOCKS = GRAPPLE + ".ropeLengthBlocks";
  public static final String GRAPPLE_ROPES_DECAY = GRAPPLE + ".ropesDecay";

  public static final String UTILITY_GLOW_DURATION_TICKS = UTILITY + ".glowDurationTicks";
  public static final String UTILITY_REDSTONE_SIGNAL_DURATION_TICKS =
      UTILITY + ".redstoneSignalDurationTicks";
  public static final String UTILITY_REDSTONE_SIGNAL_STRENGTH = UTILITY + ".redstoneSignalStrength";
  public static final String UTILITY_WIND_BURST_RADIUS = UTILITY + ".windBurstRadius";
  public static final String UTILITY_WIND_PUSH_STRENGTH = UTILITY + ".windPushStrength";

  public static final String PHYSICS_GRAVITY_IMPACT_RADIUS = PHYSICS + ".gravityImpactRadius";
  public static final String PHYSICS_GRAVITY_BLOCK_EXCLUSIONS = PHYSICS + ".gravityBlockExclusions";
  public static final String PHYSICS_RICOCHET_BOUNCE_COUNT = PHYSICS + ".ricochetBounceCount";
  public static final String PHYSICS_RICOCHET_RETAINS_DAMAGE = PHYSICS + ".ricochetRetainsDamage";

  public static final String FLETCHING_STATION_ENABLED = FLETCHING + ".stationEnabled";

  public static final String ALL = "all";

  private ConfigSettings() {}
}
