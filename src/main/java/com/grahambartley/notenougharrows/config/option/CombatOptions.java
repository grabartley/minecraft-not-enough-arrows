package com.grahambartley.notenougharrows.config.option;

import com.grahambartley.notenougharrows.config.CombatArrowConfig;
import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.HomingArrowConfig;
import com.grahambartley.notenougharrows.config.LifestealArrowConfig;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.RailgunArrowConfig;
import com.grahambartley.notenougharrows.config.ShockArrowConfig;
import com.grahambartley.notenougharrows.config.StatusArrowConfig;
import com.grahambartley.notenougharrows.config.VolleyArrowConfig;
import java.util.List;
import java.util.function.Function;

public final class CombatOptions {
  public static final float DISTANCE_STEP = 0.5f;
  public static final float DAMAGE_STEP = 0.5f;
  public static final float SHARE_STEP = 0.05f;
  public static final float ANGLE_STEP = 1.0f;

  private CombatOptions() {}

  private static final List<ConfigOption<NotEnoughArrowsConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<NotEnoughArrowsConfig> SECTION =
      new ConfigSection<>(ConfigSettings.COMBAT, OPTIONS);

  public static ConfigSection<NotEnoughArrowsConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<NotEnoughArrowsConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<NotEnoughArrowsConfig>> buildOptions() {
    return List.of(
        new FloatOption<>(
            ConfigSettings.COMBAT_SHOCK_ARC_RADIUS,
            ShockArrowConfig.ARC_RADIUS_MIN,
            ShockArrowConfig.ARC_RADIUS_MAX,
            DISTANCE_STEP,
            config -> config.combat().shock().arcRadius(),
            (config, value) -> shock(config, it -> it.withArcRadius(value))),
        new FloatOption<>(
            ConfigSettings.COMBAT_SHOCK_DAMAGE,
            ShockArrowConfig.DAMAGE_MIN,
            ShockArrowConfig.DAMAGE_MAX,
            DAMAGE_STEP,
            config -> config.combat().shock().damage(),
            (config, value) -> shock(config, it -> it.withDamage(value))),
        new FloatOption<>(
            ConfigSettings.COMBAT_LIFESTEAL_SHARE,
            LifestealArrowConfig.SHARE_MIN,
            LifestealArrowConfig.SHARE_MAX,
            SHARE_STEP,
            config -> config.combat().lifesteal().share(),
            (config, value) -> lifesteal(config, it -> it.withShare(value))),
        new FloatOption<>(
            ConfigSettings.COMBAT_LIFESTEAL_MAX_HEAL_PER_HIT,
            LifestealArrowConfig.MAX_HEAL_PER_HIT_MIN,
            LifestealArrowConfig.MAX_HEAL_PER_HIT_MAX,
            DAMAGE_STEP,
            config -> config.combat().lifesteal().maxHealPerHit(),
            (config, value) -> lifesteal(config, it -> it.withMaxHealPerHit(value))),
        new IntOption<>(
            ConfigSettings.COMBAT_STATUS_RUST_DURATION_TICKS,
            StatusArrowConfig.DURATION_TICKS_MIN,
            StatusArrowConfig.DURATION_TICKS_MAX,
            config -> config.combat().status().rustDurationTicks(),
            (config, value) -> status(config, it -> it.withRustDurationTicks(value))),
        new IntOption<>(
            ConfigSettings.COMBAT_STATUS_HASTE_DURATION_TICKS,
            StatusArrowConfig.DURATION_TICKS_MIN,
            StatusArrowConfig.DURATION_TICKS_MAX,
            config -> config.combat().status().hasteDurationTicks(),
            (config, value) -> status(config, it -> it.withHasteDurationTicks(value))),
        new IntOption<>(
            ConfigSettings.COMBAT_STATUS_GUARD_DURATION_TICKS,
            StatusArrowConfig.DURATION_TICKS_MIN,
            StatusArrowConfig.DURATION_TICKS_MAX,
            config -> config.combat().status().guardDurationTicks(),
            (config, value) -> status(config, it -> it.withGuardDurationTicks(value))),
        new FloatOption<>(
            ConfigSettings.COMBAT_HOMING_TURN_RATE,
            HomingArrowConfig.TURN_RATE_MIN,
            HomingArrowConfig.TURN_RATE_MAX,
            SHARE_STEP,
            config -> config.combat().homing().turnRate(),
            (config, value) -> homing(config, it -> it.withTurnRate(value))),
        new FloatOption<>(
            ConfigSettings.COMBAT_HOMING_SEARCH_RADIUS,
            HomingArrowConfig.SEARCH_RADIUS_MIN,
            HomingArrowConfig.SEARCH_RADIUS_MAX,
            DISTANCE_STEP,
            config -> config.combat().homing().searchRadius(),
            (config, value) -> homing(config, it -> it.withSearchRadius(value))),
        new FloatOption<>(
            ConfigSettings.COMBAT_HOMING_SEARCH_CONE_DEGREES,
            HomingArrowConfig.SEARCH_CONE_DEGREES_MIN,
            HomingArrowConfig.SEARCH_CONE_DEGREES_MAX,
            ANGLE_STEP,
            config -> config.combat().homing().searchConeDegrees(),
            (config, value) -> homing(config, it -> it.withSearchConeDegrees(value))),
        new IntOption<>(
            ConfigSettings.COMBAT_VOLLEY_FRAGMENT_COUNT,
            VolleyArrowConfig.FRAGMENT_COUNT_MIN,
            VolleyArrowConfig.FRAGMENT_COUNT_MAX,
            config -> config.combat().volley().fragmentCount(),
            (config, value) -> volley(config, it -> it.withFragmentCount(value))),
        new FloatOption<>(
            ConfigSettings.COMBAT_VOLLEY_DAMAGE_SHARE,
            VolleyArrowConfig.DAMAGE_SHARE_MIN,
            VolleyArrowConfig.DAMAGE_SHARE_MAX,
            SHARE_STEP,
            config -> config.combat().volley().damageShare(),
            (config, value) -> volley(config, it -> it.withDamageShare(value))),
        new FloatOption<>(
            ConfigSettings.COMBAT_VOLLEY_SPREAD_DEGREES,
            VolleyArrowConfig.SPREAD_DEGREES_MIN,
            VolleyArrowConfig.SPREAD_DEGREES_MAX,
            ANGLE_STEP,
            config -> config.combat().volley().spreadDegrees(),
            (config, value) -> volley(config, it -> it.withSpreadDegrees(value))),
        new IntOption<>(
            ConfigSettings.COMBAT_VOLLEY_SPLIT_DELAY_TICKS,
            VolleyArrowConfig.SPLIT_DELAY_TICKS_MIN,
            VolleyArrowConfig.SPLIT_DELAY_TICKS_MAX,
            config -> config.combat().volley().splitDelayTicks(),
            (config, value) -> volley(config, it -> it.withSplitDelayTicks(value))),
        new FloatOption<>(
            ConfigSettings.COMBAT_RAILGUN_SPEED_MULTIPLIER,
            RailgunArrowConfig.SPEED_MULTIPLIER_MIN,
            RailgunArrowConfig.SPEED_MULTIPLIER_MAX,
            SHARE_STEP,
            config -> config.combat().railgun().speedMultiplier(),
            (config, value) -> railgun(config, it -> it.withSpeedMultiplier(value))),
        new FloatOption<>(
            ConfigSettings.COMBAT_RAILGUN_GRAVITY_FACTOR,
            RailgunArrowConfig.GRAVITY_FACTOR_MIN,
            RailgunArrowConfig.GRAVITY_FACTOR_MAX,
            SHARE_STEP,
            config -> config.combat().railgun().gravityFactor(),
            (config, value) -> railgun(config, it -> it.withGravityFactor(value))));
  }

  private static NotEnoughArrowsConfig shock(
      final NotEnoughArrowsConfig config,
      final Function<ShockArrowConfig, ShockArrowConfig> change) {
    return combat(config, it -> it.withShock(change.apply(it.shock())));
  }

  private static NotEnoughArrowsConfig lifesteal(
      final NotEnoughArrowsConfig config,
      final Function<LifestealArrowConfig, LifestealArrowConfig> change) {
    return combat(config, it -> it.withLifesteal(change.apply(it.lifesteal())));
  }

  private static NotEnoughArrowsConfig status(
      final NotEnoughArrowsConfig config,
      final Function<StatusArrowConfig, StatusArrowConfig> change) {
    return combat(config, it -> it.withStatus(change.apply(it.status())));
  }

  private static NotEnoughArrowsConfig homing(
      final NotEnoughArrowsConfig config,
      final Function<HomingArrowConfig, HomingArrowConfig> change) {
    return combat(config, it -> it.withHoming(change.apply(it.homing())));
  }

  private static NotEnoughArrowsConfig volley(
      final NotEnoughArrowsConfig config,
      final Function<VolleyArrowConfig, VolleyArrowConfig> change) {
    return combat(config, it -> it.withVolley(change.apply(it.volley())));
  }

  private static NotEnoughArrowsConfig railgun(
      final NotEnoughArrowsConfig config,
      final Function<RailgunArrowConfig, RailgunArrowConfig> change) {
    return combat(config, it -> it.withRailgun(change.apply(it.railgun())));
  }

  private static NotEnoughArrowsConfig combat(
      final NotEnoughArrowsConfig config,
      final Function<CombatArrowConfig, CombatArrowConfig> change) {
    return config.withCombat(change.apply(config.combat()));
  }
}
