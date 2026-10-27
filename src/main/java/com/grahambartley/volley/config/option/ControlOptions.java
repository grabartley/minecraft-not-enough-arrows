package com.grahambartley.volley.config.option;

import com.grahambartley.volley.config.AllegianceArrowConfig;
import com.grahambartley.volley.config.ConfigSettings;
import com.grahambartley.volley.config.ControlArrowConfig;
import com.grahambartley.volley.config.DisarmArrowConfig;
import com.grahambartley.volley.config.FrostArrowConfig;
import com.grahambartley.volley.config.LevitationArrowConfig;
import com.grahambartley.volley.config.SmokeArrowConfig;
import com.grahambartley.volley.config.TargetingArrowConfig;
import com.grahambartley.volley.config.VolleyConfig;
import java.util.List;
import java.util.function.Function;

public final class ControlOptions {

  private ControlOptions() {}

  private static final List<ConfigOption<VolleyConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<VolleyConfig> SECTION =
      new ConfigSection<>(ConfigSettings.CONTROL, OPTIONS);

  public static ConfigSection<VolleyConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<VolleyConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<VolleyConfig>> buildOptions() {
    return List.of(
        new IntOption<>(
            ConfigSettings.CONTROL_FROST_DURATION_TICKS,
            FrostArrowConfig.DURATION_TICKS_MIN,
            FrostArrowConfig.DURATION_TICKS_MAX,
            config -> config.control().frost().durationTicks(),
            (config, value) -> frost(config, it -> it.withDurationTicks(value))),
        new IntOption<>(
            ConfigSettings.CONTROL_LEVITATION_DURATION_TICKS,
            LevitationArrowConfig.DURATION_TICKS_MIN,
            LevitationArrowConfig.DURATION_TICKS_MAX,
            config -> config.control().levitation().durationTicks(),
            (config, value) -> levitation(config, it -> it.withDurationTicks(value))),
        new FloatOption<>(
            ConfigSettings.CONTROL_TARGETING_TAUNT_RADIUS,
            TargetingArrowConfig.RADIUS_MIN,
            TargetingArrowConfig.RADIUS_MAX,
            CombatOptions.DISTANCE_STEP,
            config -> config.control().targeting().tauntRadius(),
            (config, value) -> targeting(config, it -> it.withTauntRadius(value))),
        new IntOption<>(
            ConfigSettings.CONTROL_TARGETING_TAUNT_DURATION_TICKS,
            TargetingArrowConfig.DURATION_TICKS_MIN,
            TargetingArrowConfig.DURATION_TICKS_MAX,
            config -> config.control().targeting().tauntDurationTicks(),
            (config, value) -> targeting(config, it -> it.withTauntDurationTicks(value))),
        new FloatOption<>(
            ConfigSettings.CONTROL_TARGETING_REPEL_RADIUS,
            TargetingArrowConfig.RADIUS_MIN,
            TargetingArrowConfig.RADIUS_MAX,
            CombatOptions.DISTANCE_STEP,
            config -> config.control().targeting().repelRadius(),
            (config, value) -> targeting(config, it -> it.withRepelRadius(value))),
        new IntOption<>(
            ConfigSettings.CONTROL_TARGETING_REPEL_DURATION_TICKS,
            TargetingArrowConfig.DURATION_TICKS_MIN,
            TargetingArrowConfig.DURATION_TICKS_MAX,
            config -> config.control().targeting().repelDurationTicks(),
            (config, value) -> targeting(config, it -> it.withRepelDurationTicks(value))),
        new FloatOption<>(
            ConfigSettings.CONTROL_TARGETING_REPEL_DISTANCE,
            TargetingArrowConfig.DISTANCE_MIN,
            TargetingArrowConfig.DISTANCE_MAX,
            CombatOptions.DISTANCE_STEP,
            config -> config.control().targeting().repelDistance(),
            (config, value) -> targeting(config, it -> it.withRepelDistance(value))),
        new IntOption<>(
            ConfigSettings.CONTROL_ALLEGIANCE_DURATION_TICKS,
            AllegianceArrowConfig.DURATION_TICKS_MIN,
            AllegianceArrowConfig.DURATION_TICKS_MAX,
            config -> config.control().allegiance().durationTicks(),
            (config, value) -> allegiance(config, it -> it.withDurationTicks(value))),
        new FloatOption<>(
            ConfigSettings.CONTROL_ALLEGIANCE_DEFEND_RADIUS,
            AllegianceArrowConfig.DEFEND_RADIUS_MIN,
            AllegianceArrowConfig.DEFEND_RADIUS_MAX,
            CombatOptions.DISTANCE_STEP,
            config -> config.control().allegiance().defendRadius(),
            (config, value) -> allegiance(config, it -> it.withDefendRadius(value))),
        new FloatOption<>(
            ConfigSettings.CONTROL_SMOKE_RADIUS,
            SmokeArrowConfig.RADIUS_MIN,
            SmokeArrowConfig.RADIUS_MAX,
            CombatOptions.DISTANCE_STEP,
            config -> config.control().smoke().radius(),
            (config, value) -> smoke(config, it -> it.withRadius(value))),
        new IntOption<>(
            ConfigSettings.CONTROL_SMOKE_DURATION_TICKS,
            SmokeArrowConfig.DURATION_TICKS_MIN,
            SmokeArrowConfig.DURATION_TICKS_MAX,
            config -> config.control().smoke().durationTicks(),
            (config, value) -> smoke(config, it -> it.withDurationTicks(value))),
        new BooleanOption<>(
            ConfigSettings.CONTROL_DISARM_AFFECTS_PLAYERS,
            config -> config.control().disarm().affectsPlayers(),
            (config, value) -> disarm(config, it -> it.withAffectsPlayers(value))),
        new FloatOption<>(
            ConfigSettings.CONTROL_DISARM_THROW_DISTANCE,
            DisarmArrowConfig.THROW_DISTANCE_MIN,
            DisarmArrowConfig.THROW_DISTANCE_MAX,
            CombatOptions.DISTANCE_STEP,
            config -> config.control().disarm().throwDistance(),
            (config, value) -> disarm(config, it -> it.withThrowDistance(value))));
  }

  private static VolleyConfig frost(
      final VolleyConfig config, final Function<FrostArrowConfig, FrostArrowConfig> change) {
    return control(config, it -> it.withFrost(change.apply(it.frost())));
  }

  private static VolleyConfig levitation(
      final VolleyConfig config,
      final Function<LevitationArrowConfig, LevitationArrowConfig> change) {
    return control(config, it -> it.withLevitation(change.apply(it.levitation())));
  }

  private static VolleyConfig targeting(
      final VolleyConfig config,
      final Function<TargetingArrowConfig, TargetingArrowConfig> change) {
    return control(config, it -> it.withTargeting(change.apply(it.targeting())));
  }

  private static VolleyConfig allegiance(
      final VolleyConfig config,
      final Function<AllegianceArrowConfig, AllegianceArrowConfig> change) {
    return control(config, it -> it.withAllegiance(change.apply(it.allegiance())));
  }

  private static VolleyConfig smoke(
      final VolleyConfig config, final Function<SmokeArrowConfig, SmokeArrowConfig> change) {
    return control(config, it -> it.withSmoke(change.apply(it.smoke())));
  }

  private static VolleyConfig disarm(
      final VolleyConfig config, final Function<DisarmArrowConfig, DisarmArrowConfig> change) {
    return control(config, it -> it.withDisarm(change.apply(it.disarm())));
  }

  private static VolleyConfig control(
      final VolleyConfig config, final Function<ControlArrowConfig, ControlArrowConfig> change) {
    return config.withControl(change.apply(config.control()));
  }
}
