package com.grahambartley.notenougharrows.config.option;

import com.grahambartley.notenougharrows.config.AgricultureArrowConfig;
import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import java.util.List;
import java.util.function.Function;

public final class AgricultureOptions {
  private AgricultureOptions() {}

  private static final List<ConfigOption<NotEnoughArrowsConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<NotEnoughArrowsConfig> SECTION =
      new ConfigSection<>(ConfigSettings.AGRICULTURE, OPTIONS);

  public static ConfigSection<NotEnoughArrowsConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<NotEnoughArrowsConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<NotEnoughArrowsConfig>> buildOptions() {
    return List.of(
        new IntOption<>(
            ConfigSettings.AGRICULTURE_BLOSSOM_RADIUS,
            AgricultureArrowConfig.BLOSSOM_RADIUS_MIN,
            AgricultureArrowConfig.BLOSSOM_RADIUS_MAX,
            config -> config.agriculture().blossomRadius(),
            (config, value) -> agriculture(config, it -> it.withBlossomRadius(value))),
        new IntOption<>(
            ConfigSettings.AGRICULTURE_TILL_RADIUS,
            AgricultureArrowConfig.TILL_RADIUS_MIN,
            AgricultureArrowConfig.TILL_RADIUS_MAX,
            config -> config.agriculture().tillRadius(),
            (config, value) -> agriculture(config, it -> it.withTillRadius(value))),
        new IntOption<>(
            ConfigSettings.AGRICULTURE_HARVEST_RADIUS,
            AgricultureArrowConfig.HARVEST_RADIUS_MIN,
            AgricultureArrowConfig.HARVEST_RADIUS_MAX,
            config -> config.agriculture().harvestRadius(),
            (config, value) -> agriculture(config, it -> it.withHarvestRadius(value))),
        new IntOption<>(
            ConfigSettings.AGRICULTURE_BEE_COUNT,
            AgricultureArrowConfig.BEE_COUNT_MIN,
            AgricultureArrowConfig.BEE_COUNT_MAX,
            config -> config.agriculture().beeCount(),
            (config, value) -> agriculture(config, it -> it.withBeeCount(value))),
        new IntOption<>(
            ConfigSettings.AGRICULTURE_BEE_LIFETIME_TICKS,
            AgricultureArrowConfig.BEE_LIFETIME_TICKS_MIN,
            AgricultureArrowConfig.BEE_LIFETIME_TICKS_MAX,
            config -> config.agriculture().beeLifetimeTicks(),
            (config, value) -> agriculture(config, it -> it.withBeeLifetimeTicks(value))));
  }

  private static NotEnoughArrowsConfig agriculture(
      final NotEnoughArrowsConfig config,
      final Function<AgricultureArrowConfig, AgricultureArrowConfig> change) {
    return config.withAgriculture(change.apply(config.agriculture()));
  }
}
