package com.grahambartley.notenougharrows.command;

import com.grahambartley.notenougharrows.config.option.DiscoveryOptions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public final class DiscoveryCommandNodes {
  private DiscoveryCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return OptionCommandNodes.section(DiscoveryOptions.section());
  }
}
