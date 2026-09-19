package com.grahambartley.notenougharrows.config.option;

import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.ControlArrowConfig;
import com.grahambartley.notenougharrows.config.DisarmArrowConfig;
import com.grahambartley.notenougharrows.config.FrostArrowConfig;
import com.grahambartley.notenougharrows.config.LevitationArrowConfig;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.SmokeArrowConfig;
import com.grahambartley.notenougharrows.config.TargetingArrowConfig;
import java.util.List;
import java.util.function.Function;

public final class ControlOptions {
  public static final float DISTANCE_STEP = 0.5f;

  private ControlOptions() {}

  private static final List<ConfigOption<NotEnoughArrowsConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<NotEnoughArrowsConfig> SECTION =
      new ConfigSection<>(ConfigSettings.CONTROL, OPTIONS);

  public static ConfigSection<NotEnoughArrowsConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<NotEnoughArrowsConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<NotEnoughArrowsConfig>> buildOptions() {
    return List.of(
        new IntOption<>(
            ConfigSettings.CONTROL_FROST_FREEZE_TICKS_PER_HIT,
            FrostArrowConfig.FREEZE_TICKS_PER_HIT_MIN,
            FrostArrowConfig.FREEZE_TICKS_PER_HIT_MAX,
            config -> config.control().frost().freezeTicksPerHit(),
            (config, value) -> frost(config, it -> it.withFreezeTicksPerHit(value))),
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
            DISTANCE_STEP,
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
            DISTANCE_STEP,
            config -> config.control().targeting().repelRadius(),
            (config, value) -> targeting(config, it -> it.withRepelRadius(value))),
        new IntOption<>(
            ConfigSettings.CONTROL_TARGETING_REPEL_DURATION_TICKS,
            TargetingArrowConfig.DURATION_TICKS_MIN,
            TargetingArrowConfig.DURATION_TICKS_MAX,
            config -> config.control().targeting().repelDurationTicks(),
            (config, value) -> targeting(config, it -> it.withRepelDurationTicks(value))),
        new IntOption<>(
            ConfigSettings.CONTROL_TARGETING_DAZE_DURATION_TICKS,
            TargetingArrowConfig.DURATION_TICKS_MIN,
            TargetingArrowConfig.DURATION_TICKS_MAX,
            config -> config.control().targeting().dazeDurationTicks(),
            (config, value) -> targeting(config, it -> it.withDazeDurationTicks(value))),
        new FloatOption<>(
            ConfigSettings.CONTROL_SMOKE_RADIUS,
            SmokeArrowConfig.RADIUS_MIN,
            SmokeArrowConfig.RADIUS_MAX,
            DISTANCE_STEP,
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
            (config, value) -> disarm(config, it -> it.withAffectsPlayers(value))));
  }

  private static NotEnoughArrowsConfig frost(
      final NotEnoughArrowsConfig config,
      final Function<FrostArrowConfig, FrostArrowConfig> change) {
    return control(config, it -> it.withFrost(change.apply(it.frost())));
  }

  private static NotEnoughArrowsConfig levitation(
      final NotEnoughArrowsConfig config,
      final Function<LevitationArrowConfig, LevitationArrowConfig> change) {
    return control(config, it -> it.withLevitation(change.apply(it.levitation())));
  }

  private static NotEnoughArrowsConfig targeting(
      final NotEnoughArrowsConfig config,
      final Function<TargetingArrowConfig, TargetingArrowConfig> change) {
    return control(config, it -> it.withTargeting(change.apply(it.targeting())));
  }

  private static NotEnoughArrowsConfig smoke(
      final NotEnoughArrowsConfig config,
      final Function<SmokeArrowConfig, SmokeArrowConfig> change) {
    return control(config, it -> it.withSmoke(change.apply(it.smoke())));
  }

  private static NotEnoughArrowsConfig disarm(
      final NotEnoughArrowsConfig config,
      final Function<DisarmArrowConfig, DisarmArrowConfig> change) {
    return control(config, it -> it.withDisarm(change.apply(it.disarm())));
  }

  private static NotEnoughArrowsConfig control(
      final NotEnoughArrowsConfig config,
      final Function<ControlArrowConfig, ControlArrowConfig> change) {
    return config.withControl(change.apply(config.control()));
  }
}
