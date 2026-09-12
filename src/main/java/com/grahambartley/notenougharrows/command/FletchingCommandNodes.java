package com.grahambartley.notenougharrows.command;

import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.FletchingStationConfig;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
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

  private static NotEnoughArrowsConfig change(
      final NotEnoughArrowsConfig current, final UnaryOperator<FletchingStationConfig> change) {
    return current.withFletching(change.apply(current.fletching()));
  }
}
