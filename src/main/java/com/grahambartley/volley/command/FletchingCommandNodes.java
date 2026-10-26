package com.grahambartley.volley.command;

import com.grahambartley.volley.config.option.FletchingOptions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public final class FletchingCommandNodes {
  private FletchingCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return OptionCommandNodes.section(FletchingOptions.section());
  }
}
