package com.grahambartley.volley.config.option;

import com.grahambartley.volley.config.BeaconArrowConfig;
import com.grahambartley.volley.config.ConfigSettings;
import com.grahambartley.volley.config.DiscoveryArrowConfig;
import com.grahambartley.volley.config.ProspectorArrowConfig;
import com.grahambartley.volley.config.SonarArrowConfig;
import com.grahambartley.volley.config.TorchArrowConfig;
import com.grahambartley.volley.config.TracerArrowConfig;
import com.grahambartley.volley.config.TripwireArrowConfig;
import com.grahambartley.volley.config.VolleyConfig;
import java.util.List;
import java.util.function.Function;

public final class DiscoveryOptions {
  private DiscoveryOptions() {}

  public static final IdentifierListOption<VolleyConfig> PROSPECTOR_BLOCKS =
      new IdentifierListOption<>(
          ConfigSettings.DISCOVERY_PROSPECTOR_BLOCKS,
          ProspectorArrowConfig.BLOCKS_MAX,
          config -> config.discovery().prospector().blocks(),
          (config, value) -> prospector(config, it -> it.withBlocks(value)));

  private static final List<ConfigOption<VolleyConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<VolleyConfig> SECTION =
      new ConfigSection<>(ConfigSettings.DISCOVERY, OPTIONS);

  public static ConfigSection<VolleyConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<VolleyConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<VolleyConfig>> buildOptions() {
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

  private static VolleyConfig torch(
      final VolleyConfig config, final Function<TorchArrowConfig, TorchArrowConfig> change) {
    return discovery(config, it -> it.withTorch(change.apply(it.torch())));
  }

  private static VolleyConfig beacon(
      final VolleyConfig config, final Function<BeaconArrowConfig, BeaconArrowConfig> change) {
    return discovery(config, it -> it.withBeacon(change.apply(it.beacon())));
  }

  private static VolleyConfig prospector(
      final VolleyConfig config,
      final Function<ProspectorArrowConfig, ProspectorArrowConfig> change) {
    return discovery(config, it -> it.withProspector(change.apply(it.prospector())));
  }

  private static VolleyConfig sonar(
      final VolleyConfig config, final Function<SonarArrowConfig, SonarArrowConfig> change) {
    return discovery(config, it -> it.withSonar(change.apply(it.sonar())));
  }

  private static VolleyConfig tripwire(
      final VolleyConfig config, final Function<TripwireArrowConfig, TripwireArrowConfig> change) {
    return discovery(config, it -> it.withTripwire(change.apply(it.tripwire())));
  }

  private static VolleyConfig tracer(
      final VolleyConfig config, final Function<TracerArrowConfig, TracerArrowConfig> change) {
    return discovery(config, it -> it.withTracer(change.apply(it.tracer())));
  }

  private static VolleyConfig discovery(
      final VolleyConfig config,
      final Function<DiscoveryArrowConfig, DiscoveryArrowConfig> change) {
    return config.withDiscovery(change.apply(config.discovery()));
  }
}
