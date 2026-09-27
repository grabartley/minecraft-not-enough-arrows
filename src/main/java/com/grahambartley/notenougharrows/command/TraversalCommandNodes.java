package com.grahambartley.notenougharrows.command;

import com.grahambartley.notenougharrows.config.option.TraversalOptions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public final class TraversalCommandNodes {
  private TraversalCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return OptionCommandNodes.section(TraversalOptions.section());
  }
}
