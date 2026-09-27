package com.grahambartley.notenougharrows.config.option;

import com.grahambartley.notenougharrows.config.ChaosArrowConfig;
import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import java.util.List;
import java.util.function.Function;

public final class ChaosOptions {
  private ChaosOptions() {}

  private static final List<ConfigOption<NotEnoughArrowsConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<NotEnoughArrowsConfig> SECTION =
      new ConfigSection<>(ConfigSettings.CHAOS, OPTIONS);

  public static ConfigSection<NotEnoughArrowsConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<NotEnoughArrowsConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<NotEnoughArrowsConfig>> buildOptions() {
    return List.of(
        new BooleanOption<>(
            ConfigSettings.CHAOS_PARTY_ENABLED,
            config -> config.chaos().partyEnabled(),
            (config, value) -> chaos(config, it -> it.withPartyEnabled(value))),
        new BooleanOption<>(
            ConfigSettings.CHAOS_CHICKEN_ENABLED,
            config -> config.chaos().chickenEnabled(),
            (config, value) -> chaos(config, it -> it.withChickenEnabled(value))),
        new BooleanOption<>(
            ConfigSettings.CHAOS_PUFFER_ENABLED,
            config -> config.chaos().pufferEnabled(),
            (config, value) -> chaos(config, it -> it.withPufferEnabled(value))),
        new IntOption<>(
            ConfigSettings.CHAOS_PUFFER_DURATION_TICKS,
            ChaosArrowConfig.PUFFER_DURATION_TICKS_MIN,
            ChaosArrowConfig.PUFFER_DURATION_TICKS_MAX,
            config -> config.chaos().pufferDurationTicks(),
            (config, value) -> chaos(config, it -> it.withPufferDurationTicks(value))),
        new BooleanOption<>(
            ConfigSettings.CHAOS_STINK_ENABLED,
            config -> config.chaos().stinkEnabled(),
            (config, value) -> chaos(config, it -> it.withStinkEnabled(value))),
        new IntOption<>(
            ConfigSettings.CHAOS_STINK_CLOUD_LIFETIME_TICKS,
            ChaosArrowConfig.STINK_CLOUD_LIFETIME_TICKS_MIN,
            ChaosArrowConfig.STINK_CLOUD_LIFETIME_TICKS_MAX,
            config -> config.chaos().stinkCloudLifetimeTicks(),
            (config, value) -> chaos(config, it -> it.withStinkCloudLifetimeTicks(value))),
        new BooleanOption<>(
            ConfigSettings.CHAOS_BOOMERANG_ENABLED,
            config -> config.chaos().boomerangEnabled(),
            (config, value) -> chaos(config, it -> it.withBoomerangEnabled(value))),
        new BooleanOption<>(
            ConfigSettings.CHAOS_POLYMORPH_ENABLED,
            config -> config.chaos().polymorphEnabled(),
            (config, value) -> chaos(config, it -> it.withPolymorphEnabled(value))),
        new IntOption<>(
            ConfigSettings.CHAOS_POLYMORPH_DURATION_TICKS,
            ChaosArrowConfig.POLYMORPH_DURATION_TICKS_MIN,
            ChaosArrowConfig.POLYMORPH_DURATION_TICKS_MAX,
            config -> config.chaos().polymorphDurationTicks(),
            (config, value) -> chaos(config, it -> it.withPolymorphDurationTicks(value))));
  }

  private static NotEnoughArrowsConfig chaos(
      final NotEnoughArrowsConfig config,
      final Function<ChaosArrowConfig, ChaosArrowConfig> change) {
    return config.withChaos(change.apply(config.chaos()));
  }
}
