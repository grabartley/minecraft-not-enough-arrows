package com.grahambartley.notenougharrows.config.option;

import com.grahambartley.notenougharrows.config.BridgeArrowConfig;
import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.ScaffoldArrowConfig;
import com.grahambartley.notenougharrows.config.TowArrowConfig;
import com.grahambartley.notenougharrows.config.TrampolineArrowConfig;
import com.grahambartley.notenougharrows.config.TraversalArrowConfig;
import com.grahambartley.notenougharrows.config.UpdraftArrowConfig;
import com.grahambartley.notenougharrows.config.VineArrowConfig;
import com.grahambartley.notenougharrows.config.ZiplineArrowConfig;
import java.util.List;
import java.util.function.Function;

public final class TraversalOptions {
  public static final float VELOCITY_STEP = 0.1f;

  private TraversalOptions() {}

  private static final List<ConfigOption<NotEnoughArrowsConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<NotEnoughArrowsConfig> SECTION =
      new ConfigSection<>(ConfigSettings.TRAVERSAL, OPTIONS);

  public static ConfigSection<NotEnoughArrowsConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<NotEnoughArrowsConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<NotEnoughArrowsConfig>> buildOptions() {
    return List.of(
        new IntOption<>(
            ConfigSettings.TRAVERSAL_ZIPLINE_MAX_SPAN_BLOCKS,
            ZiplineArrowConfig.MAX_SPAN_BLOCKS_MIN,
            ZiplineArrowConfig.MAX_SPAN_BLOCKS_MAX,
            config -> config.traversal().zipline().maxSpanBlocks(),
            (config, value) -> zipline(config, it -> it.withMaxSpanBlocks(value))),
        new IntOption<>(
            ConfigSettings.TRAVERSAL_ZIPLINE_PENDING_WINDOW_TICKS,
            ZiplineArrowConfig.PENDING_WINDOW_TICKS_MIN,
            ZiplineArrowConfig.PENDING_WINDOW_TICKS_MAX,
            config -> config.traversal().zipline().pendingWindowTicks(),
            (config, value) -> zipline(config, it -> it.withPendingWindowTicks(value))),
        new FloatOption<>(
            ConfigSettings.TRAVERSAL_ZIPLINE_RIDE_SPEED,
            ZiplineArrowConfig.RIDE_SPEED_MIN,
            ZiplineArrowConfig.RIDE_SPEED_MAX,
            VELOCITY_STEP,
            config -> config.traversal().zipline().rideSpeed(),
            (config, value) -> zipline(config, it -> it.withRideSpeed(value))),
        new IntOption<>(
            ConfigSettings.TRAVERSAL_ZIPLINE_LIFETIME_TICKS,
            ZiplineArrowConfig.LIFETIME_TICKS_MIN,
            ZiplineArrowConfig.LIFETIME_TICKS_MAX,
            config -> config.traversal().zipline().lifetimeTicks(),
            (config, value) -> zipline(config, it -> it.withLifetimeTicks(value))),
        new IntOption<>(
            ConfigSettings.TRAVERSAL_TOW_RANGE_BLOCKS,
            TowArrowConfig.RANGE_BLOCKS_MIN,
            TowArrowConfig.RANGE_BLOCKS_MAX,
            config -> config.traversal().tow().rangeBlocks(),
            (config, value) -> tow(config, it -> it.withRangeBlocks(value))),
        new IntOption<>(
            ConfigSettings.TRAVERSAL_TOW_MAX_TICKS,
            TowArrowConfig.MAX_TICKS_MIN,
            TowArrowConfig.MAX_TICKS_MAX,
            config -> config.traversal().tow().maxTicks(),
            (config, value) -> tow(config, it -> it.withMaxTicks(value))),
        new IntOption<>(
            ConfigSettings.TRAVERSAL_UPDRAFT_HEIGHT_BLOCKS,
            UpdraftArrowConfig.HEIGHT_BLOCKS_MIN,
            UpdraftArrowConfig.HEIGHT_BLOCKS_MAX,
            config -> config.traversal().updraft().heightBlocks(),
            (config, value) -> updraft(config, it -> it.withHeightBlocks(value))),
        new IntOption<>(
            ConfigSettings.TRAVERSAL_UPDRAFT_LIFETIME_TICKS,
            UpdraftArrowConfig.LIFETIME_TICKS_MIN,
            UpdraftArrowConfig.LIFETIME_TICKS_MAX,
            config -> config.traversal().updraft().lifetimeTicks(),
            (config, value) -> updraft(config, it -> it.withLifetimeTicks(value))),
        new FloatOption<>(
            ConfigSettings.TRAVERSAL_UPDRAFT_STRENGTH,
            UpdraftArrowConfig.STRENGTH_MIN,
            UpdraftArrowConfig.STRENGTH_MAX,
            VELOCITY_STEP,
            config -> config.traversal().updraft().strength(),
            (config, value) -> updraft(config, it -> it.withStrength(value))),
        new IntOption<>(
            ConfigSettings.TRAVERSAL_VINE_LENGTH_BLOCKS,
            VineArrowConfig.LENGTH_BLOCKS_MIN,
            VineArrowConfig.LENGTH_BLOCKS_MAX,
            config -> config.traversal().vine().lengthBlocks(),
            (config, value) -> vine(config, it -> it.withLengthBlocks(value))),
        new FloatOption<>(
            ConfigSettings.TRAVERSAL_TRAMPOLINE_STRENGTH,
            TrampolineArrowConfig.STRENGTH_MIN,
            TrampolineArrowConfig.STRENGTH_MAX,
            VELOCITY_STEP,
            config -> config.traversal().trampoline().strength(),
            (config, value) -> trampoline(config, it -> it.withStrength(value))),
        new IntOption<>(
            ConfigSettings.TRAVERSAL_TRAMPOLINE_LIFETIME_TICKS,
            TrampolineArrowConfig.LIFETIME_TICKS_MIN,
            TrampolineArrowConfig.LIFETIME_TICKS_MAX,
            config -> config.traversal().trampoline().lifetimeTicks(),
            (config, value) -> trampoline(config, it -> it.withLifetimeTicks(value))),
        new IntOption<>(
            ConfigSettings.TRAVERSAL_SCAFFOLD_HEIGHT_BLOCKS,
            ScaffoldArrowConfig.HEIGHT_BLOCKS_MIN,
            ScaffoldArrowConfig.HEIGHT_BLOCKS_MAX,
            config -> config.traversal().scaffold().heightBlocks(),
            (config, value) -> scaffold(config, it -> it.withHeightBlocks(value))),
        new IntOption<>(
            ConfigSettings.TRAVERSAL_SCAFFOLD_LIFETIME_TICKS,
            ScaffoldArrowConfig.LIFETIME_TICKS_MIN,
            ScaffoldArrowConfig.LIFETIME_TICKS_MAX,
            config -> config.traversal().scaffold().lifetimeTicks(),
            (config, value) -> scaffold(config, it -> it.withLifetimeTicks(value))),
        new IntOption<>(
            ConfigSettings.TRAVERSAL_BRIDGE_LENGTH_BLOCKS,
            BridgeArrowConfig.LENGTH_BLOCKS_MIN,
            BridgeArrowConfig.LENGTH_BLOCKS_MAX,
            config -> config.traversal().bridge().lengthBlocks(),
            (config, value) -> bridge(config, it -> it.withLengthBlocks(value))),
        new IntOption<>(
            ConfigSettings.TRAVERSAL_BRIDGE_LIFETIME_TICKS,
            BridgeArrowConfig.LIFETIME_TICKS_MIN,
            BridgeArrowConfig.LIFETIME_TICKS_MAX,
            config -> config.traversal().bridge().lifetimeTicks(),
            (config, value) -> bridge(config, it -> it.withLifetimeTicks(value))));
  }

  private static NotEnoughArrowsConfig zipline(
      final NotEnoughArrowsConfig config,
      final Function<ZiplineArrowConfig, ZiplineArrowConfig> change) {
    return traversal(config, it -> it.withZipline(change.apply(it.zipline())));
  }

  private static NotEnoughArrowsConfig tow(
      final NotEnoughArrowsConfig config, final Function<TowArrowConfig, TowArrowConfig> change) {
    return traversal(config, it -> it.withTow(change.apply(it.tow())));
  }

  private static NotEnoughArrowsConfig updraft(
      final NotEnoughArrowsConfig config,
      final Function<UpdraftArrowConfig, UpdraftArrowConfig> change) {
    return traversal(config, it -> it.withUpdraft(change.apply(it.updraft())));
  }

  private static NotEnoughArrowsConfig vine(
      final NotEnoughArrowsConfig config, final Function<VineArrowConfig, VineArrowConfig> change) {
    return traversal(config, it -> it.withVine(change.apply(it.vine())));
  }

  private static NotEnoughArrowsConfig trampoline(
      final NotEnoughArrowsConfig config,
      final Function<TrampolineArrowConfig, TrampolineArrowConfig> change) {
    return traversal(config, it -> it.withTrampoline(change.apply(it.trampoline())));
  }

  private static NotEnoughArrowsConfig scaffold(
      final NotEnoughArrowsConfig config,
      final Function<ScaffoldArrowConfig, ScaffoldArrowConfig> change) {
    return traversal(config, it -> it.withScaffold(change.apply(it.scaffold())));
  }

  private static NotEnoughArrowsConfig bridge(
      final NotEnoughArrowsConfig config,
      final Function<BridgeArrowConfig, BridgeArrowConfig> change) {
    return traversal(config, it -> it.withBridge(change.apply(it.bridge())));
  }

  private static NotEnoughArrowsConfig traversal(
      final NotEnoughArrowsConfig config,
      final Function<TraversalArrowConfig, TraversalArrowConfig> change) {
    return config.withTraversal(change.apply(config.traversal()));
  }
}
