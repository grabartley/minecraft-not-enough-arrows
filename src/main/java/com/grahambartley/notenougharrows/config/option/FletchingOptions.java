package com.grahambartley.notenougharrows.config.option;

import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.FletchingStationConfig;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import java.util.List;
import java.util.function.Function;

public final class FletchingOptions {
  private FletchingOptions() {}

  private static final List<ConfigOption<NotEnoughArrowsConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<NotEnoughArrowsConfig> SECTION =
      new ConfigSection<>(ConfigSettings.FLETCHING, OPTIONS);

  public static ConfigSection<NotEnoughArrowsConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<NotEnoughArrowsConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<NotEnoughArrowsConfig>> buildOptions() {
    return List.of(
        new BooleanOption<>(
            ConfigSettings.FLETCHING_STATION_ENABLED,
            config -> config.fletching().stationEnabled(),
            (config, value) -> fletching(config, it -> it.withStationEnabled(value))));
  }

  private static NotEnoughArrowsConfig fletching(
      final NotEnoughArrowsConfig config,
      final Function<FletchingStationConfig, FletchingStationConfig> change) {
    return config.withFletching(change.apply(config.fletching()));
  }
}
