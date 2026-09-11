package com.grahambartley.notenougharrows.config.option;

import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.UtilityArrowConfig;
import java.util.List;
import java.util.function.Function;

public final class UtilityOptions {
  public static final float WIND_STEP = 0.1f;

  private UtilityOptions() {}

  private static final List<ConfigOption<NotEnoughArrowsConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<NotEnoughArrowsConfig> SECTION =
      new ConfigSection<>(ConfigSettings.UTILITY, OPTIONS);

  public static ConfigSection<NotEnoughArrowsConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<NotEnoughArrowsConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<NotEnoughArrowsConfig>> buildOptions() {
    return List.of(
        new IntOption<>(
            ConfigSettings.UTILITY_GLOW_DURATION_TICKS,
            UtilityArrowConfig.GLOW_DURATION_TICKS_MIN,
            UtilityArrowConfig.GLOW_DURATION_TICKS_MAX,
            config -> config.utility().glowDurationTicks(),
            (config, value) -> utility(config, it -> it.withGlowDurationTicks(value))),
        new IntOption<>(
            ConfigSettings.UTILITY_REDSTONE_SIGNAL_DURATION_TICKS,
            UtilityArrowConfig.REDSTONE_SIGNAL_DURATION_TICKS_MIN,
            UtilityArrowConfig.REDSTONE_SIGNAL_DURATION_TICKS_MAX,
            config -> config.utility().redstoneSignalDurationTicks(),
            (config, value) -> utility(config, it -> it.withRedstoneSignalDurationTicks(value))),
        new IntOption<>(
            ConfigSettings.UTILITY_REDSTONE_SIGNAL_STRENGTH,
            UtilityArrowConfig.REDSTONE_SIGNAL_STRENGTH_MIN,
            UtilityArrowConfig.REDSTONE_SIGNAL_STRENGTH_MAX,
            config -> config.utility().redstoneSignalStrength(),
            (config, value) -> utility(config, it -> it.withRedstoneSignalStrength(value))),
        new FloatOption<>(
            ConfigSettings.UTILITY_WIND_BURST_RADIUS,
            UtilityArrowConfig.WIND_BURST_RADIUS_MIN,
            UtilityArrowConfig.WIND_BURST_RADIUS_MAX,
            WIND_STEP,
            config -> config.utility().windBurstRadius(),
            (config, value) -> utility(config, it -> it.withWindBurstRadius(value))),
        new FloatOption<>(
            ConfigSettings.UTILITY_WIND_PUSH_STRENGTH,
            UtilityArrowConfig.WIND_PUSH_STRENGTH_MIN,
            UtilityArrowConfig.WIND_PUSH_STRENGTH_MAX,
            WIND_STEP,
            config -> config.utility().windPushStrength(),
            (config, value) -> utility(config, it -> it.withWindPushStrength(value))));
  }

  private static NotEnoughArrowsConfig utility(
      final NotEnoughArrowsConfig config,
      final Function<UtilityArrowConfig, UtilityArrowConfig> change) {
    return config.withUtility(change.apply(config.utility()));
  }
}
