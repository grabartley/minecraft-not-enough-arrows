package com.grahambartley.notenougharrows.command;

import com.grahambartley.notenougharrows.config.option.AgricultureOptions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public final class AgricultureCommandNodes {
  private AgricultureCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return OptionCommandNodes.section(AgricultureOptions.section());
  }
}
