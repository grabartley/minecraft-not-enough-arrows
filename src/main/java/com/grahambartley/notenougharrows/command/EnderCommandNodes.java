package com.grahambartley.notenougharrows.command;

import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.EnderArrowConfig;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
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

  private static NotEnoughArrowsConfig change(
      final NotEnoughArrowsConfig current, final UnaryOperator<EnderArrowConfig> change) {
    return current.withEnder(change.apply(current.ender()));
  }
}
