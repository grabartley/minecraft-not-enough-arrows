package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record DiscoveryArrowConfig(
    TorchArrowConfig torch,
    BeaconArrowConfig beacon,
    ProspectorArrowConfig prospector,
    SonarArrowConfig sonar,
    TripwireArrowConfig tripwire,
    TracerArrowConfig tracer) {

  static final String KEY_TORCH = "torch";
  static final String KEY_BEACON = "beacon";
  static final String KEY_PROSPECTOR = "prospector";
  static final String KEY_SONAR = "sonar";
  static final String KEY_TRIPWIRE = "tripwire";
  static final String KEY_TRACER = "tracer";

  public DiscoveryArrowConfig {
    torch = torch == null ? TorchArrowConfig.defaults() : torch;
    beacon = beacon == null ? BeaconArrowConfig.defaults() : beacon;
    prospector = prospector == null ? ProspectorArrowConfig.defaults() : prospector;
    sonar = sonar == null ? SonarArrowConfig.defaults() : sonar;
    tripwire = tripwire == null ? TripwireArrowConfig.defaults() : tripwire;
    tracer = tracer == null ? TracerArrowConfig.defaults() : tracer;
  }

  public static DiscoveryArrowConfig defaults() {
    return new DiscoveryArrowConfig(
        TorchArrowConfig.defaults(),
        BeaconArrowConfig.defaults(),
        ProspectorArrowConfig.defaults(),
        SonarArrowConfig.defaults(),
        TripwireArrowConfig.defaults(),
        TracerArrowConfig.defaults());
  }

  public DiscoveryArrowConfig withTorch(final TorchArrowConfig value) {
    return new DiscoveryArrowConfig(value, beacon, prospector, sonar, tripwire, tracer);
  }

  public DiscoveryArrowConfig withBeacon(final BeaconArrowConfig value) {
    return new DiscoveryArrowConfig(torch, value, prospector, sonar, tripwire, tracer);
  }

  public DiscoveryArrowConfig withProspector(final ProspectorArrowConfig value) {
    return new DiscoveryArrowConfig(torch, beacon, value, sonar, tripwire, tracer);
  }

  public DiscoveryArrowConfig withSonar(final SonarArrowConfig value) {
    return new DiscoveryArrowConfig(torch, beacon, prospector, value, tripwire, tracer);
  }

  public DiscoveryArrowConfig withTripwire(final TripwireArrowConfig value) {
    return new DiscoveryArrowConfig(torch, beacon, prospector, sonar, value, tracer);
  }

  public DiscoveryArrowConfig withTracer(final TracerArrowConfig value) {
    return new DiscoveryArrowConfig(torch, beacon, prospector, sonar, tripwire, value);
  }

  public static DiscoveryArrowConfig fromJson(final JsonObject root) {
    return new DiscoveryArrowConfig(
        TorchArrowConfig.fromJson(ConfigValues.readObject(root, KEY_TORCH)),
        BeaconArrowConfig.fromJson(ConfigValues.readObject(root, KEY_BEACON)),
        ProspectorArrowConfig.fromJson(ConfigValues.readObject(root, KEY_PROSPECTOR)),
        SonarArrowConfig.fromJson(ConfigValues.readObject(root, KEY_SONAR)),
        TripwireArrowConfig.fromJson(ConfigValues.readObject(root, KEY_TRIPWIRE)),
        TracerArrowConfig.fromJson(ConfigValues.readObject(root, KEY_TRACER)));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.add(KEY_TORCH, torch.toJson());
    root.add(KEY_BEACON, beacon.toJson());
    root.add(KEY_PROSPECTOR, prospector.toJson());
    root.add(KEY_SONAR, sonar.toJson());
    root.add(KEY_TRIPWIRE, tripwire.toJson());
    root.add(KEY_TRACER, tracer.toJson());
    return root;
  }
}
