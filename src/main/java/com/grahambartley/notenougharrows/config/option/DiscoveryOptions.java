package com.grahambartley.notenougharrows.config.option;

import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.DiscoveryArrowConfig;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import java.util.List;
import java.util.function.Function;

public final class DiscoveryOptions {
  private DiscoveryOptions() {}

  private static final List<ConfigOption<NotEnoughArrowsConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<NotEnoughArrowsConfig> SECTION =
      new ConfigSection<>(ConfigSettings.DISCOVERY, OPTIONS);

  public static ConfigSection<NotEnoughArrowsConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<NotEnoughArrowsConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<NotEnoughArrowsConfig>> buildOptions() {
    return List.of(
        new BooleanOption<>(
            ConfigSettings.DISCOVERY_TORCH_ENABLED,
            config -> config.discovery().torchEnabled(),
            (config, value) -> discovery(config, it -> it.withTorchEnabled(value))),
        new IntOption<>(
            ConfigSettings.DISCOVERY_BEACON_LIFETIME_TICKS,
            DiscoveryArrowConfig.BEACON_LIFETIME_TICKS_MIN,
            DiscoveryArrowConfig.BEACON_LIFETIME_TICKS_MAX,
            config -> config.discovery().beaconLifetimeTicks(),
            (config, value) -> discovery(config, it -> it.withBeaconLifetimeTicks(value))),
        new IntOption<>(
            ConfigSettings.DISCOVERY_REVEAL_RADIUS,
            DiscoveryArrowConfig.REVEAL_RADIUS_MIN,
            DiscoveryArrowConfig.REVEAL_RADIUS_MAX,
            config -> config.discovery().revealRadius(),
            (config, value) -> discovery(config, it -> it.withRevealRadius(value))),
        new IntOption<>(
            ConfigSettings.DISCOVERY_REVEAL_DURATION_TICKS,
            DiscoveryArrowConfig.REVEAL_DURATION_TICKS_MIN,
            DiscoveryArrowConfig.REVEAL_DURATION_TICKS_MAX,
            config -> config.discovery().revealDurationTicks(),
            (config, value) -> discovery(config, it -> it.withRevealDurationTicks(value))),
        new IdentifierListOption<>(
            ConfigSettings.DISCOVERY_PROSPECTOR_BLOCKS,
            DiscoveryArrowConfig.PROSPECTOR_BLOCKS_MAX,
            config -> config.discovery().prospectorBlocks(),
            (config, value) -> discovery(config, it -> it.withProspectorBlocks(value))),
        new IntOption<>(
            ConfigSettings.DISCOVERY_WATCHER_LIFETIME_TICKS,
            DiscoveryArrowConfig.WATCHER_LIFETIME_TICKS_MIN,
            DiscoveryArrowConfig.WATCHER_LIFETIME_TICKS_MAX,
            config -> config.discovery().watcherLifetimeTicks(),
            (config, value) -> discovery(config, it -> it.withWatcherLifetimeTicks(value))),
        new IntOption<>(
            ConfigSettings.DISCOVERY_WATCHER_REPORT_INTERVAL_TICKS,
            DiscoveryArrowConfig.WATCHER_REPORT_INTERVAL_TICKS_MIN,
            DiscoveryArrowConfig.WATCHER_REPORT_INTERVAL_TICKS_MAX,
            config -> config.discovery().watcherReportIntervalTicks(),
            (config, value) -> discovery(config, it -> it.withWatcherReportIntervalTicks(value))),
        new IntOption<>(
            ConfigSettings.DISCOVERY_TRACER_PATH_LIFETIME_TICKS,
            DiscoveryArrowConfig.TRACER_PATH_LIFETIME_TICKS_MIN,
            DiscoveryArrowConfig.TRACER_PATH_LIFETIME_TICKS_MAX,
            config -> config.discovery().tracerPathLifetimeTicks(),
            (config, value) -> discovery(config, it -> it.withTracerPathLifetimeTicks(value))));
  }

  private static NotEnoughArrowsConfig discovery(
      final NotEnoughArrowsConfig config,
      final Function<DiscoveryArrowConfig, DiscoveryArrowConfig> change) {
    return config.withDiscovery(change.apply(config.discovery()));
  }
}
