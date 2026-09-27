package com.grahambartley.notenougharrows.config.option;

import com.grahambartley.notenougharrows.config.BeaconArrowConfig;
import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.DiscoveryArrowConfig;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.ProspectorArrowConfig;
import com.grahambartley.notenougharrows.config.SonarArrowConfig;
import com.grahambartley.notenougharrows.config.TorchArrowConfig;
import com.grahambartley.notenougharrows.config.TracerArrowConfig;
import com.grahambartley.notenougharrows.config.TripwireArrowConfig;
import java.util.List;
import java.util.function.Function;

public final class DiscoveryOptions {
  private DiscoveryOptions() {}

  public static final IdentifierListOption<NotEnoughArrowsConfig> PROSPECTOR_BLOCKS =
      new IdentifierListOption<>(
          ConfigSettings.DISCOVERY_PROSPECTOR_BLOCKS,
          ProspectorArrowConfig.BLOCKS_MAX,
          config -> config.discovery().prospector().blocks(),
          (config, value) -> prospector(config, it -> it.withBlocks(value)));

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
            config -> config.discovery().torch().enabled(),
            (config, value) -> torch(config, it -> it.withEnabled(value))),
        new IntOption<>(
            ConfigSettings.DISCOVERY_BEACON_LIFETIME_TICKS,
            BeaconArrowConfig.LIFETIME_TICKS_MIN,
            BeaconArrowConfig.LIFETIME_TICKS_MAX,
            config -> config.discovery().beacon().lifetimeTicks(),
            (config, value) -> beacon(config, it -> it.withLifetimeTicks(value))),
        new IntOption<>(
            ConfigSettings.DISCOVERY_PROSPECTOR_RADIUS,
            ProspectorArrowConfig.RADIUS_MIN,
            ProspectorArrowConfig.RADIUS_MAX,
            config -> config.discovery().prospector().radius(),
            (config, value) -> prospector(config, it -> it.withRadius(value))),
        new IntOption<>(
            ConfigSettings.DISCOVERY_PROSPECTOR_DURATION_TICKS,
            ProspectorArrowConfig.DURATION_TICKS_MIN,
            ProspectorArrowConfig.DURATION_TICKS_MAX,
            config -> config.discovery().prospector().durationTicks(),
            (config, value) -> prospector(config, it -> it.withDurationTicks(value))),
        PROSPECTOR_BLOCKS,
        new IntOption<>(
            ConfigSettings.DISCOVERY_SONAR_RADIUS,
            SonarArrowConfig.RADIUS_MIN,
            SonarArrowConfig.RADIUS_MAX,
            config -> config.discovery().sonar().radius(),
            (config, value) -> sonar(config, it -> it.withRadius(value))),
        new IntOption<>(
            ConfigSettings.DISCOVERY_SONAR_DURATION_TICKS,
            SonarArrowConfig.DURATION_TICKS_MIN,
            SonarArrowConfig.DURATION_TICKS_MAX,
            config -> config.discovery().sonar().durationTicks(),
            (config, value) -> sonar(config, it -> it.withDurationTicks(value))),
        new IntOption<>(
            ConfigSettings.DISCOVERY_TRIPWIRE_LIFETIME_TICKS,
            TripwireArrowConfig.LIFETIME_TICKS_MIN,
            TripwireArrowConfig.LIFETIME_TICKS_MAX,
            config -> config.discovery().tripwire().lifetimeTicks(),
            (config, value) -> tripwire(config, it -> it.withLifetimeTicks(value))),
        new IntOption<>(
            ConfigSettings.DISCOVERY_TRIPWIRE_REPORT_INTERVAL_TICKS,
            TripwireArrowConfig.REPORT_INTERVAL_TICKS_MIN,
            TripwireArrowConfig.REPORT_INTERVAL_TICKS_MAX,
            config -> config.discovery().tripwire().reportIntervalTicks(),
            (config, value) -> tripwire(config, it -> it.withReportIntervalTicks(value))),
        new IntOption<>(
            ConfigSettings.DISCOVERY_TRACER_PATH_LIFETIME_TICKS,
            TracerArrowConfig.PATH_LIFETIME_TICKS_MIN,
            TracerArrowConfig.PATH_LIFETIME_TICKS_MAX,
            config -> config.discovery().tracer().pathLifetimeTicks(),
            (config, value) -> tracer(config, it -> it.withPathLifetimeTicks(value))));
  }

  private static NotEnoughArrowsConfig torch(
      final NotEnoughArrowsConfig config,
      final Function<TorchArrowConfig, TorchArrowConfig> change) {
    return discovery(config, it -> it.withTorch(change.apply(it.torch())));
  }

  private static NotEnoughArrowsConfig beacon(
      final NotEnoughArrowsConfig config,
      final Function<BeaconArrowConfig, BeaconArrowConfig> change) {
    return discovery(config, it -> it.withBeacon(change.apply(it.beacon())));
  }

  private static NotEnoughArrowsConfig prospector(
      final NotEnoughArrowsConfig config,
      final Function<ProspectorArrowConfig, ProspectorArrowConfig> change) {
    return discovery(config, it -> it.withProspector(change.apply(it.prospector())));
  }

  private static NotEnoughArrowsConfig sonar(
      final NotEnoughArrowsConfig config,
      final Function<SonarArrowConfig, SonarArrowConfig> change) {
    return discovery(config, it -> it.withSonar(change.apply(it.sonar())));
  }

  private static NotEnoughArrowsConfig tripwire(
      final NotEnoughArrowsConfig config,
      final Function<TripwireArrowConfig, TripwireArrowConfig> change) {
    return discovery(config, it -> it.withTripwire(change.apply(it.tripwire())));
  }

  private static NotEnoughArrowsConfig tracer(
      final NotEnoughArrowsConfig config,
      final Function<TracerArrowConfig, TracerArrowConfig> change) {
    return discovery(config, it -> it.withTracer(change.apply(it.tracer())));
  }

  private static NotEnoughArrowsConfig discovery(
      final NotEnoughArrowsConfig config,
      final Function<DiscoveryArrowConfig, DiscoveryArrowConfig> change) {
    return config.withDiscovery(change.apply(config.discovery()));
  }
}
