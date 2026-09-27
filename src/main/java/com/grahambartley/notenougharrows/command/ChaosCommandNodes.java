package com.grahambartley.notenougharrows.command;

import com.grahambartley.notenougharrows.config.option.ChaosOptions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public final class ChaosCommandNodes {
  private ChaosCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return OptionCommandNodes.section(ChaosOptions.section());
  }
}
