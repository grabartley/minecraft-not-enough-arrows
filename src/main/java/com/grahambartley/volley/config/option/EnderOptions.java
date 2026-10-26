package com.grahambartley.volley.config.option;

import com.grahambartley.volley.config.ConfigSettings;
import com.grahambartley.volley.config.EnderArrowConfig;
import com.grahambartley.volley.config.VolleyConfig;
import java.util.List;
import java.util.function.Function;

public final class EnderOptions {

  private EnderOptions() {}

  private static final List<ConfigOption<VolleyConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<VolleyConfig> SECTION =
      new ConfigSection<>(ConfigSettings.ENDER, OPTIONS);

  public static ConfigSection<VolleyConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<VolleyConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<VolleyConfig>> buildOptions() {
    return List.of(
        new IntOption<>(
            ConfigSettings.ENDER_PEARL_MAX_RANGE_BLOCKS,
            EnderArrowConfig.PEARL_MAX_RANGE_BLOCKS_MIN,
            EnderArrowConfig.PEARL_MAX_RANGE_BLOCKS_MAX,
            config -> config.ender().pearlMaxRangeBlocks(),
            (config, value) -> ender(config, it -> it.withPearlMaxRangeBlocks(value))),
        new IntOption<>(
            ConfigSettings.ENDER_RECALL_MAX_RANGE_BLOCKS,
            EnderArrowConfig.RECALL_MAX_RANGE_BLOCKS_MIN,
            EnderArrowConfig.RECALL_MAX_RANGE_BLOCKS_MAX,
            config -> config.ender().recallMaxRangeBlocks(),
            (config, value) -> ender(config, it -> it.withRecallMaxRangeBlocks(value))),
        new BooleanOption<>(
            ConfigSettings.ENDER_RECALL_AFFECTS_PLAYERS,
            config -> config.ender().recallAffectsPlayers(),
            (config, value) -> ender(config, it -> it.withRecallAffectsPlayers(value))));
  }

  private static VolleyConfig ender(
      final VolleyConfig config, final Function<EnderArrowConfig, EnderArrowConfig> change) {
    return config.withEnder(change.apply(config.ender()));
  }
}
