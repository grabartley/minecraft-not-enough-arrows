package com.grahambartley.morearrows.config.option;

import com.grahambartley.morearrows.config.ConfigSettings;
import com.grahambartley.morearrows.config.GrappleArrowConfig;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import java.util.List;
import java.util.function.Function;

public final class GrappleOptions {
  public static final float PULL_SPEED_STEP = 0.05f;
  public static final float PULL_ACCELERATION_STEP = 0.01f;

  private GrappleOptions() {}

  private static final List<ConfigOption<MoreArrowsConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<MoreArrowsConfig> SECTION =
      new ConfigSection<>(ConfigSettings.GRAPPLE, OPTIONS);

  public static ConfigSection<MoreArrowsConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<MoreArrowsConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<MoreArrowsConfig>> buildOptions() {
    return List.of(
        new IntOption<>(
            ConfigSettings.GRAPPLE_MAX_RANGE_BLOCKS,
            GrappleArrowConfig.MAX_RANGE_BLOCKS_MIN,
            GrappleArrowConfig.MAX_RANGE_BLOCKS_MAX,
            config -> config.grapple().maxRangeBlocks(),
            (config, value) -> grapple(config, it -> it.withMaxRangeBlocks(value))),
        new FloatOption<>(
            ConfigSettings.GRAPPLE_PULL_SPEED,
            GrappleArrowConfig.PULL_SPEED_MIN,
            GrappleArrowConfig.PULL_SPEED_MAX,
            PULL_SPEED_STEP,
            config -> config.grapple().pullSpeed(),
            (config, value) -> grapple(config, it -> it.withPullSpeed(value))),
        new FloatOption<>(
            ConfigSettings.GRAPPLE_PULL_ACCELERATION,
            GrappleArrowConfig.PULL_ACCELERATION_MIN,
            GrappleArrowConfig.PULL_ACCELERATION_MAX,
            PULL_ACCELERATION_STEP,
            config -> config.grapple().pullAcceleration(),
            (config, value) -> grapple(config, it -> it.withPullAcceleration(value))),
        new BooleanOption<>(
            ConfigSettings.GRAPPLE_CANCEL_FALL_DAMAGE_ON_ARRIVAL,
            config -> config.grapple().cancelFallDamageOnArrival(),
            (config, value) -> grapple(config, it -> it.withCancelFallDamageOnArrival(value))),
        new BooleanOption<>(
            ConfigSettings.GRAPPLE_RETURN_ARROW_ON_ARRIVAL,
            config -> config.grapple().returnArrowOnArrival(),
            (config, value) -> grapple(config, it -> it.withReturnArrowOnArrival(value))),
        new IntOption<>(
            ConfigSettings.GRAPPLE_ROPE_LENGTH_BLOCKS,
            GrappleArrowConfig.ROPE_LENGTH_BLOCKS_MIN,
            GrappleArrowConfig.ROPE_LENGTH_BLOCKS_MAX,
            config -> config.grapple().ropeLengthBlocks(),
            (config, value) -> grapple(config, it -> it.withRopeLengthBlocks(value))),
        new BooleanOption<>(
            ConfigSettings.GRAPPLE_ROPES_DECAY,
            config -> config.grapple().ropesDecay(),
            (config, value) -> grapple(config, it -> it.withRopesDecay(value))));
  }

  private static MoreArrowsConfig grapple(
      final MoreArrowsConfig config,
      final Function<GrappleArrowConfig, GrappleArrowConfig> change) {
    return config.withGrapple(change.apply(config.grapple()));
  }
}
