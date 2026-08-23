package com.grahambartley.morearrows.command;

import com.grahambartley.morearrows.config.ExplosiveArrowConfig;
import com.grahambartley.morearrows.config.GrappleArrowConfig;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.grahambartley.morearrows.config.PhysicsArrowConfig;
import com.grahambartley.morearrows.config.UtilityArrowConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.text.Text;

public final class ConfigStatusLines {
  public static final String HEADER_KEY = "command.more-arrows.status.header";
  public static final String ENTRY_KEY = "command.more-arrows.status.entry";

  private ConfigStatusLines() {}

  public record StatusEntry(String setting, String value) {}

  public static List<Text> lines(final MoreArrowsConfig config) {
    final List<Text> lines = new ArrayList<>();
    lines.add(Text.translatable(HEADER_KEY));
    for (final StatusEntry entry : entries(config)) {
      lines.add(Text.translatable(ENTRY_KEY, entry.setting(), entry.value()));
    }
    return List.copyOf(lines);
  }

  public static List<StatusEntry> entries(final MoreArrowsConfig config) {
    final List<StatusEntry> entries = new ArrayList<>();
    addExplosive(entries, config.explosive());
    addGrapple(entries, config.grapple());
    addUtility(entries, config.utility());
    addPhysics(entries, config.physics());
    return List.copyOf(entries);
  }

  private static void addExplosive(
      final List<StatusEntry> entries, final ExplosiveArrowConfig explosive) {
    entries.add(
        new StatusEntry(
            ConfigSettings.EXPLOSIVE_GUNPOWDER_DELAY_TICKS,
            ConfigValueFormat.of(explosive.gunpowder().delayTicks())));
    entries.add(
        new StatusEntry(
            ConfigSettings.EXPLOSIVE_GUNPOWDER_POWER,
            ConfigValueFormat.of(explosive.gunpowder().power())));
    entries.add(
        new StatusEntry(
            ConfigSettings.EXPLOSIVE_TNT_DELAY_TICKS,
            ConfigValueFormat.of(explosive.tnt().delayTicks())));
    entries.add(
        new StatusEntry(
            ConfigSettings.EXPLOSIVE_TNT_POWER, ConfigValueFormat.of(explosive.tnt().power())));
    entries.add(
        new StatusEntry(
            ConfigSettings.EXPLOSIVE_FIRE_CHARGE_DELAY_TICKS,
            ConfigValueFormat.of(explosive.fireCharge().delayTicks())));
    entries.add(
        new StatusEntry(
            ConfigSettings.EXPLOSIVE_FIRE_CHARGE_POWER,
            ConfigValueFormat.of(explosive.fireCharge().power())));
    entries.add(
        new StatusEntry(
            ConfigSettings.EXPLOSIVE_DAMAGE_TERRAIN,
            ConfigValueFormat.of(explosive.damageTerrain())));
    entries.add(
        new StatusEntry(
            ConfigSettings.EXPLOSIVE_DAMAGE_ENTITIES,
            ConfigValueFormat.of(explosive.damageEntities())));
    entries.add(
        new StatusEntry(
            ConfigSettings.EXPLOSIVE_FIRE_PATCH_RADIUS,
            ConfigValueFormat.of(explosive.firePatchRadius())));
    entries.add(
        new StatusEntry(
            ConfigSettings.EXPLOSIVE_FIRE_PATCH_DURATION_TICKS,
            ConfigValueFormat.of(explosive.firePatchDurationTicks())));
    entries.add(
        new StatusEntry(
            ConfigSettings.EXPLOSIVE_BEEP_VOLUME, ConfigValueFormat.of(explosive.beepVolume())));
  }

  private static void addGrapple(
      final List<StatusEntry> entries, final GrappleArrowConfig grapple) {
    entries.add(
        new StatusEntry(
            ConfigSettings.GRAPPLE_MAX_RANGE_BLOCKS,
            ConfigValueFormat.of(grapple.maxRangeBlocks())));
    entries.add(
        new StatusEntry(
            ConfigSettings.GRAPPLE_PULL_SPEED, ConfigValueFormat.of(grapple.pullSpeed())));
    entries.add(
        new StatusEntry(
            ConfigSettings.GRAPPLE_CANCEL_FALL_DAMAGE_ON_ARRIVAL,
            ConfigValueFormat.of(grapple.cancelFallDamageOnArrival())));
    entries.add(
        new StatusEntry(
            ConfigSettings.GRAPPLE_RETURN_ARROW_ON_ARRIVAL,
            ConfigValueFormat.of(grapple.returnArrowOnArrival())));
    entries.add(
        new StatusEntry(
            ConfigSettings.GRAPPLE_ROPE_LENGTH_BLOCKS,
            ConfigValueFormat.of(grapple.ropeLengthBlocks())));
    entries.add(
        new StatusEntry(
            ConfigSettings.GRAPPLE_ROPES_DECAY, ConfigValueFormat.of(grapple.ropesDecay())));
  }

  private static void addUtility(
      final List<StatusEntry> entries, final UtilityArrowConfig utility) {
    entries.add(
        new StatusEntry(
            ConfigSettings.UTILITY_GLOW_DURATION_TICKS,
            ConfigValueFormat.of(utility.glowDurationTicks())));
    entries.add(
        new StatusEntry(
            ConfigSettings.UTILITY_REDSTONE_SIGNAL_DURATION_TICKS,
            ConfigValueFormat.of(utility.redstoneSignalDurationTicks())));
    entries.add(
        new StatusEntry(
            ConfigSettings.UTILITY_REDSTONE_SIGNAL_STRENGTH,
            ConfigValueFormat.of(utility.redstoneSignalStrength())));
    entries.add(
        new StatusEntry(
            ConfigSettings.UTILITY_WIND_BURST_RADIUS,
            ConfigValueFormat.of(utility.windBurstRadius())));
    entries.add(
        new StatusEntry(
            ConfigSettings.UTILITY_WIND_PUSH_STRENGTH,
            ConfigValueFormat.of(utility.windPushStrength())));
  }

  private static void addPhysics(
      final List<StatusEntry> entries, final PhysicsArrowConfig physics) {
    entries.add(
        new StatusEntry(
            ConfigSettings.PHYSICS_GRAVITY_IMPACT_RADIUS,
            ConfigValueFormat.of(physics.gravityImpactRadius())));
    entries.add(
        new StatusEntry(
            ConfigSettings.PHYSICS_GRAVITY_BLOCK_EXCLUSIONS,
            ConfigValueFormat.of(physics.gravityBlockExclusions())));
    entries.add(
        new StatusEntry(
            ConfigSettings.PHYSICS_RICOCHET_BOUNCE_COUNT,
            ConfigValueFormat.of(physics.ricochetBounceCount())));
    entries.add(
        new StatusEntry(
            ConfigSettings.PHYSICS_RICOCHET_RETAINS_DAMAGE,
            ConfigValueFormat.of(physics.ricochetRetainsDamage())));
  }
}
