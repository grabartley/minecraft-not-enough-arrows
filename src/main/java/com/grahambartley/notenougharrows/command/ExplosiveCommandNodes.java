package com.grahambartley.notenougharrows.command;

import com.grahambartley.notenougharrows.config.option.ExplosiveOptions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public final class ExplosiveCommandNodes {
  private ExplosiveCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return OptionCommandNodes.section(ExplosiveOptions.section());
  }
}
