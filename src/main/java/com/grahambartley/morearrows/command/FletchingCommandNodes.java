package com.grahambartley.morearrows.command;

import com.grahambartley.morearrows.config.ConfigSettings;
import com.grahambartley.morearrows.config.FletchingStationConfig;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.function.UnaryOperator;
import net.minecraft.server.command.ServerCommandSource;

public final class FletchingCommandNodes {
  private FletchingCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return ConfigOptionNodes.group(ConfigSettings.FLETCHING)
        .then(
            ConfigOptionNodes.booleanOption(
                ConfigSettings.FLETCHING_STATION_ENABLED,
                (current, value) ->
                    change(current, fletching -> fletching.withStationEnabled(value))));
  }

  private static MoreArrowsConfig change(
      final MoreArrowsConfig current, final UnaryOperator<FletchingStationConfig> change) {
    return current.withFletching(change.apply(current.fletching()));
  }
}
