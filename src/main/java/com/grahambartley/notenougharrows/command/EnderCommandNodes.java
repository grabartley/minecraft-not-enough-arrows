package com.grahambartley.notenougharrows.command;

import com.grahambartley.notenougharrows.config.option.EnderOptions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public final class EnderCommandNodes {
  private EnderCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return OptionCommandNodes.section(EnderOptions.section());
  }
}
