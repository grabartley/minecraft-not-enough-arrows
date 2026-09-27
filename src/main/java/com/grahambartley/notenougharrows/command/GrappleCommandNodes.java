package com.grahambartley.notenougharrows.command;

import com.grahambartley.notenougharrows.config.option.GrappleOptions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public final class GrappleCommandNodes {
  private GrappleCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return OptionCommandNodes.section(GrappleOptions.section());
  }
}
