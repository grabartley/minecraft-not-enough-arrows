package com.grahambartley.notenougharrows.config.option;

import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.EnderArrowConfig;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import java.util.List;
import java.util.function.Function;

public final class EnderOptions {

  private EnderOptions() {}

  private static final List<ConfigOption<NotEnoughArrowsConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<NotEnoughArrowsConfig> SECTION =
      new ConfigSection<>(ConfigSettings.ENDER, OPTIONS);

  public static ConfigSection<NotEnoughArrowsConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<NotEnoughArrowsConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<NotEnoughArrowsConfig>> buildOptions() {
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

  private static NotEnoughArrowsConfig ender(
      final NotEnoughArrowsConfig config,
      final Function<EnderArrowConfig, EnderArrowConfig> change) {
    return config.withEnder(change.apply(config.ender()));
  }
}
