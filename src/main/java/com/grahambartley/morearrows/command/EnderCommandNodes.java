package com.grahambartley.morearrows.command;

import com.grahambartley.morearrows.config.ConfigSettings;
import com.grahambartley.morearrows.config.EnderArrowConfig;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.function.UnaryOperator;
import net.minecraft.server.command.ServerCommandSource;

public final class EnderCommandNodes {
  private EnderCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return ConfigOptionNodes.group(ConfigSettings.ENDER)
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.ENDER_PEARL_MAX_RANGE_BLOCKS,
                EnderArrowConfig.PEARL_MAX_RANGE_BLOCKS_MIN,
                EnderArrowConfig.PEARL_MAX_RANGE_BLOCKS_MAX,
                (current, value) -> change(current, ender -> ender.withPearlMaxRangeBlocks(value))))
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.ENDER_RECALL_MAX_RANGE_BLOCKS,
                EnderArrowConfig.RECALL_MAX_RANGE_BLOCKS_MIN,
                EnderArrowConfig.RECALL_MAX_RANGE_BLOCKS_MAX,
                (current, value) ->
                    change(current, ender -> ender.withRecallMaxRangeBlocks(value))))
        .then(
            ConfigOptionNodes.booleanOption(
                ConfigSettings.ENDER_RECALL_AFFECTS_PLAYERS,
                (current, value) ->
                    change(current, ender -> ender.withRecallAffectsPlayers(value))));
  }

  private static MoreArrowsConfig change(
      final MoreArrowsConfig current, final UnaryOperator<EnderArrowConfig> change) {
    return current.withEnder(change.apply(current.ender()));
  }
}
