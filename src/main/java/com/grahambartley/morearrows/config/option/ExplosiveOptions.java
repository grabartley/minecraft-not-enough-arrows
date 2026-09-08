package com.grahambartley.morearrows.config.option;

import com.grahambartley.morearrows.config.ConfigSettings;
import com.grahambartley.morearrows.config.ExplosiveArrowConfig;
import com.grahambartley.morearrows.config.ExplosiveTierConfig;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class ExplosiveOptions {
  public static final float POWER_STEP = 0.1f;
  public static final float BEEP_VOLUME_STEP = 0.05f;

  private ExplosiveOptions() {}

  private static final List<ConfigOption<MoreArrowsConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<MoreArrowsConfig> SECTION =
      new ConfigSection<>(ConfigSettings.EXPLOSIVE, OPTIONS);

  public static ConfigSection<MoreArrowsConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<MoreArrowsConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<MoreArrowsConfig>> buildOptions() {
    final List<ConfigOption<MoreArrowsConfig>> options = new ArrayList<>();
    addTier(
        options,
        ConfigSettings.EXPLOSIVE_GUNPOWDER_DELAY_TICKS,
        ConfigSettings.EXPLOSIVE_GUNPOWDER_POWER,
        ExplosiveArrowConfig::gunpowder,
        ExplosiveArrowConfig::withGunpowder);
    addTier(
        options,
        ConfigSettings.EXPLOSIVE_TNT_DELAY_TICKS,
        ConfigSettings.EXPLOSIVE_TNT_POWER,
        ExplosiveArrowConfig::tnt,
        ExplosiveArrowConfig::withTnt);
    addTier(
        options,
        ConfigSettings.EXPLOSIVE_FIRE_CHARGE_DELAY_TICKS,
        ConfigSettings.EXPLOSIVE_FIRE_CHARGE_POWER,
        ExplosiveArrowConfig::fireCharge,
        ExplosiveArrowConfig::withFireCharge);
    options.add(
        new BooleanOption<>(
            ConfigSettings.EXPLOSIVE_DAMAGE_TERRAIN,
            config -> config.explosive().damageTerrain(),
            (config, value) -> explosive(config, it -> it.withDamageTerrain(value))));
    options.add(
        new BooleanOption<>(
            ConfigSettings.EXPLOSIVE_DAMAGE_ENTITIES,
            config -> config.explosive().damageEntities(),
            (config, value) -> explosive(config, it -> it.withDamageEntities(value))));
    options.add(
        new IntOption<>(
            ConfigSettings.EXPLOSIVE_FIRE_PATCH_RADIUS,
            ExplosiveArrowConfig.FIRE_PATCH_RADIUS_MIN,
            ExplosiveArrowConfig.FIRE_PATCH_RADIUS_MAX,
            config -> config.explosive().firePatchRadius(),
            (config, value) -> explosive(config, it -> it.withFirePatchRadius(value))));
    options.add(
        new IntOption<>(
            ConfigSettings.EXPLOSIVE_FIRE_PATCH_DURATION_TICKS,
            ExplosiveArrowConfig.FIRE_PATCH_DURATION_TICKS_MIN,
            ExplosiveArrowConfig.FIRE_PATCH_DURATION_TICKS_MAX,
            config -> config.explosive().firePatchDurationTicks(),
            (config, value) -> explosive(config, it -> it.withFirePatchDurationTicks(value))));
    options.add(
        new FloatOption<>(
            ConfigSettings.EXPLOSIVE_BEEP_VOLUME,
            ExplosiveArrowConfig.BEEP_VOLUME_MIN,
            ExplosiveArrowConfig.BEEP_VOLUME_MAX,
            BEEP_VOLUME_STEP,
            config -> config.explosive().beepVolume(),
            (config, value) -> explosive(config, it -> it.withBeepVolume(value))));
    options.add(
        new IntOption<>(
            ConfigSettings.EXPLOSIVE_INCENDIARY_BURN_RADIUS,
            ExplosiveArrowConfig.INCENDIARY_BURN_RADIUS_MIN,
            ExplosiveArrowConfig.INCENDIARY_BURN_RADIUS_MAX,
            config -> config.explosive().incendiaryBurnRadius(),
            (config, value) -> explosive(config, it -> it.withIncendiaryBurnRadius(value))));
    options.add(
        new IntOption<>(
            ConfigSettings.EXPLOSIVE_INCENDIARY_IGNITE_SECONDS,
            ExplosiveArrowConfig.INCENDIARY_IGNITE_SECONDS_MIN,
            ExplosiveArrowConfig.INCENDIARY_IGNITE_SECONDS_MAX,
            config -> config.explosive().incendiaryIgniteSeconds(),
            (config, value) -> explosive(config, it -> it.withIncendiaryIgniteSeconds(value))));
    options.add(
        new BooleanOption<>(
            ConfigSettings.EXPLOSIVE_INCENDIARY_IGNITES_BLOCKS,
            config -> config.explosive().incendiaryIgnitesBlocks(),
            (config, value) -> explosive(config, it -> it.withIncendiaryIgnitesBlocks(value))));
    return List.copyOf(options);
  }

  private static void addTier(
      final List<ConfigOption<MoreArrowsConfig>> options,
      final String delayId,
      final String powerId,
      final Function<ExplosiveArrowConfig, ExplosiveTierConfig> tier,
      final BiFunction<ExplosiveArrowConfig, ExplosiveTierConfig, ExplosiveArrowConfig> withTier) {
    options.add(
        new IntOption<>(
            delayId,
            ExplosiveTierConfig.DELAY_TICKS_MIN,
            ExplosiveTierConfig.DELAY_TICKS_MAX,
            config -> tier.apply(config.explosive()).delayTicks(),
            (config, value) ->
                explosive(config, it -> withTier.apply(it, tier.apply(it).withDelayTicks(value)))));
    options.add(
        new FloatOption<>(
            powerId,
            ExplosiveTierConfig.POWER_MIN,
            ExplosiveTierConfig.POWER_MAX,
            POWER_STEP,
            config -> tier.apply(config.explosive()).power(),
            (config, value) ->
                explosive(config, it -> withTier.apply(it, tier.apply(it).withPower(value)))));
  }

  private static MoreArrowsConfig explosive(
      final MoreArrowsConfig config,
      final Function<ExplosiveArrowConfig, ExplosiveArrowConfig> change) {
    return config.withExplosive(change.apply(config.explosive()));
  }
}
