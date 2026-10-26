package com.grahambartley.volley.config.option;

import com.grahambartley.volley.config.ConfigSettings;
import com.grahambartley.volley.config.FletchingStationConfig;
import com.grahambartley.volley.config.VolleyConfig;
import java.util.List;
import java.util.function.Function;

public final class FletchingOptions {
  private FletchingOptions() {}

  private static final List<ConfigOption<VolleyConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<VolleyConfig> SECTION =
      new ConfigSection<>(ConfigSettings.FLETCHING, OPTIONS);

  public static ConfigSection<VolleyConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<VolleyConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<VolleyConfig>> buildOptions() {
    return List.of(
        new BooleanOption<>(
            ConfigSettings.FLETCHING_STATION_ENABLED,
            config -> config.fletching().stationEnabled(),
            (config, value) -> fletching(config, it -> it.withStationEnabled(value))));
  }

  private static VolleyConfig fletching(
      final VolleyConfig config,
      final Function<FletchingStationConfig, FletchingStationConfig> change) {
    return config.withFletching(change.apply(config.fletching()));
  }
}
