package com.grahambartley.morearrows.command;

import com.grahambartley.morearrows.config.ConfigSettings;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.grahambartley.morearrows.config.UtilityArrowConfig;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.function.UnaryOperator;
import net.minecraft.server.command.ServerCommandSource;

public final class UtilityCommandNodes {
  private UtilityCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return ConfigOptionNodes.group(ConfigSettings.UTILITY)
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.UTILITY_GLOW_DURATION_TICKS,
                UtilityArrowConfig.GLOW_DURATION_TICKS_MIN,
                UtilityArrowConfig.GLOW_DURATION_TICKS_MAX,
                (current, value) ->
                    change(current, utility -> utility.withGlowDurationTicks(value))))
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.UTILITY_REDSTONE_SIGNAL_DURATION_TICKS,
                UtilityArrowConfig.REDSTONE_SIGNAL_DURATION_TICKS_MIN,
                UtilityArrowConfig.REDSTONE_SIGNAL_DURATION_TICKS_MAX,
                (current, value) ->
                    change(current, utility -> utility.withRedstoneSignalDurationTicks(value))))
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.UTILITY_REDSTONE_SIGNAL_STRENGTH,
                UtilityArrowConfig.REDSTONE_SIGNAL_STRENGTH_MIN,
                UtilityArrowConfig.REDSTONE_SIGNAL_STRENGTH_MAX,
                (current, value) ->
                    change(current, utility -> utility.withRedstoneSignalStrength(value))))
        .then(
            ConfigOptionNodes.floatOption(
                ConfigSettings.UTILITY_WIND_BURST_RADIUS,
                UtilityArrowConfig.WIND_BURST_RADIUS_MIN,
                UtilityArrowConfig.WIND_BURST_RADIUS_MAX,
                (current, value) -> change(current, utility -> utility.withWindBurstRadius(value))))
        .then(
            ConfigOptionNodes.floatOption(
                ConfigSettings.UTILITY_WIND_PUSH_STRENGTH,
                UtilityArrowConfig.WIND_PUSH_STRENGTH_MIN,
                UtilityArrowConfig.WIND_PUSH_STRENGTH_MAX,
                (current, value) ->
                    change(current, utility -> utility.withWindPushStrength(value))));
  }

  private static MoreArrowsConfig change(
      final MoreArrowsConfig current, final UnaryOperator<UtilityArrowConfig> change) {
    return current.withUtility(change.apply(current.utility()));
  }
}
