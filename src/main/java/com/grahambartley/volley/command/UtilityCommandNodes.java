package com.grahambartley.volley.command;

import com.grahambartley.volley.config.option.UtilityOptions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public final class UtilityCommandNodes {
  private UtilityCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return OptionCommandNodes.section(UtilityOptions.section());
  }
}
