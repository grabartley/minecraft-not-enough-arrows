package com.grahambartley.volley.config.option;

import com.grahambartley.volley.config.AgricultureArrowConfig;
import com.grahambartley.volley.config.BeeArrowConfig;
import com.grahambartley.volley.config.BlossomArrowConfig;
import com.grahambartley.volley.config.ConfigSettings;
import com.grahambartley.volley.config.HarvestArrowConfig;
import com.grahambartley.volley.config.TillArrowConfig;
import com.grahambartley.volley.config.VolleyConfig;
import java.util.List;
import java.util.function.Function;

public final class AgricultureOptions {
  private AgricultureOptions() {}

  private static final List<ConfigOption<VolleyConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<VolleyConfig> SECTION =
      new ConfigSection<>(ConfigSettings.AGRICULTURE, OPTIONS);

  public static ConfigSection<VolleyConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<VolleyConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<VolleyConfig>> buildOptions() {
    return List.of(
        new IntOption<>(
            ConfigSettings.AGRICULTURE_BLOSSOM_RADIUS,
            BlossomArrowConfig.RADIUS_MIN,
            BlossomArrowConfig.RADIUS_MAX,
            config -> config.agriculture().blossom().radius(),
            (config, value) -> blossom(config, it -> it.withRadius(value))),
        new IntOption<>(
            ConfigSettings.AGRICULTURE_TILL_RADIUS,
            TillArrowConfig.RADIUS_MIN,
            TillArrowConfig.RADIUS_MAX,
            config -> config.agriculture().till().radius(),
            (config, value) -> till(config, it -> it.withRadius(value))),
        new IntOption<>(
            ConfigSettings.AGRICULTURE_HARVEST_RADIUS,
            HarvestArrowConfig.RADIUS_MIN,
            HarvestArrowConfig.RADIUS_MAX,
            config -> config.agriculture().harvest().radius(),
            (config, value) -> harvest(config, it -> it.withRadius(value))),
        new IntOption<>(
            ConfigSettings.AGRICULTURE_BEE_COUNT,
            BeeArrowConfig.COUNT_MIN,
            BeeArrowConfig.COUNT_MAX,
            config -> config.agriculture().bee().count(),
            (config, value) -> bee(config, it -> it.withCount(value))),
        new IntOption<>(
            ConfigSettings.AGRICULTURE_BEE_LIFETIME_TICKS,
            BeeArrowConfig.LIFETIME_TICKS_MIN,
            BeeArrowConfig.LIFETIME_TICKS_MAX,
            config -> config.agriculture().bee().lifetimeTicks(),
            (config, value) -> bee(config, it -> it.withLifetimeTicks(value))));
  }

  private static VolleyConfig blossom(
      final VolleyConfig config, final Function<BlossomArrowConfig, BlossomArrowConfig> change) {
    return agriculture(config, it -> it.withBlossom(change.apply(it.blossom())));
  }

  private static VolleyConfig till(
      final VolleyConfig config, final Function<TillArrowConfig, TillArrowConfig> change) {
    return agriculture(config, it -> it.withTill(change.apply(it.till())));
  }

  private static VolleyConfig harvest(
      final VolleyConfig config, final Function<HarvestArrowConfig, HarvestArrowConfig> change) {
    return agriculture(config, it -> it.withHarvest(change.apply(it.harvest())));
  }

  private static VolleyConfig bee(
      final VolleyConfig config, final Function<BeeArrowConfig, BeeArrowConfig> change) {
    return agriculture(config, it -> it.withBee(change.apply(it.bee())));
  }

  private static VolleyConfig agriculture(
      final VolleyConfig config,
      final Function<AgricultureArrowConfig, AgricultureArrowConfig> change) {
    return config.withAgriculture(change.apply(config.agriculture()));
  }
}
