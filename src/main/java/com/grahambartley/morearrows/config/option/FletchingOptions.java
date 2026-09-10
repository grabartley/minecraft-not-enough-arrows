package com.grahambartley.morearrows.config.option;

import com.grahambartley.morearrows.config.ConfigSettings;
import com.grahambartley.morearrows.config.FletchingStationConfig;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import java.util.List;
import java.util.function.Function;

public final class FletchingOptions {
  private FletchingOptions() {}

  private static final List<ConfigOption<MoreArrowsConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<MoreArrowsConfig> SECTION =
      new ConfigSection<>(ConfigSettings.FLETCHING, OPTIONS);

  public static ConfigSection<MoreArrowsConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<MoreArrowsConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<MoreArrowsConfig>> buildOptions() {
    return List.of(
        new BooleanOption<>(
            ConfigSettings.FLETCHING_STATION_ENABLED,
            config -> config.fletching().stationEnabled(),
            (config, value) -> fletching(config, it -> it.withStationEnabled(value))));
  }

  private static MoreArrowsConfig fletching(
      final MoreArrowsConfig config,
      final Function<FletchingStationConfig, FletchingStationConfig> change) {
    return config.withFletching(change.apply(config.fletching()));
  }
}
