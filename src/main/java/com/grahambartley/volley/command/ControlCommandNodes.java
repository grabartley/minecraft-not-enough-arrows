package com.grahambartley.volley.command;

import com.grahambartley.volley.config.option.ControlOptions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public final class ControlCommandNodes {
  private ControlCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return OptionCommandNodes.section(ControlOptions.section());
  }
}
