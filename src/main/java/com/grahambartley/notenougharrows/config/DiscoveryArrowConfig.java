package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;
import java.util.List;

public record DiscoveryArrowConfig(
    boolean torchEnabled,
    int beaconLifetimeTicks,
    int revealRadius,
    int revealDurationTicks,
    List<String> prospectorBlocks,
    int watcherLifetimeTicks,
    int watcherReportIntervalTicks,
    int tracerPathLifetimeTicks) {

  public static final int BEACON_LIFETIME_TICKS_MIN = 0;
  public static final int BEACON_LIFETIME_TICKS_MAX = 12000;
  public static final int REVEAL_RADIUS_MIN = 0;
  public static final int REVEAL_RADIUS_MAX = 32;
  public static final int REVEAL_DURATION_TICKS_MIN = 0;
  public static final int REVEAL_DURATION_TICKS_MAX = 1200;
  public static final int PROSPECTOR_BLOCKS_MAX = 64;
  public static final int WATCHER_LIFETIME_TICKS_MIN = 20;
  public static final int WATCHER_LIFETIME_TICKS_MAX = 24000;
  public static final int WATCHER_REPORT_INTERVAL_TICKS_MIN = 20;
  public static final int WATCHER_REPORT_INTERVAL_TICKS_MAX = 1200;
  public static final int TRACER_PATH_LIFETIME_TICKS_MIN = 0;
  public static final int TRACER_PATH_LIFETIME_TICKS_MAX = 1200;

  public static final boolean DEFAULT_TORCH_ENABLED = true;
  public static final int DEFAULT_BEACON_LIFETIME_TICKS = 1200;
  public static final int DEFAULT_REVEAL_RADIUS = 12;
  public static final int DEFAULT_REVEAL_DURATION_TICKS = 200;
  public static final List<String> DEFAULT_PROSPECTOR_BLOCKS =
      List.of(
          "minecraft:coal_ore",
          "minecraft:deepslate_coal_ore",
          "minecraft:copper_ore",
          "minecraft:deepslate_copper_ore",
          "minecraft:iron_ore",
          "minecraft:deepslate_iron_ore",
          "minecraft:gold_ore",
          "minecraft:deepslate_gold_ore",
          "minecraft:redstone_ore",
          "minecraft:deepslate_redstone_ore",
          "minecraft:lapis_ore",
          "minecraft:deepslate_lapis_ore",
          "minecraft:diamond_ore",
          "minecraft:deepslate_diamond_ore",
          "minecraft:emerald_ore",
          "minecraft:deepslate_emerald_ore",
          "minecraft:nether_gold_ore",
          "minecraft:nether_quartz_ore",
          "minecraft:ancient_debris");
  public static final int DEFAULT_WATCHER_LIFETIME_TICKS = 6000;
  public static final int DEFAULT_WATCHER_REPORT_INTERVAL_TICKS = 40;
  public static final int DEFAULT_TRACER_PATH_LIFETIME_TICKS = 200;

  static final String KEY_TORCH_ENABLED = "torchEnabled";
  static final String KEY_BEACON_LIFETIME_TICKS = "beaconLifetimeTicks";
  static final String KEY_REVEAL_RADIUS = "revealRadius";
  static final String KEY_REVEAL_DURATION_TICKS = "revealDurationTicks";
  static final String KEY_PROSPECTOR_BLOCKS = "prospectorBlocks";
  static final String KEY_WATCHER_LIFETIME_TICKS = "watcherLifetimeTicks";
  static final String KEY_WATCHER_REPORT_INTERVAL_TICKS = "watcherReportIntervalTicks";
  static final String KEY_TRACER_PATH_LIFETIME_TICKS = "tracerPathLifetimeTicks";

  public DiscoveryArrowConfig {
    beaconLifetimeTicks =
        ConfigValues.clampInt(
            beaconLifetimeTicks, BEACON_LIFETIME_TICKS_MIN, BEACON_LIFETIME_TICKS_MAX);
    revealRadius = ConfigValues.clampInt(revealRadius, REVEAL_RADIUS_MIN, REVEAL_RADIUS_MAX);
    revealDurationTicks =
        ConfigValues.clampInt(
            revealDurationTicks, REVEAL_DURATION_TICKS_MIN, REVEAL_DURATION_TICKS_MAX);
    prospectorBlocks = ConfigValues.normalizeIdentifiers(prospectorBlocks, PROSPECTOR_BLOCKS_MAX);
    watcherLifetimeTicks =
        ConfigValues.clampInt(
            watcherLifetimeTicks, WATCHER_LIFETIME_TICKS_MIN, WATCHER_LIFETIME_TICKS_MAX);
    watcherReportIntervalTicks =
        ConfigValues.clampInt(
            watcherReportIntervalTicks,
            WATCHER_REPORT_INTERVAL_TICKS_MIN,
            WATCHER_REPORT_INTERVAL_TICKS_MAX);
    tracerPathLifetimeTicks =
        ConfigValues.clampInt(
            tracerPathLifetimeTicks,
            TRACER_PATH_LIFETIME_TICKS_MIN,
            TRACER_PATH_LIFETIME_TICKS_MAX);
  }

  public static DiscoveryArrowConfig defaults() {
    return new DiscoveryArrowConfig(
        DEFAULT_TORCH_ENABLED,
        DEFAULT_BEACON_LIFETIME_TICKS,
        DEFAULT_REVEAL_RADIUS,
        DEFAULT_REVEAL_DURATION_TICKS,
        DEFAULT_PROSPECTOR_BLOCKS,
        DEFAULT_WATCHER_LIFETIME_TICKS,
        DEFAULT_WATCHER_REPORT_INTERVAL_TICKS,
        DEFAULT_TRACER_PATH_LIFETIME_TICKS);
  }

  public static DiscoveryArrowConfig fromJson(final JsonObject root) {
    final DiscoveryArrowConfig defaults = defaults();
    return new DiscoveryArrowConfig(
        ConfigValues.readBoolean(root, KEY_TORCH_ENABLED, defaults.torchEnabled()),
        ConfigValues.readInt(
            root,
            KEY_BEACON_LIFETIME_TICKS,
            defaults.beaconLifetimeTicks(),
            BEACON_LIFETIME_TICKS_MIN,
            BEACON_LIFETIME_TICKS_MAX),
        ConfigValues.readInt(
            root, KEY_REVEAL_RADIUS, defaults.revealRadius(), REVEAL_RADIUS_MIN, REVEAL_RADIUS_MAX),
        ConfigValues.readInt(
            root,
            KEY_REVEAL_DURATION_TICKS,
            defaults.revealDurationTicks(),
            REVEAL_DURATION_TICKS_MIN,
            REVEAL_DURATION_TICKS_MAX),
        ConfigValues.readIdentifierList(root, KEY_PROSPECTOR_BLOCKS, defaults.prospectorBlocks()),
        ConfigValues.readInt(
            root,
            KEY_WATCHER_LIFETIME_TICKS,
            defaults.watcherLifetimeTicks(),
            WATCHER_LIFETIME_TICKS_MIN,
            WATCHER_LIFETIME_TICKS_MAX),
        ConfigValues.readInt(
            root,
            KEY_WATCHER_REPORT_INTERVAL_TICKS,
            defaults.watcherReportIntervalTicks(),
            WATCHER_REPORT_INTERVAL_TICKS_MIN,
            WATCHER_REPORT_INTERVAL_TICKS_MAX),
        ConfigValues.readInt(
            root,
            KEY_TRACER_PATH_LIFETIME_TICKS,
            defaults.tracerPathLifetimeTicks(),
            TRACER_PATH_LIFETIME_TICKS_MIN,
            TRACER_PATH_LIFETIME_TICKS_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_TORCH_ENABLED, torchEnabled);
    root.addProperty(KEY_BEACON_LIFETIME_TICKS, beaconLifetimeTicks);
    root.addProperty(KEY_REVEAL_RADIUS, revealRadius);
    root.addProperty(KEY_REVEAL_DURATION_TICKS, revealDurationTicks);
    root.add(KEY_PROSPECTOR_BLOCKS, ConfigValues.toJsonArray(prospectorBlocks));
    root.addProperty(KEY_WATCHER_LIFETIME_TICKS, watcherLifetimeTicks);
    root.addProperty(KEY_WATCHER_REPORT_INTERVAL_TICKS, watcherReportIntervalTicks);
    root.addProperty(KEY_TRACER_PATH_LIFETIME_TICKS, tracerPathLifetimeTicks);
    return root;
  }

  public DiscoveryArrowConfig withTorchEnabled(final boolean value) {
    return new DiscoveryArrowConfig(
        value,
        beaconLifetimeTicks,
        revealRadius,
        revealDurationTicks,
        prospectorBlocks,
        watcherLifetimeTicks,
        watcherReportIntervalTicks,
        tracerPathLifetimeTicks);
  }

  public DiscoveryArrowConfig withBeaconLifetimeTicks(final int value) {
    return new DiscoveryArrowConfig(
        torchEnabled,
        value,
        revealRadius,
        revealDurationTicks,
        prospectorBlocks,
        watcherLifetimeTicks,
        watcherReportIntervalTicks,
        tracerPathLifetimeTicks);
  }

  public DiscoveryArrowConfig withRevealRadius(final int value) {
    return new DiscoveryArrowConfig(
        torchEnabled,
        beaconLifetimeTicks,
        value,
        revealDurationTicks,
        prospectorBlocks,
        watcherLifetimeTicks,
        watcherReportIntervalTicks,
        tracerPathLifetimeTicks);
  }

  public DiscoveryArrowConfig withRevealDurationTicks(final int value) {
    return new DiscoveryArrowConfig(
        torchEnabled,
        beaconLifetimeTicks,
        revealRadius,
        value,
        prospectorBlocks,
        watcherLifetimeTicks,
        watcherReportIntervalTicks,
        tracerPathLifetimeTicks);
  }

  public DiscoveryArrowConfig withProspectorBlocks(final List<String> value) {
    return new DiscoveryArrowConfig(
        torchEnabled,
        beaconLifetimeTicks,
        revealRadius,
        revealDurationTicks,
        value,
        watcherLifetimeTicks,
        watcherReportIntervalTicks,
        tracerPathLifetimeTicks);
  }

  public DiscoveryArrowConfig withWatcherLifetimeTicks(final int value) {
    return new DiscoveryArrowConfig(
        torchEnabled,
        beaconLifetimeTicks,
        revealRadius,
        revealDurationTicks,
        prospectorBlocks,
        value,
        watcherReportIntervalTicks,
        tracerPathLifetimeTicks);
  }

  public DiscoveryArrowConfig withWatcherReportIntervalTicks(final int value) {
    return new DiscoveryArrowConfig(
        torchEnabled,
        beaconLifetimeTicks,
        revealRadius,
        revealDurationTicks,
        prospectorBlocks,
        watcherLifetimeTicks,
        value,
        tracerPathLifetimeTicks);
  }

  public DiscoveryArrowConfig withTracerPathLifetimeTicks(final int value) {
    return new DiscoveryArrowConfig(
        torchEnabled,
        beaconLifetimeTicks,
        revealRadius,
        revealDurationTicks,
        prospectorBlocks,
        watcherLifetimeTicks,
        watcherReportIntervalTicks,
        value);
  }
}
