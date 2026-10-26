package com.grahambartley.volley.command;

import com.grahambartley.volley.config.option.EnderOptions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public final class EnderCommandNodes {
  private EnderCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return OptionCommandNodes.section(EnderOptions.section());
  }
}
