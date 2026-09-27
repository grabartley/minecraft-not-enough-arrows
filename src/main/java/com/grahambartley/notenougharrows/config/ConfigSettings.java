package com.grahambartley.notenougharrows.config;

public final class ConfigSettings {
  public static final String EXPLOSIVE = "explosive";
  public static final String GRAPPLE = "grapple";
  public static final String UTILITY = "utility";
  public static final String PHYSICS = "physics";
  public static final String ENDER = "ender";
  public static final String COMBAT = "combat";
  public static final String CONTROL = "control";
  public static final String TRAVERSAL = "traversal";
  public static final String TERRAIN = "terrain";
  public static final String AGRICULTURE = "agriculture";
  public static final String DISCOVERY = "discovery";
  public static final String CHAOS = "chaos";
  public static final String SOCIAL = "social";
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

  public static final String ENDER_PEARL_MAX_RANGE_BLOCKS = ENDER + ".pearlMaxRangeBlocks";
  public static final String ENDER_RECALL_MAX_RANGE_BLOCKS = ENDER + ".recallMaxRangeBlocks";
  public static final String ENDER_RECALL_AFFECTS_PLAYERS = ENDER + ".recallAffectsPlayers";

  public static final String COMBAT_SHOCK = COMBAT + ".shock";
  public static final String COMBAT_LIFESTEAL = COMBAT + ".lifesteal";
  public static final String COMBAT_STATUS = COMBAT + ".status";
  public static final String COMBAT_HOMING = COMBAT + ".homing";
  public static final String COMBAT_VOLLEY = COMBAT + ".volley";
  public static final String COMBAT_RAILGUN = COMBAT + ".railgun";

  public static final String COMBAT_SHOCK_ARC_RADIUS = COMBAT_SHOCK + ".arcRadius";
  public static final String COMBAT_SHOCK_DAMAGE = COMBAT_SHOCK + ".damage";
  public static final String COMBAT_LIFESTEAL_SHARE = COMBAT_LIFESTEAL + ".share";
  public static final String COMBAT_LIFESTEAL_MAX_HEAL_PER_HIT =
      COMBAT_LIFESTEAL + ".maxHealPerHit";
  public static final String COMBAT_STATUS_RUST_DURATION_TICKS =
      COMBAT_STATUS + ".rustDurationTicks";
  public static final String COMBAT_STATUS_HASTE_DURATION_TICKS =
      COMBAT_STATUS + ".hasteDurationTicks";
  public static final String COMBAT_STATUS_GUARD_DURATION_TICKS =
      COMBAT_STATUS + ".guardDurationTicks";
  public static final String COMBAT_HOMING_TURN_RATE = COMBAT_HOMING + ".turnRate";
  public static final String COMBAT_HOMING_SEARCH_RADIUS = COMBAT_HOMING + ".searchRadius";
  public static final String COMBAT_HOMING_SEARCH_CONE_DEGREES =
      COMBAT_HOMING + ".searchConeDegrees";
  public static final String COMBAT_VOLLEY_FRAGMENT_COUNT = COMBAT_VOLLEY + ".fragmentCount";
  public static final String COMBAT_VOLLEY_DAMAGE_SHARE = COMBAT_VOLLEY + ".damageShare";
  public static final String COMBAT_VOLLEY_SPREAD_DEGREES = COMBAT_VOLLEY + ".spreadDegrees";
  public static final String COMBAT_VOLLEY_SPLIT_DELAY_TICKS = COMBAT_VOLLEY + ".splitDelayTicks";
  public static final String COMBAT_RAILGUN_SPEED_MULTIPLIER = COMBAT_RAILGUN + ".speedMultiplier";
  public static final String COMBAT_RAILGUN_GRAVITY_FACTOR = COMBAT_RAILGUN + ".gravityFactor";

  public static final String CONTROL_FROST = CONTROL + ".frost";
  public static final String CONTROL_LEVITATION = CONTROL + ".levitation";
  public static final String CONTROL_TARGETING = CONTROL + ".targeting";
  public static final String CONTROL_ALLEGIANCE = CONTROL + ".allegiance";
  public static final String CONTROL_SMOKE = CONTROL + ".smoke";
  public static final String CONTROL_DISARM = CONTROL + ".disarm";

  public static final String CONTROL_FROST_DURATION_TICKS = CONTROL_FROST + ".durationTicks";
  public static final String CONTROL_LEVITATION_DURATION_TICKS =
      CONTROL_LEVITATION + ".durationTicks";
  public static final String CONTROL_TARGETING_TAUNT_RADIUS = CONTROL_TARGETING + ".tauntRadius";
  public static final String CONTROL_TARGETING_TAUNT_DURATION_TICKS =
      CONTROL_TARGETING + ".tauntDurationTicks";
  public static final String CONTROL_TARGETING_REPEL_RADIUS = CONTROL_TARGETING + ".repelRadius";
  public static final String CONTROL_TARGETING_REPEL_DURATION_TICKS =
      CONTROL_TARGETING + ".repelDurationTicks";
  public static final String CONTROL_TARGETING_REPEL_DISTANCE =
      CONTROL_TARGETING + ".repelDistance";
  public static final String CONTROL_ALLEGIANCE_DURATION_TICKS =
      CONTROL_ALLEGIANCE + ".durationTicks";
  public static final String CONTROL_ALLEGIANCE_DEFEND_RADIUS =
      CONTROL_ALLEGIANCE + ".defendRadius";
  public static final String CONTROL_SMOKE_RADIUS = CONTROL_SMOKE + ".radius";
  public static final String CONTROL_SMOKE_DURATION_TICKS = CONTROL_SMOKE + ".durationTicks";
  public static final String CONTROL_DISARM_AFFECTS_PLAYERS = CONTROL_DISARM + ".affectsPlayers";
  public static final String CONTROL_DISARM_THROW_DISTANCE = CONTROL_DISARM + ".throwDistance";

  public static final String TRAVERSAL_ZIPLINE = TRAVERSAL + ".zipline";
  public static final String TRAVERSAL_TOW = TRAVERSAL + ".tow";
  public static final String TRAVERSAL_UPDRAFT = TRAVERSAL + ".updraft";
  public static final String TRAVERSAL_VINE = TRAVERSAL + ".vine";
  public static final String TRAVERSAL_TRAMPOLINE = TRAVERSAL + ".trampoline";
  public static final String TRAVERSAL_SCAFFOLD = TRAVERSAL + ".scaffold";
  public static final String TRAVERSAL_BRIDGE = TRAVERSAL + ".bridge";

  public static final String TRAVERSAL_ZIPLINE_MAX_SPAN_BLOCKS =
      TRAVERSAL_ZIPLINE + ".maxSpanBlocks";
  public static final String TRAVERSAL_ZIPLINE_PENDING_WINDOW_TICKS =
      TRAVERSAL_ZIPLINE + ".pendingWindowTicks";
  public static final String TRAVERSAL_ZIPLINE_RIDE_SPEED = TRAVERSAL_ZIPLINE + ".rideSpeed";
  public static final String TRAVERSAL_ZIPLINE_LIFETIME_TICKS =
      TRAVERSAL_ZIPLINE + ".lifetimeTicks";
  public static final String TRAVERSAL_TOW_RANGE_BLOCKS = TRAVERSAL_TOW + ".rangeBlocks";
  public static final String TRAVERSAL_TOW_MAX_TICKS = TRAVERSAL_TOW + ".maxTicks";
  public static final String TRAVERSAL_UPDRAFT_HEIGHT_BLOCKS = TRAVERSAL_UPDRAFT + ".heightBlocks";
  public static final String TRAVERSAL_UPDRAFT_LIFETIME_TICKS =
      TRAVERSAL_UPDRAFT + ".lifetimeTicks";
  public static final String TRAVERSAL_UPDRAFT_STRENGTH = TRAVERSAL_UPDRAFT + ".strength";
  public static final String TRAVERSAL_VINE_LENGTH_BLOCKS = TRAVERSAL_VINE + ".lengthBlocks";
  public static final String TRAVERSAL_TRAMPOLINE_STRENGTH = TRAVERSAL_TRAMPOLINE + ".strength";
  public static final String TRAVERSAL_TRAMPOLINE_LIFETIME_TICKS =
      TRAVERSAL_TRAMPOLINE + ".lifetimeTicks";
  public static final String TRAVERSAL_SCAFFOLD_HEIGHT_BLOCKS =
      TRAVERSAL_SCAFFOLD + ".heightBlocks";
  public static final String TRAVERSAL_SCAFFOLD_LIFETIME_TICKS =
      TRAVERSAL_SCAFFOLD + ".lifetimeTicks";
  public static final String TRAVERSAL_BRIDGE_LENGTH_BLOCKS = TRAVERSAL_BRIDGE + ".lengthBlocks";
  public static final String TRAVERSAL_BRIDGE_LIFETIME_TICKS = TRAVERSAL_BRIDGE + ".lifetimeTicks";

  public static final String TERRAIN_DRILL_ENABLED = TERRAIN + ".drillEnabled";
  public static final String TERRAIN_DRILL_TOOL_TIER = TERRAIN + ".drillToolTier";
  public static final String TERRAIN_PILLAR_ENABLED = TERRAIN + ".pillarEnabled";
  public static final String TERRAIN_PILLAR_HEIGHT_BLOCKS = TERRAIN + ".pillarHeightBlocks";
  public static final String TERRAIN_PILLAR_LIFETIME_TICKS = TERRAIN + ".pillarLifetimeTicks";
  public static final String TERRAIN_DRAIN_ENABLED = TERRAIN + ".drainEnabled";
  public static final String TERRAIN_DRAIN_RADIUS = TERRAIN + ".drainRadius";
  public static final String TERRAIN_DRAIN_MAX_BLOCKS = TERRAIN + ".drainMaxBlocks";
  public static final String TERRAIN_FREEZE_ENABLED = TERRAIN + ".freezeEnabled";
  public static final String TERRAIN_FREEZE_RADIUS = TERRAIN + ".freezeRadius";
  public static final String TERRAIN_WEB_ENABLED = TERRAIN + ".webEnabled";
  public static final String TERRAIN_WEB_PATCH_RADIUS = TERRAIN + ".webPatchRadius";
  public static final String TERRAIN_WEB_LIFETIME_TICKS = TERRAIN + ".webLifetimeTicks";
  public static final String TERRAIN_PAINT_ENABLED = TERRAIN + ".paintEnabled";

  public static final String AGRICULTURE_BLOSSOM_RADIUS = AGRICULTURE + ".blossomRadius";
  public static final String AGRICULTURE_TILL_RADIUS = AGRICULTURE + ".tillRadius";
  public static final String AGRICULTURE_HARVEST_RADIUS = AGRICULTURE + ".harvestRadius";
  public static final String AGRICULTURE_BEE_COUNT = AGRICULTURE + ".beeCount";
  public static final String AGRICULTURE_BEE_LIFETIME_TICKS = AGRICULTURE + ".beeLifetimeTicks";

  public static final String DISCOVERY_TORCH_ENABLED = DISCOVERY + ".torchEnabled";
  public static final String DISCOVERY_BEACON_LIFETIME_TICKS = DISCOVERY + ".beaconLifetimeTicks";
  public static final String DISCOVERY_REVEAL_RADIUS = DISCOVERY + ".revealRadius";
  public static final String DISCOVERY_REVEAL_DURATION_TICKS = DISCOVERY + ".revealDurationTicks";
  public static final String DISCOVERY_PROSPECTOR_BLOCKS = DISCOVERY + ".prospectorBlocks";
  public static final String DISCOVERY_WATCHER_LIFETIME_TICKS = DISCOVERY + ".watcherLifetimeTicks";
  public static final String DISCOVERY_WATCHER_REPORT_INTERVAL_TICKS =
      DISCOVERY + ".watcherReportIntervalTicks";
  public static final String DISCOVERY_TRACER_PATH_LIFETIME_TICKS =
      DISCOVERY + ".tracerPathLifetimeTicks";

  public static final String CHAOS_PARTY_ENABLED = CHAOS + ".partyEnabled";
  public static final String CHAOS_CHICKEN_ENABLED = CHAOS + ".chickenEnabled";
  public static final String CHAOS_PUFFER_ENABLED = CHAOS + ".pufferEnabled";
  public static final String CHAOS_PUFFER_DURATION_TICKS = CHAOS + ".pufferDurationTicks";
  public static final String CHAOS_STINK_ENABLED = CHAOS + ".stinkEnabled";
  public static final String CHAOS_STINK_CLOUD_LIFETIME_TICKS = CHAOS + ".stinkCloudLifetimeTicks";
  public static final String CHAOS_BOOMERANG_ENABLED = CHAOS + ".boomerangEnabled";
  public static final String CHAOS_POLYMORPH_ENABLED = CHAOS + ".polymorphEnabled";
  public static final String CHAOS_POLYMORPH_DURATION_TICKS = CHAOS + ".polymorphDurationTicks";

  public static final String SOCIAL_COURIER_MAX_PAYLOAD = SOCIAL + ".courierMaxPayload";
  public static final String SOCIAL_COURIER_UNDELIVERABLE = SOCIAL + ".courierUndeliverable";
  public static final String SOCIAL_SNOW_GOLEM_LIFETIME_TICKS = SOCIAL + ".snowGolemLifetimeTicks";
  public static final String SOCIAL_MAGNET_RADIUS = SOCIAL + ".magnetRadius";

  public static final String FLETCHING_STATION_ENABLED = FLETCHING + ".stationEnabled";

  public static final String ALL = "all";

  private ConfigSettings() {}
}
